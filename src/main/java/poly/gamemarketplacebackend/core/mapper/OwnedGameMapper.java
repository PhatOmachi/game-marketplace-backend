package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.dto.OwnedGameDTO;
import poly.gamemarketplacebackend.core.entity.Orders;
import poly.gamemarketplacebackend.core.entity.OwnedGame;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnedGameMapper {
    OwnedGameMapper INSTANCE = Mappers.getMapper(OwnedGameMapper.class);

    OwnedGameDTO toDTO(OwnedGame ownedGame);
    OwnedGame toEntity(OwnedGameDTO ownedGameDTO);

    List<OwnedGameDTO> toDTOs(List<OwnedGame> ownedGames);
    List<OwnedGame> toEntities(List<OwnedGameDTO> ownedGameDTOS);
}