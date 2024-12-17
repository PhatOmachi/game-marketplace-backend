package poly.gamemarketplacebackend.core.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Statistic {
    private int thisMonth;
    private BigDecimal increasedPercent;
}
