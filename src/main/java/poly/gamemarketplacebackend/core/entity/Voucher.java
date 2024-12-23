package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Voucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_voucher")
    private Integer sysIdVoucher;

    @Column(name = "code_voucher", nullable = false)
    private String codeVoucher;

    @Column(name = "discount_name", nullable = false)
    private String discountName;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "sysIdVoucherUseDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Voucher_use> voucherDetails;

//    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Game> games;

    // new field
    private String voucherBanner;
    private int quantity;
    private boolean isActive;
    private int maxDiscount;
}
