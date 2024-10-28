package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.CategoryDTO;
import poly.gamemarketplacebackend.core.entity.Category;
import poly.gamemarketplacebackend.core.mapper.CategoryMapper;
import poly.gamemarketplacebackend.core.repository.CategoryRepository;
import poly.gamemarketplacebackend.core.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategory() {
        return categoryMapper.toDTOs(categoryRepository.findAll());
    }

    @Override
    public Category saveCategory(CategoryDTO categoryDTO) {
        return categoryRepository.save(categoryMapper.toEntity(categoryDTO));
    }

    @Override
    public void deleteCategory(CategoryDTO categoryDTO) {
        categoryRepository.delete(categoryMapper.toEntity(categoryDTO));
    }

    @Override
    public CategoryDTO findById(int id) {
        return categoryMapper.toDTO(categoryRepository.findById(id));
    }
}
