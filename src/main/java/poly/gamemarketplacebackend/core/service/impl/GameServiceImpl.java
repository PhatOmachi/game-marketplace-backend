package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.mapper.GameMapper;
import poly.gamemarketplacebackend.core.repository.GameRepository;
import poly.gamemarketplacebackend.core.service.GameService;

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
}
