package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Game;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer>, JpaSpecificationExecutor<Game> {

    @Query(value = "SELECT g from Game g ORDER BY g.sysIdGame asc")
    List<Game> findAll();

    Game save(Game game);


    Optional<Game> findById(Integer id);

    void delete(Game game);

    Optional<Game> findBySlug(String slug);

    Page<Game> findAll(Specification<Game> spec, Pageable pageable);

    @Query("SELECT g FROM Game g JOIN CategoryDetail cd ON g.sysIdGame = cd.game.sysIdGame WHERE cd.category.sysIdCategory in :categoryIds AND g.sysIdGame <> :sysIdGame order by g.discountPercent desc")
    List<Game> findRelatedGames(@Param("categoryIds") List<Integer> categoryIds, @Param("sysIdGame") Integer sysIdGame);

    @Query(value = "SELECT * " +
            "FROM game " +
            "ORDER BY price * quantity_sold * (1 - discount_percent / 100) DESC " +
            "LIMIT 5;", nativeQuery = true)
    List<Game> getTopSelling();

    @Procedure(procedureName = "update_game_ratings")
    void updateGameRatings();

    @Modifying
    @Query("UPDATE Game g SET g.quantity = g.quantity - :amount, g.quantitySold = g.quantitySold + :amount WHERE g.sysIdGame = :sysIdGame")
    void updateGameQuantityAndSold(@Param("sysIdGame") Integer sysIdGame, @Param("amount") Integer amount);
}
