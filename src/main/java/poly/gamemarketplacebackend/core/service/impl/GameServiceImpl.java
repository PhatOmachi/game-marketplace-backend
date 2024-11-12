package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Category;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.mapper.GameMapper;
import poly.gamemarketplacebackend.core.repository.GameRepository;
import poly.gamemarketplacebackend.core.service.GameService;
import poly.gamemarketplacebackend.core.util.GameSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

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
}
