package poly.gamemarketplacebackend.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.CartItemDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Game;

import java.io.IOException;
import java.util.List;

public interface GameService {
    List<GameDTO> getAllGame();

    List<GameDTO> getTop10RecommendedGames(String slug);

    Game saveGame(GameDTO gameDTO);

    void deleteGame(GameDTO gameDTO);

    GameDTO findBySlug(String slug);

    GameDTO findById(Integer integer);

    String saveFile(String uploadDir, MultipartFile file) throws IOException;

    List<GameDTO> getGamesByFieldDesc(String field, int page, int size);

    List<GameDTO> getTopGamesByVoucherEndDateNearest(int page, int size);

    List<CartItemDTO> isValidCartItems(List<GameDTO> cartItems);

    Page<Game> searchGames(String name, Double minPrice, Double maxPrice, String category, String minRatingStr, String maxRatingStr, Pageable pageable);

    ResponseObject<?> saveOrUpdateGame(GameDTO gameDTO, boolean isUpdate);

    void deleteImage(String imageUrl);
}
