package poly.gamemarketplacebackend.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionSummary {
    private String date;
    private int orderCount;
    private int userCount;
}
