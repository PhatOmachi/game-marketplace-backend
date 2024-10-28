package poly.gamemarketplacebackend.core.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
public class PaymentRequestDTO {
    private String orderCode;
    private float totalPayment;
    private String voucherCode;
    private String username;
    private int userId;
    private Timestamp orderDate;
    private List<OrdersDTO> orders;
}
