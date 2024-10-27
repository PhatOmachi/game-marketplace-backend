package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Game;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    // sử dụng để tìm kiếm tất cả các game
    List<Game> findAll();

    // sử dụng để tìm kiếm game theo code
    Game findByGameCode(String codeGame);

    // sử dụng để tìm kiếm game theo id chính
    Game findBySysIdGame(Integer sysIdGame);

    // sử dụng để tìm kiếm game theo slug
    Game findBySlug(String slug);

    // sử dụng để lưu game vào cơ sở dữ liệu
    Game save(Game game);

    // sử dụng để xóa game theo id chính
    void deleteBySysIdGame(Integer sysIdGame);
}
