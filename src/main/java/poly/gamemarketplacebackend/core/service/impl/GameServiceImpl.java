package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.dto.MediaDTO;
import poly.gamemarketplacebackend.core.entity.Category;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.mapper.GameMapper;
import poly.gamemarketplacebackend.core.repository.GameRepository;
import poly.gamemarketplacebackend.core.service.CategoryDetailService;
import poly.gamemarketplacebackend.core.service.GameService;
import poly.gamemarketplacebackend.core.service.MediaService;
import poly.gamemarketplacebackend.core.util.GameSpecification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final MediaService mediaService;
    private final CategoryDetailService categoryDetailService;

    @Override
    public List<GameDTO> getAllGame() {
        return gameMapper.toDTOs(gameRepository.findAll());
    }

    @Override
    public List<GameDTO> getTop10RecommendedGames(String slug) {
        Optional<Game> gameOptional = gameRepository.findBySlug(slug);
        Game game = gameOptional.orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<Integer> categoryIds = game.getCategoryDetails().stream()
                .map(CategoryDetail::getCategory)
                .map(Category::getSysIdCategory)
                .collect(Collectors.toList());
        List<Game> relatedGames = gameRepository.findRelatedGames(categoryIds, game.getSysIdGame());
        return relatedGames.stream().limit(3).map(gameMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Game saveGame(GameDTO gameDTO) {
        return gameRepository.save(gameMapper.toEntity(gameDTO));
    }

    @Override
    public void deleteGame(GameDTO gameDTO) {
        gameRepository.delete(gameMapper.toEntity(gameDTO));
    }

    @Override
    public GameDTO findBySlug(String slug) {
        var game = gameRepository.findBySlug(slug);
        return game.map(gameMapper::toDTO).orElse(null);
    }

    @Override
    public GameDTO findById(Integer sysIdGame) {
        return gameMapper.toDTO(gameRepository.findById(sysIdGame).get());
    }

    public String saveFile(String uploadDir, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get("src/main/resources/static/" + uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return "/static/" + uploadDir + "/" + fileName;
    }

    @Override
    public List<GameDTO> getGamesByFieldDesc(String field, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, field));
        Page<Game> gamePage = gameRepository.findAll(pageable);
        return gameMapper.toDTOs(gamePage.getContent());
    }

    @Override
    public List<GameDTO> getTopGamesByVoucherEndDateNearest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "quantity"));
        Page<Game> gamePage = gameRepository.findAll(pageable);
        return gameMapper.toDTOs(gamePage.getContent());
    }

    @Override
    public List<CartItemDTO> isValidCartItems(List<GameDTO> cartItems) {
        var err = new ArrayList<CartItemDTO>();
        for (GameDTO cartItem : cartItems) {
            var game = gameRepository.findBySlug(cartItem.getSlug());
            if (game.isEmpty()) {
                err.add(CartItemDTO.builder()
                        .slug(cartItem.getSlug())
                        .message("Game " + cartItem.getSlug() + " not found")
                        .build());
            } else if (game.get().getQuantity() < cartItem.getQuantity()) {
                err.add(CartItemDTO.builder()
                        .slug(cartItem.getSlug())
                        .message("Game " + cartItem.getSlug() + " only has " + game.get().getQuantity() + " left")
                        .build());
            }
        }
        return err;
    }

    @Override
    public Page<Game> searchGames(String name, Double minPrice, Double maxPrice, String category, String minRatingStr, String maxRatingStr, Pageable pageable) {
        Specification<Game> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(GameSpecification.hasName(name));
        }
        if (minPrice != null && maxPrice != null) {
            spec = spec.and(GameSpecification.hasPriceBetween(minPrice, maxPrice));
        }
        if (category != null && !category.isEmpty()) {
            spec = spec.and(GameSpecification.hasCategory(category));
        }
        if (minRatingStr != null && maxRatingStr != null) {
            spec = spec.and(GameSpecification.hasRatingBetween(minRatingStr, maxRatingStr));
        }

        return gameRepository.findAll(spec, pageable);
    }

    @Override
    public ResponseObject<?> saveOrUpdateGame(GameDTO gameDTO, boolean isUpdate) {
        Game game;
        try {
            game = saveGameInfo(gameDTO, isUpdate);
            gameDTO.setSysIdGame(game.getSysIdGame());
        } catch (Exception e) {
            log.error("Error saving game: {}", e.getMessage());
            return ResponseObject.builder()
                    .data(gameDTO)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed to save game")
                    .build();
        }

        if (gameDTO.getMedia() == null || gameDTO.getMedia().isEmpty()) {
            return ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Media list cannot be empty")
                    .build();
        }

        try {
            if (isUpdate) {
                // Xóa các hình ảnh cũ
                deleteOldMedia(gameDTO, game);
            }
            saveMedia(gameDTO, game);
        } catch (Exception e) {
            return ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }

        try {
            saveCategoryDetails(gameDTO, game);
        } catch (Exception e) {
            return ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }

        return ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message(isUpdate ? "Update game successfully" : "Create game successfully")
                .data(game)
                .build();
    }

    private void deleteOldMedia(GameDTO gameDTO, Game game) throws Exception {
        List<MediaDTO> existingMedia = mediaService.getMediaByGameId(game.getSysIdGame());
        for (MediaDTO mediaDTO : existingMedia) {
            deleteImage(mediaDTO.getMediaUrl());
        }
    }

    private Game saveGameInfo(GameDTO gameDTO, boolean isUpdate) throws Exception {
        GameDTO gameDTO1 = new GameDTO();
        gameDTO1.setGameName(gameDTO.getGameName());
        gameDTO1.setGameCode(gameDTO.getGameCode());
        gameDTO1.setPrice(gameDTO.getPrice());
        gameDTO1.setDiscountPercent(gameDTO.getDiscountPercent());
        gameDTO1.setQuantity(gameDTO.getQuantity());
        gameDTO1.setIsActive(gameDTO.getIsActive());
        gameDTO1.setDescription(gameDTO.getDescription());
        gameDTO1.setSlug(gameDTO.getSlug());
        gameDTO1.setReleaseDate(gameDTO.getReleaseDate());

        if (isUpdate) {
            gameDTO1.setSysIdGame(gameDTO.getSysIdGame());
        }

        return saveGame(gameDTO1);
    }

    private void saveMedia(GameDTO gameDTO, Game game) throws Exception {
        File gameDir = new File("src/main/resources/static/images/" + game.getSysIdGame());
        if (!gameDir.exists()) {
            gameDir.mkdirs();
            log.info("Created directory for game {}: {}", game.getSysIdGame(), gameDir.getAbsolutePath());
        }

        List<MediaDTO> mediaList = new ArrayList<>();
        int imageIndex = 1;

        // Lưu trữ các hình ảnh mới
        for (MediaDTO mediaDTO : gameDTO.getMedia()) {
            if (mediaDTO.getMediaUrl() != null && mediaDTO.getMediaUrl().contains(",")) {
                // Handle base64 encoded image
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(mediaDTO.getMediaUrl().split(",")[1]);
                    String newFileName = mediaDTO.getMediaName() + ".jpg";
                    String filePath = gameDir.getAbsolutePath() + "/" + newFileName;

                    try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                        fos.write(decodedBytes);
                        mediaDTO.setMediaUrl(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/images/" + game.getSysIdGame() + "/" + newFileName);
                        log.info("Image saved successfully at {}", filePath);
                    }
                } catch (IOException | IllegalArgumentException e) {
                    log.error("Error saving image for media {}: {}", mediaDTO.getMediaName(), e.getMessage());
                    throw new Exception("Failed to save image: " + mediaDTO.getMediaName());
                }
            } else if (mediaDTO.getMediaUrl() != null) {
                // Handle URL
                log.info("Media URL is a valid URL for media {}", mediaDTO.getMediaName());
            } else {
                log.warn("Media URL is null or invalid for media {}", mediaDTO.getMediaName());
                throw new Exception("Media URL is null or invalid for media: " + mediaDTO.getMediaName());
            }
            mediaDTO.setSysIdGame(game.getSysIdGame());
            mediaList.add(mediaDTO);
        }

        // Sắp xếp lại các hình ảnh mới từ đầu và thay đổi tên
        for (MediaDTO mediaDTO : mediaList) {
            if (!mediaDTO.getMediaName().equals("thumbnail") && !mediaDTO.getMediaName().equals("logo")) {
                mediaDTO.setMediaName("p" + imageIndex++);
                mediaDTO.setMediaUrl(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/images/" + game.getSysIdGame() + "/" + mediaDTO.getMediaName() + ".jpg");
            }
            mediaService.saveMedia(mediaDTO.getMediaName(), mediaDTO.getMediaUrl(), mediaDTO.getSysIdGame());
        }

        gameDTO.setMedia(mediaList);
    }

    @Override
    public void deleteImage(String imageUrl) {
        // Logic to delete image from server
        File file = new File("src/main/resources/static" + imageUrl);
        if (file.exists()) {
            file.delete();
            log.info("Deleted image: {}", imageUrl);
        } else {
            log.warn("Image not found: {}", imageUrl);
        }

        // Logic to delete image from database
        mediaService.deleteMediaByUrl(imageUrl);
    }

    private void saveCategoryDetails(GameDTO gameDTO, Game game) throws Exception {
        if (gameDTO.getCategoryDetails() != null) {
            // Delete existing category details
            categoryDetailService.deleteCategoryDetailsByGameId(game.getSysIdGame());

            // Insert new category details
            for (CategoryDetailDTO categoryDetailDTO : gameDTO.getCategoryDetails()) {
                categoryDetailDTO.setSysIdGame(game.getSysIdGame());
                try {
                    categoryDetailService.insertCategoryDetail(categoryDetailDTO.getSysIdCategory(), game.getSysIdGame());
                } catch (Exception e) {
                    log.error("Error saving category detail for ID {}: {}", categoryDetailDTO.getSysIdCategory(), e.getMessage());
                    throw new Exception("Failed to save category detail for ID: " + categoryDetailDTO.getSysIdCategory());
                }
            }
        }
    }
}
