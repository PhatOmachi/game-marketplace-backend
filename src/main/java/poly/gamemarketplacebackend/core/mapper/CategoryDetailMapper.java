package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryDetailMapper {
    CategoryDetailMapper INSTANCE = Mappers.getMapper(CategoryDetailMapper.class);

    CategoryDetailDTO toDTO(CategoryDetail categoryDetail);

    CategoryDetail toEntity(CategoryDetailDTO categoryDetailDTO);

    List<CategoryDetailDTO> toDTOs(List<CategoryDetail> categoryDetails);

    List<CategoryDetail> toEntities(List<CategoryDetailDTO> categoryDetailDTOs);
}
