package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.RolesDTO;
import poly.gamemarketplacebackend.core.entity.Roles;

@Mapper(componentModel = "spring")
public interface RolesMapper {
    RolesMapper INSTANCE = Mappers.getMapper(RolesMapper.class);

    RolesDTO toDTO(Roles roles);
    Roles toEntity(RolesDTO rolesDTO);
}