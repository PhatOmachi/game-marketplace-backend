package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.entity.Voucher;

import java.io.IOException;
import java.util.List;

public interface VoucherService {
    List<VoucherDTO> findAll();

    VoucherDTO findByCodeVoucher(String codeVoucher);

    VoucherDTO validVoucherByUser(String codeVoucher);

    VoucherDTO findBySysIdVoucher(Integer id);

    void save(VoucherDTO voucher) throws IOException;

    void deleteBySysIdVoucher(Integer id);

    List<VoucherDTO> findTopByEndDateNearest(int page, int size);

    List<VoucherDTO> getPageVoucher(int page, int size);
}
