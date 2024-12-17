package poly.gamemarketplacebackend.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RevenueAndProfitDTO {
    private List<MonthlyData> revenue;
    private List<MonthlyData> profit;

    @Data
    @AllArgsConstructor
    public static class MonthlyData {
        private String month;
        private Double amount;
    }
}

