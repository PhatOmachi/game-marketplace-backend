package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Orders;
import poly.gamemarketplacebackend.core.entity.OwnedGame;

@Repository
public interface OwnedGameRepository extends JpaRepository<OwnedGame, Integer> {
}