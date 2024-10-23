package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDTO {
    private Integer sysIdCategory;
    private String categoryName;
    private String description;
    private List<CategoryDetailDTO> categoryDetails;
}
