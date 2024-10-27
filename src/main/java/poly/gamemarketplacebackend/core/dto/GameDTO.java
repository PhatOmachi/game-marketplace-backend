package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

@Data
public class GameDTO {
    private Integer sysIdGame;
    private String gameCode;
    private String gameName;
    private Boolean status;
    private Float price;
    private Float discountPercent;
    private String gameImage;
    private String slug;
    private String gameCategory;
    private String description;
    private Boolean isActive;
    private Integer quantity;
    private Integer quantitySold;
    private Integer quantityCount;

    private VoucherDTO voucher;
}
