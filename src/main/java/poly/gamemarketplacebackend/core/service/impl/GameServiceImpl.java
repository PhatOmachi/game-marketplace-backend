package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.mapper.GameMapper;
import poly.gamemarketplacebackend.core.repository.GameRepository;
import poly.gamemarketplacebackend.core.service.GameService;
import poly.gamemarketplacebackend.core.util.GameSpecification;

import java.util.ArrayList;
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
