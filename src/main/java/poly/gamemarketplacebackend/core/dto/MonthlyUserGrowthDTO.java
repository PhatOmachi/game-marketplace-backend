package poly.gamemarketplacebackend.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthlyUserGrowthDTO {
    private List<MonthlyData> userGrowth;

    @Data
    @AllArgsConstructor
    public static class MonthlyData {
        private String month;
        private Integer newUsers;
    }
}
