package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Voucher;

import java.util.List;

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
}
