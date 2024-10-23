package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.CategoryDTO;
import poly.gamemarketplacebackend.core.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "categoryDetails", ignore = true)
    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(target = "categoryDetails", ignore = true)
    List<CategoryDTO> toDTOs(List<Category> categories);

    List<Category> toEntities(List<CategoryDTO> categoryDTOs);
}
