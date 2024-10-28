package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Game;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    @Query(value = "SELECT g from Game g ORDER BY g.sysIdGame asc")
    List<Game> findAll();

    Game save(Game game);

    void delete(Game game);

    Optional<Game> findBySlug(String slug);

//    @Query("SELECT g FROM Game g JOIN g.voucher v WHERE v.endDate >= CURRENT_DATE ORDER BY v.endDate ASC")
//    Page<Game> findTopByVoucherEndDateNearest(Pageable pageable);
}
