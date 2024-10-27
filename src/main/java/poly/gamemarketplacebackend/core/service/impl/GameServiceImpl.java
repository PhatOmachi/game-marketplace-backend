package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.mapper.GameMapper;
import poly.gamemarketplacebackend.core.repository.GameRepository;
import poly.gamemarketplacebackend.core.service.GameService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
}
