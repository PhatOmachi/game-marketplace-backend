package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TransactionHistoryDTO {
    private int sysIdPayment;
    private Timestamp paymentTime;
    private String description;
    private float amount;
    private boolean status;
    //    private int userId;
    private String username;
    // new field
    private double userBalance;

    @Override
    public String toString() {
        return "TransactionHistoryDTO{" +
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