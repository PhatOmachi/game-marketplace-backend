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

@Mapper(componentModel = "spring", uses = {MediaMapper.class, CategoryDetailMapper.class})
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    @Mapping(source = "categoryDetails", target = "categoryDetails")
    @Mapping(source = "media", target = "media")
//    @Mapping(source = "voucher.discountPercent", target = "discountPercent")
    GameDTO toDTO(Game game);

    @Mapping(source = "categoryDetails", target = "categoryDetails")
    @Mapping(source = "media", target = "media")
    Game toEntity(GameDTO gameDTO);

    List<GameDTO> toDTOs(List<Game> games);

    List<Game> toEntities(List<GameDTO> gameDTOs);
}
