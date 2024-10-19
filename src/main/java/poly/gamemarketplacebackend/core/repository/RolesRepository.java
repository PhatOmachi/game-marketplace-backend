package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.gamemarketplacebackend.core.entity.Roles;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
}