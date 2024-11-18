package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {
}