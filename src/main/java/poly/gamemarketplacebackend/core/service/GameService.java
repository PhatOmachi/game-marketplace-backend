package poly.gamemarketplacebackend.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Game;

import java.util.List;

public interface GameService {
    List<GameDTO> getAllGame();

    Game saveGame(GameDTO gameDTO);

    void deleteGame(GameDTO gameDTO);

    GameDTO findBySlug(String slug);

    List<GameDTO> getGamesByFieldDesc(String field, int page, int size);

    List<GameDTO> getTopGamesByVoucherEndDateNearest(int page, int size);

    List<CartItemDTO> isValidCartItems(List<GameDTO> cartItems);

    Page<Game> searchGames(String name, Double minPrice, Double maxPrice, String category, String minRatingStr, String maxRatingStr, Pageable pageable);
}
