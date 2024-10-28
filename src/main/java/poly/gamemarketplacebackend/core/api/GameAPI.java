package poly.gamemarketplacebackend.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.Base64;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.CategoryDTO;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.dto.MediaDTO;
import poly.gamemarketplacebackend.core.entity.Category;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.service.CategoryDetailService;
import poly.gamemarketplacebackend.core.service.CategoryService;
import poly.gamemarketplacebackend.core.service.GameService;
import poly.gamemarketplacebackend.core.service.MediaService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameAPI {
    private final GameService gameService;
    private final CategoryDetailService categoryDetailService;
    private final CategoryService categoryService;
    private final MediaService mediaService;

    @GetMapping("")
    public ResponseObject<?> getAllGame() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.getAllGame())
                .build();
    }

    @PostMapping("")
    public ResponseObject<?> createGame(@RequestBody GameDTO gameDTO) {
        // Bước 1: Lưu thông tin game trước
        Game game;
        try {
            GameDTO gameDTO1 = new GameDTO();
            gameDTO1.setGameName(gameDTO.getGameName());
            gameDTO1.setPrice(gameDTO.getPrice());
            gameDTO1.setDiscountPercent(gameDTO.getDiscountPercent());
            gameDTO1.setQuantity(gameDTO.getQuantity());
            gameDTO1.setStatus(gameDTO.getStatus());
            gameDTO1.setDescription(gameDTO.getDescription());
            gameDTO1.setSlug(gameDTO.getSlug());

            // Lưu game và cập nhật ID vào gameDTO
            game = gameService.saveGame(gameDTO1);
            gameDTO.setSysIdGame(game.getSysIdGame()); // Cập nhật sysIdGame
            System.out.println("Id cua game: " + gameDTO.getSysIdGame());
        } catch (Exception e) {
            log.error("Error saving game: {}", e.getMessage());
            return ResponseObject.builder()
                    .data(gameDTO)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed to save game")
                    .build();
        }

        // Bước 2: Kiểm tra và lưu media
        if (gameDTO.getMedia() == null || gameDTO.getMedia().isEmpty()) {
            return ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Media list cannot be empty")
                    .build();
        }

        // Tạo thư mục riêng cho game
        File gameDir = new File("src/main/resources/static/images/" + game.getSysIdGame());
        if (!gameDir.exists()) {
            gameDir.mkdirs();
            log.info("Created directory for game {}: {}", game.getSysIdGame(), gameDir.getAbsolutePath());
        }

        List<MediaDTO> mediaList = new ArrayList<>();
        for (MediaDTO mediaDTO : gameDTO.getMedia()) {
            if (mediaDTO.getMediaUrl() != null && mediaDTO.getMediaUrl().contains(",")) {
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(mediaDTO.getMediaUrl().split(",")[1]);
                    String filePath = gameDir.getAbsolutePath() + "/" + mediaDTO.getMediaName() + ".jpg"; // Đường dẫn tệp

                    // Lưu ảnh vào thư mục của game
                    try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                        fos.write(decodedBytes);
                        mediaDTO.setMediaUrl("http://localhost:9999/images/" + game.getSysIdGame() + "/" + mediaDTO.getMediaName() + ".jpg"); // Cập nhật URL hình ảnh đã lưu
                        log.info("Image saved successfully at {}", filePath);
                    }
                } catch (IOException e) {
                    log.error("Error saving image for media {}: {}", mediaDTO.getMediaName(), e.getMessage());
                    return ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Failed to save image: " + mediaDTO.getMediaName())
                            .build();
                } catch (IllegalArgumentException e) {
                    log.error("Invalid base64 string for media {}: {}", mediaDTO.getMediaName(), e.getMessage());
                    return ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Invalid base64 string for media: " + mediaDTO.getMediaName())
                            .build();
                }
            } else {
                log.warn("Media URL is null or invalid for media {}", mediaDTO.getMediaName());
                return ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Media URL is null or invalid for media: " + mediaDTO.getMediaName())
                        .build();
            }
            mediaList.add(mediaDTO);
            mediaDTO.setSysIdGame(game.getSysIdGame()); // Gán sysIdGame
        }
        gameDTO.setMedia(mediaList);

        // Lưu media vào database
        try {
            for (MediaDTO mediaDTO : mediaList) {
                mediaService.saveMedia(mediaDTO.getMediaName(), mediaDTO.getMediaUrl(), mediaDTO.getSysIdGame());
            }
        } catch (Exception e) {
            log.error("Error saving media for game {}: {}", gameDTO.getGameName(), e.getMessage());
            return ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed to save media for game: " + gameDTO.getGameName())
                    .build();
        }

        // Bước 3: Kiểm tra và lưu category detail với hai trường sysIdCategory và sysIdGame
        if (gameDTO.getCategoryDetails() != null) {
            for (CategoryDetailDTO categoryDetailDTO : gameDTO.getCategoryDetails()) {
                categoryDetailDTO.setSysIdGame(game.getSysIdGame()); // Gán sysIdGame từ game đã lưu
                try {
                    // Gọi phương thức chỉ lưu sysIdCategory và sysIdGame
                    categoryDetailService.insertCategoryDetail(categoryDetailDTO.getSysIdCategory(), game.getSysIdGame());
                } catch (Exception e) {
                    log.error("Error saving category detail for ID {}: {}", categoryDetailDTO.getSysIdCategory(), e.getMessage());
                    return ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Failed to save category detail for ID: " + categoryDetailDTO.getSysIdCategory())
                            .build();
                }
            }
        }

        return ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("Create game successfully")
                .data(game) // Trả về thông tin game đã lưu
                .build();
    }


    @DeleteMapping("")
    public ResponseObject<?> deleteGame(@RequestBody GameDTO gameDTO) {
        gameService.deleteGame(gameDTO);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Delete game successfully")
                .build();
    }

    @GetMapping("/{slug}")
    public ResponseObject<?> getGameBySlug(@PathVariable String slug) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.findBySlug(slug))
                .build();
    }

    @GetMapping("/p/sort")
    public ResponseObject<?> getGamesByFieldDesc(@RequestParam String field, @RequestParam int page, @RequestParam int size) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.getGamesByFieldDesc(field, page, size))
                .build();
    }

    @GetMapping("/p/top-limited-discount")
    public ResponseObject<?> getTopGamesByVoucherEndDateNearest(@RequestParam int page, @RequestParam int size) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.getTopGamesByVoucherEndDateNearest(page, size))
                .build();
    }

    @PostMapping("/valid-cart-items")
    public ResponseObject<?> isValidCartItems(@RequestBody List<GameDTO> cartItems) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.isValidCartItems(cartItems))
                .build();
    }

    @GetMapping("/browser")
    public Page<Game> searchGames(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String minRatingStr,
            @RequestParam(required = false) String maxRatingStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return gameService.searchGames(name, minPrice, maxPrice, category, minRatingStr, maxRatingStr, pageable);
    }
}
