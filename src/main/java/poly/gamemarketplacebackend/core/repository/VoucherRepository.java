package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Voucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    // Sử dụng phương thức findAll() để lấy tất cả các đối tượng Voucher từ cơ sở dữ liệu.
    List<Voucher> findAll();

    // Sử dụng phương thức findByCode() để tìm kiếm đối tượng Voucher theo mã code.
    Voucher findByCodeVoucher(String codeVoucher);

    // Sử dụng phương thức findBySySIdVoucher() để tìm kiếm đối tượng Voucher theo ID.
    Voucher findBySysIdVoucher(Integer id);

    // Sử dụng phương thức save() để lưu đối tượng Voucher vào cơ sở dữ liệu.
    Voucher save(Voucher voucher);

    // Sử dụng phương thức deleteById() để xóa đối tượng Voucher theo ID.
    void deleteBySysIdVoucher(Integer id);

    @Query("SELECT v FROM Voucher v WHERE v.endDate >= CURRENT_DATE and v.startDate <= CURRENT_DATE ORDER BY v.endDate ASC")
    Page<Voucher> findTopByEndDateNearest(Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.endDate >= CURRENT_DATE and v.startDate <= CURRENT_DATE")
    Page<Voucher> findAllByPage(Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.codeVoucher = :codeVoucher " +
            "AND v.startDate <= CURRENT_DATE " +
            "AND v.endDate >= CURRENT_DATE " +
            "AND v.isActive = true " +
            "AND v.quantity >= 1 " +
            "AND NOT EXISTS (SELECT vu FROM Voucher_use vu WHERE vu.sysIdVoucherUseDetail.sysIdVoucher = v.sysIdVoucher AND vu.sysIdUser = :sysIdUser)")
    Optional<Voucher> findValidVoucher(@Param("codeVoucher") String codeVoucher, @Param("sysIdUser") int sysIdUser);

}
