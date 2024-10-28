package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

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
//    private String gameCategory;
    private String description;
    private Boolean isActive;
//    private Integer sysIdDiscount;
    private Integer quantity;
    private Integer quantitySold;
//    private Integer quantityCount;
//    private VoucherDTO voucher;
    private List<CategoryDetailDTO> categoryDetails;

    // additional fields
    private Float rating;
    private Integer ratingCount;
    private String features;
    private LocalDate releaseDate;
    private String developer;
    private String platform;
    private String language;
    private String about;
    private List<MediaDTO> media;
}
