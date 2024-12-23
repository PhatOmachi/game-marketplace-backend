package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
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

    @Column(name = "user_name")
    private String username;

    // new field
    private double userBalance;

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "sysIdPayment=" + sysIdPayment +
                ", paymentTime=" + paymentTime +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", username='" + username + '\'' +
                ", userBalance=" + userBalance +
                '}';
    }
}