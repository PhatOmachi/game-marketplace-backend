package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.CommentDTO;
import poly.gamemarketplacebackend.core.entity.Comment;

@Mapper(componentModel = "spring", uses = {UsersMapper.class, GameMapper.class})
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "user", target = "usersDTO")
    @Mapping(source = "game.sysIdGame", target = "gameId")
    @Mapping(source = "game", target = "gameDTO")
    CommentDTO toDTO(Comment comment);

    @Mapping(source = "usersDTO", target = "user")
    @Mapping(source = "gameId", target = "game.sysIdGame")
    @Mapping(source = "gameDTO", target = "game")
    Comment toEntity(CommentDTO commentDTO);

    void updateEntityFromDTO(CommentDTO commentDTO, @MappingTarget Comment comment);
}
