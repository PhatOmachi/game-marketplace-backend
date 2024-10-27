package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrdersDTO {
    private int sysIdOrder;
    private String orderCode;
    private Timestamp orderDate;
    private boolean paymentStatus;
    private float totalGamePrice;
    private float totalPayment;
    private int quantityPurchased;
    private int gameId;
    private int userId;
}