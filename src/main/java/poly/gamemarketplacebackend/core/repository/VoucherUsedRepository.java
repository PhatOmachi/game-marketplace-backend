package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Voucher;
import poly.gamemarketplacebackend.core.entity.Voucher_use;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherUsedRepository extends JpaRepository<Voucher_use, Integer> {

}
