package poly.gamemarketplacebackend.core.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VNPayResponse {
    public String code;
    public String message;
    public String paymentUrl;
}
