package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_order")
    private int sysIdOrder;

    @Column(name = "order_code", length = 255)
    private String orderCode;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "payment_status")
    private boolean paymentStatus;

    @Column(name = "total_game_price")
    private float totalGamePrice;

    @Column(name = "total_payment")
    private float totalPayment;

    @Column(name = "quantity_purchased")
    private int quantityPurchased;

//    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL )
//    @JoinColumn(name = "sys_id_product", referencedColumnName = "sys_id_game")
//    private Game game;

    @ManyToOne()
    @JoinColumn(name = "sys_id_user", referencedColumnName = "sys_id_user", nullable = false)
    @JsonBackReference
    private Users users;
}

