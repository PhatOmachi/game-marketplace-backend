package poly.gamemarketplacebackend.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class AnalyticsDataDTO {
    private Double totalRevenue;
    private Integer totalItemsSold;
    private Integer totalUsers;
}
