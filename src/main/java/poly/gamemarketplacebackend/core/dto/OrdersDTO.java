package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrdersDTO {
    private int sysIdOrder;
    private String orderCode;
    private Timestamp orderDate;
    private boolean paymentStatus;
    //    private float totalGamePrice;
    private float totalPayment;
    //    private int quantityPurchased;
    private int sysIdProduct;
    private int sysIdUser;
    // new field
    private String slug;
    private int quantity;
    private int price;
    private String gameName;
    private GameDTO gameDTO;
    private UsersDTO usersDTO;

    @Override
    public String toString() {
        return "OrdersDTO{" +
                "sysIdOrder=" + sysIdOrder +
                ", orderCode='" + orderCode + '\'' +
                ", orderDate=" + orderDate +
                ", paymentStatus=" + paymentStatus +
//                ", totalGamePrice=" + totalGamePrice +
                ", price=" + price +
                ", totalPayment=" + totalPayment +
//                ", quantityPurchased=" + quantityPurchased +
                ", sysIdProduct=" + sysIdProduct +
                ", sysIdUser=" + sysIdUser +
                ", slug='" + slug + '\'' +
                ", quantity=" + quantity +
                ", gameName='" + gameName + '\'' +
                '}';
    }
}