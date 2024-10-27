package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.entity.Game;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    @Mapping(target = "voucher", source = "voucher", ignore = true)
    GameDTO toDTO(Game game);

    @Mapping(target = "voucher", source = "voucher", ignore = true)
    Game toEntity(GameDTO gameDTO);

    List<GameDTO> toDTOs(List<Game> games);
    List<Game> toEntities(List<GameDTO> gameDTOs);
}

