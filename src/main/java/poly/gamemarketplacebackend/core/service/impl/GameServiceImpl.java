package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    public GameDTO findBySlug(String slug) {
        var game = gameRepository.findBySlug(slug);
        return game.map(gameMapper::toDTO).orElse(null);
    }

    @Override
    public List<GameDTO> getGamesByFieldDesc(String field, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, field));
        Page<Game> gamePage = gameRepository.findAll(pageable);
        return gameMapper.toDTOs(gamePage.getContent());
    }

    @Override
    public List<GameDTO> getTopGamesByVoucherEndDateNearest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamePage = gameRepository.findTopByVoucherEndDateNearest(pageable);
        return gameMapper.toDTOs(gamePage.getContent());
    }
}
