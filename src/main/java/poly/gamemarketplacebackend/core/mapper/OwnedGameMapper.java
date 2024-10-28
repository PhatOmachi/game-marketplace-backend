package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.dto.OwnedGameDTO;
import poly.gamemarketplacebackend.core.entity.Orders;
import poly.gamemarketplacebackend.core.entity.OwnedGame;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnedGameMapper {
    OwnedGameMapper INSTANCE = Mappers.getMapper(OwnedGameMapper.class);

    @Mapping(target = "userId", source = "user.sysIdUser")
    @Mapping(target = "gameId", source = "game.sysIdGame")
    OwnedGameDTO toDTO(OwnedGame ownedGame);

    @Mapping(source = "userId", target = "user.sysIdUser")
    @Mapping(source = "gameId", target = "game.sysIdGame")
    OwnedGame toEntity(OwnedGameDTO ownedGameDTO);

    List<OwnedGameDTO> toDTOs(List<OwnedGame> ownedGames);
    List<OwnedGame> toEntities(List<OwnedGameDTO> ownedGameDTOS);
}