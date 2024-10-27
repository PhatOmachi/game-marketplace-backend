package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.service.GameService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameAPI {
    private final GameService gameService;

    @GetMapping("")
    public ResponseObject<?> getAllGame() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.getAllGame())
                .build();
    }

    @PostMapping("")
    public ResponseObject<?> createGame(@RequestBody GameDTO gameDTO) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(gameService.saveGame(gameDTO))
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
}
