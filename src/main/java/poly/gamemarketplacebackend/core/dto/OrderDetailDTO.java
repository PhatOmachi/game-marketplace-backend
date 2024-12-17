package poly.gamemarketplacebackend.core.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class OrderDetailDTO {
    private TransactionHistoryDTO transactionHistoryDTO;
    private List<OrdersDTO> ordersDTOS;
}