package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.MediaDTO;
import poly.gamemarketplacebackend.core.entity.Media;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    MediaMapper INSTANCE = Mappers.getMapper(MediaMapper.class);

    @Mapping(target = "sysIdGame", source = "game.sysIdGame")
    MediaDTO toDTO(Media media);

    @Mapping(target = "game.sysIdGame", source = "sysIdGame")
    Media toEntity(MediaDTO mediaDTO);

    List<MediaDTO> toDTOs(List<Media> mediaList);

    List<Media> toEntities(List<MediaDTO> mediaDTOList);
}