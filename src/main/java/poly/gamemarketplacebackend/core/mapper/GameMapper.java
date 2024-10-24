package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.entity.Game;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {MediaMapper.class, CategoryDetailMapper.class,})
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    @Mapping(source = "categoryDetails", target = "categoryDetails", qualifiedByName = "mapCategoryDetailsToDTOs")
    @Mapping(source = "media", target = "media")
    GameDTO toDTO(Game game);

    @Mapping(source = "categoryDetails", target = "categoryDetails", qualifiedByName = "mapDTOsToCategoryDetails")
    @Mapping(source = "media", target = "media")
    Game toEntity(GameDTO gameDTO);

    List<GameDTO> toDTOs(List<Game> games);

    List<Game> toEntities(List<GameDTO> gameDTOs);

    @Mapping(source = "categoryDetails", target = "categoryDetails", qualifiedByName = "mapDTOsToCategoryDetails")
    void updateGameFromDto(GameDTO gameDTO, @MappingTarget Game game);

    @Named("mapCategoryDetailsToDTOs")
    default List<CategoryDetailDTO> mapCategoryDetailsToDTOs(List<CategoryDetail> categoryDetails) {
        if (categoryDetails == null) {
            return null;
        }
        return categoryDetails.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Named("mapDTOsToCategoryDetails")
    default List<CategoryDetail> mapDTOsToCategoryDetails(List<CategoryDetailDTO> categoryDetailDTOs) {
        if (categoryDetailDTOs == null) {
            return null;
        }
        return categoryDetailDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    default CategoryDetailDTO convertToDTO(CategoryDetail categoryDetail) {
        if (categoryDetail == null) {
            return null;
        }
        CategoryDetailDTO dto = new CategoryDetailDTO();
        dto.setSysIdCategory(categoryDetail.getSysIdCategory());
        dto.setSysIdGame(categoryDetail.getSysIdGame());
        return dto;
    }

    default CategoryDetail convertToEntity(CategoryDetailDTO dto) {
        if (dto == null) {
            return null;
        }
        CategoryDetail categoryDetail = new CategoryDetail();
        categoryDetail.setSysIdCategory(dto.getSysIdCategory());
        categoryDetail.setSysIdGame(dto.getSysIdGame());
        return categoryDetail;
    }
}