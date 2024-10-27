package poly.gamemarketplacebackend.core.mapper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface CategoryDetailMapper {
    CategoryDetailMapper INSTANCE = Mappers.getMapper(CategoryDetailMapper.class);


    @Mapping(target = "categoryName", source = "category.categoryName")
    @Mapping(target = "sysIdCategory", source = "category.sysIdCategory")
    @Mapping(target = "sysIdGame", source = "game.sysIdGame")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "game", ignore = true)
    CategoryDetailDTO toDTO(CategoryDetail categoryDetail);

    CategoryDetail toEntity(CategoryDetailDTO categoryDetailDTO);

    List<CategoryDetailDTO> toDTOs(List<CategoryDetail> categoryDetails);

    List<CategoryDetail> toEntities(List<CategoryDetailDTO> categoryDetailDTOs);
}
