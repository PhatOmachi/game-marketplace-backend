package poly.gamemarketplacebackend.core.service;

import org.springframework.web.multipart.MultipartFile;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface GameService {
    List<GameDTO> getAllGame();

    Game saveGame(GameDTO gameDTO);

    void deleteGame(GameDTO gameDTO);

    GameDTO findBySlug(String slug);

    String saveFile(String uploadDir, MultipartFile file) throws IOException;
}
