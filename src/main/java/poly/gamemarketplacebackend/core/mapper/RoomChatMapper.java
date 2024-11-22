package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.ChatRoomDTO;
import poly.gamemarketplacebackend.core.dto.GameDTO;
import poly.gamemarketplacebackend.core.dto.RolesDTO;
import poly.gamemarketplacebackend.core.entity.ChatRoom;
import poly.gamemarketplacebackend.core.entity.Game;
import poly.gamemarketplacebackend.core.entity.Roles;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomChatMapper {
    RoomChatMapper INSTANCE = Mappers.getMapper(RoomChatMapper.class);

    ChatRoomDTO toDTO(ChatRoom chatRoom);

    ChatRoom toEntity(ChatRoomDTO chatRoomDTO);

    List<ChatRoomDTO> toDTOs(List<ChatRoom> rooms);

    List<ChatRoom> toEntities(List<ChatRoomDTO> roomDTOS);

}
