package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.CategoryDTO;
import poly.gamemarketplacebackend.core.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategory();

    Category saveCategory(CategoryDTO categoryDTO);

    void deleteCategory(CategoryDTO categoryDTO);

    CategoryDTO findById(int id);
}
