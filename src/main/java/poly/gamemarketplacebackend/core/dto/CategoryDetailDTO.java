package poly.gamemarketplacebackend.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CategoryDetailDTO {
    private int sysIdCategoryDetail;
    @JsonIgnore
    private CategoryDTO category;
    @JsonIgnore
    private GameDTO game;

//    additional fields
    private Integer sysIdCategory;
    private Integer sysIdGame;
    private String categoryName;
}
