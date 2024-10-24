package poly.gamemarketplacebackend.core.dto;

import lombok.Data;
import poly.gamemarketplacebackend.core.entity.Voucher_use;

import java.time.LocalDate;
import java.util.List;

@Data
public class VoucherDTO {
    private Integer sysIdVoucher;
    private String codeVoucher;
    private String discountName;
    private Integer discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private List<Voucher_use> voucherDetails;
    private List<GameDTO> games;
}
