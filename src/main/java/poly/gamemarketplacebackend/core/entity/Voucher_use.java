package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Voucher_used")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher_use {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_voucher_used")
    private int sysIdVoucherUse;

    @Column(name = "sys_id_user", nullable = false)
    private int sysIdUser;

    @Column(name = "use_date", nullable = false)
    private LocalDate useDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_id_voucher", referencedColumnName = "sys_id_voucher")
    @JsonIgnore
    private Voucher sysIdVoucherUseDetail;

}
