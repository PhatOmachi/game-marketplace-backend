package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

@Data
public class CategoryDetailDTO {
    private Integer sysIdCategory;
    private Integer sysIdGame;
    private CategoryDTO category;
    private GameDTO game;
}
