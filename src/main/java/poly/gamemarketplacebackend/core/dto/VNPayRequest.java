package poly.gamemarketplacebackend.core.dto;

import lombok.Getter;

@Getter
public class VNPayRequest {
    private int amount;
    private String bankCode;
    private String name;
    private String successUrl;
    private String errorUrl;
}
