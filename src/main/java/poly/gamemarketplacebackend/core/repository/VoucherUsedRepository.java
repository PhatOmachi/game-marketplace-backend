package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Voucher_use;

@Repository
public interface VoucherUsedRepository extends JpaRepository<Voucher_use, Integer> {
}
