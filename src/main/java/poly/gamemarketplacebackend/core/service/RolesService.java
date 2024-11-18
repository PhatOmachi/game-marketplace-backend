package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.RolesDTO;

import java.util.List;

public interface RolesService {
    List<RolesDTO> getAllRoles();

    RolesDTO getRoleById(int id);

    RolesDTO createRole(RolesDTO rolesDTO);

    RolesDTO updateRole(int id, RolesDTO rolesDTO);

    void deleteRole(int id);
}