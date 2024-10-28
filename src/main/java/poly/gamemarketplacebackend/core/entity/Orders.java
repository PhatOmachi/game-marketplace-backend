// Orders.java
package poly.gamemarketplacebackend.core.entity;

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
    private int sysIdProduct;
    private int sysIdUser;

//    @ManyToOne
//    @JoinColumn(name = "sys_id_product")
//    private Game game;
//
//    @ManyToOne
//    @JoinColumn(name = "sys_id_user")
//    private Users user;

    // Getters and Setters
}