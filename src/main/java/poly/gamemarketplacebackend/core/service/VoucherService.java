package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.entity.Voucher;

import java.util.List;

public interface VoucherService {
    List<VoucherDTO> findAll();

    VoucherDTO findByCodeVoucher(String codeVoucher);

    VoucherDTO validVoucherByUser(String codeVoucher);

    VoucherDTO findBySysIdVoucher(Integer id);

    VoucherDTO save(Voucher voucher);

    void deleteBySysIdVoucher(Integer id);

    List<VoucherDTO> findTopByEndDateNearest(int page, int size);

    List<VoucherDTO> getPageVoucher(int page, int size);

    void sendVoucherToUser(String codeVoucher);
}
