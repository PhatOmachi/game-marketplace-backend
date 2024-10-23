package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.service.GameService;

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
}
