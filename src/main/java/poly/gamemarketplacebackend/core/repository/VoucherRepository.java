package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Voucher;

import java.time.LocalDate;
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
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Voucher (code_voucher, discount_name, discount_percent, start_date, end_date, description, voucher_banner, quantity, is_active, max_discount) VALUES (:codeVoucher, :discountName, :discountPercent, :startDate, :endDate, :description, :voucherBanner, :quantity, :isActive, :maxDiscount)", nativeQuery = true)
    void insertVoucher(@Param("codeVoucher") String codeVoucher, @Param("discountName") String discountName, @Param("discountPercent") Integer discountPercent, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("description") String description, @Param("voucherBanner") String voucherBanner, @Param("quantity") Integer quantity, @Param("isActive") Boolean isActive, @Param("maxDiscount") Integer maxDiscount);

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
            "AND NOT EXISTS (SELECT vu FROM Voucher_use vu WHERE vu.sysIdVoucherUseDetail = v AND vu.sysIdUser = :sysIdUser)")
    Optional<Voucher> findValidVoucher(@Param("codeVoucher") String codeVoucher, @Param("sysIdUser") int sysIdUser);

}
