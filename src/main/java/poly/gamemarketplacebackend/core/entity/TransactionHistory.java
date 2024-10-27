// TransactionHistory.java
package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Transaction_History")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sysIdPayment;

    private Timestamp paymentTime;
    private String description;
    private float amount;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "user_name")
    private Users user;

    // Getters and Setters
}