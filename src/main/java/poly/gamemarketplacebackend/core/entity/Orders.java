package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sysIdOrder;

    private String orderCode;
    private Timestamp orderDate;
    private boolean paymentStatus;
    private float totalGamePrice;
    private float totalPayment;
    private int quantityPurchased;
//    @Column(name = "sys_id_product")
//    private int sysIdProduct;
    @Column(name = "sys_id_product", insertable = false, updatable = false)
    private int sysIdProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_id_user", referencedColumnName = "sys_id_user", nullable = false)
    @JsonBackReference
    private Users users;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sys_id_product", referencedColumnName = "sys_id_game", nullable = false)
    private Game game;
//    name = "sys_id_product": Tên cột trong bảng Orders.
//    referencedColumnName = "sys_id_game": Tên cột trong bảng Game mà sys_id_product tham chiếu đến.

}