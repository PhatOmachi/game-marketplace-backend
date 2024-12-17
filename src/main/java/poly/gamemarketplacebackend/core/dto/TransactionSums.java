package poly.gamemarketplacebackend.core.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionSums {
    private BigDecimal totalIncome;
    private BigDecimal todayIncome;
}
