package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Integer> {
}