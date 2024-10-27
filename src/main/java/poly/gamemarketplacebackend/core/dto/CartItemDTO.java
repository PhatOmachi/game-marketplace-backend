package poly.gamemarketplacebackend.core.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemDTO {
    private String slug;
    private int quantity;
    private String message;
}
