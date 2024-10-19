package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.gamemarketplacebackend.core.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}