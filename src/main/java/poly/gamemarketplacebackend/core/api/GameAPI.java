package poly.gamemarketplacebackend.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Base64;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.CategoryDTO;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.dto.MediaDTO;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.service.CategoryDetailService;
import poly.gamemarketplacebackend.core.service.CategoryService;
import poly.gamemarketplacebackend.core.service.GameService;

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

    @GetMapping("")
    public ResponseObject<?> getAllGame() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.getAllGame())
                .build();
    }

    @PostMapping("")
    public ResponseObject<?> createGame(@RequestBody GameDTO gameDTO) {
        // Kiểm tra xem gameDTO có media không
        if (gameDTO.getMedia() == null || gameDTO.getMedia().isEmpty()) {
            return ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Media list cannot be empty")
                    .build();
        }

        List<MediaDTO> mediaList = new ArrayList<>();

        // Tạo thư mục lưu hình ảnh nếu không tồn tại
        File dir = new File("src/main/resources/static/images");
        if (!dir.exists()) {
            dir.mkdirs(); // Tạo thư mục
            log.info("Created directory: {}", dir.getAbsolutePath());
        }

        // Lưu từng ảnh từ base64
        for (MediaDTO mediaDTO : gameDTO.getMedia()) {
            if (mediaDTO.getMediaUrl() != null && mediaDTO.getMediaUrl().contains(",")) {
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(mediaDTO.getMediaUrl().split(",")[1]);
                    String filePath = "src/main/resources/static/images/" + mediaDTO.getMediaName() + "_" + gameDTO.getSysIdGame() + ".jpg";

                    try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                        fos.write(decodedBytes);
                        mediaDTO.setMediaUrl(filePath); // Cập nhật URL ảnh đã lưu
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
        }

        // Cập nhật danh sách media vào gameDTO
        gameDTO.setMedia(mediaList);


//        GameDTO savedGameDTO;
        try {
            Game game = gameService.saveGame(gameDTO);
//            savedGameDTO = gameService.findBySlug(game.getSlug());// Lưu game và lấy đối tượng đã lưu
        } catch (Exception e) {
            log.error("Error saving game: {}", e.getMessage());
            return ResponseObject.builder()
                    .data(gameDTO)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed to save game")
                    .build();
        }

        return ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("Create game successfully")
                .build();

//        // Kiểm tra và lưu danh sách category detail
//        if (gameDTO.getCategoryDetails() != null) {
//            for (CategoryDetailDTO categoryDetailDTO : gameDTO.getCategoryDetails()) {
//                // Thiết lập sysIdGame từ game đã lưu
//                categoryDetailDTO.setSysIdGame(savedGameDTO.getSysIdGame());
//                System.out.println(">>>>>00" + categoryDetailDTO.getSysIdGame());
//                // Lưu category detail
//                try {
//                    categoryDetailService.saveCategoryDetail(categoryDetailDTO);
//                } catch (Exception e) {
//                    log.error("Error saving category detail for ID {}: {}", categoryDetailDTO.getSysIdCategory(), e.getMessage());
//                    return ResponseObject.builder()
//                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .message("Failed to save category detail for ID: " + categoryDetailDTO.getSysIdCategory())
//                            .build();
//                }
//            }
//        }

//        return ResponseObject.builder()
//                .status(HttpStatus.CREATED)
//                .data(savedGameDTO)
//                .build();
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
}
