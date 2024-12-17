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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.CategoryDTO;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.dto.MediaDTO;
import poly.gamemarketplacebackend.core.entity.Category;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.repository.GameRepository;
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

    @GetMapping
    public ResponseObject<?> getAllGame() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.getAllGame())
                .build();
    }

    @PostMapping
    public ResponseObject<?> createGame(@RequestBody GameDTO gameDTO) {
        return gameService.saveOrUpdateGame(gameDTO, false);
    }

    @PutMapping("/{id}")
    public ResponseObject<?> updateGame(@PathVariable Integer id, @RequestBody GameDTO gameDTO) {
        gameDTO.setSysIdGame(id);
        return gameService.saveOrUpdateGame(gameDTO, true);
    }

    @DeleteMapping
    public ResponseObject<?> deleteGame(@RequestBody GameDTO gameDTO) {
        gameService.deleteGame(gameDTO);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Delete game successfully")
                .build();
    }

    @GetMapping("/p/{slug}")
    public ResponseObject<?> getGameBySlug(@PathVariable String slug) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.findBySlug(slug))
                .build();
    }

    @GetMapping("/p/{slug}/recommendations")
    public ResponseObject<?> getTop10RecommendedGames(@PathVariable String slug) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.getTop10RecommendedGames(slug))
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

    @GetMapping("/p/browser")
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

    @GetMapping("/id")
    public ResponseObject<?> findByid(@RequestParam("id") Integer id){
        GameDTO gameDTO = gameService.findById(id);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameDTO)
                .build();
    }

    @GetMapping("/top-selling")
    public ResponseObject<?> getTopSellingGames() {
        List<Game> topSellingGames = gameService.getTopSellingGames();
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Top selling games retrieved successfully")
                .data(topSellingGames)
                .build();
    }
}
