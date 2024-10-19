package poly.gamemarketplacebackend.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.RolesDTO;
import poly.gamemarketplacebackend.core.entity.Roles;
import poly.gamemarketplacebackend.core.mapper.RolesMapper;
import poly.gamemarketplacebackend.core.repository.RolesRepository;
import poly.gamemarketplacebackend.core.service.RolesService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RolesMapper rolesMapper;

    @Override
    public List<RolesDTO> getAllRoles() {
        return rolesRepository.findAll().stream()
                .map(rolesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RolesDTO getRoleById(int id) {
        return rolesRepository.findById(id)
                .map(rolesMapper::toDTO)
                .orElse(null);
    }

    @Override
    public RolesDTO createRole(RolesDTO rolesDTO) {
        Roles roles = rolesMapper.toEntity(rolesDTO);
        roles = rolesRepository.save(roles);
        return rolesMapper.toDTO(roles);
    }

    @Override
    public RolesDTO updateRole(int id, RolesDTO rolesDTO) {
        if (rolesRepository.existsById(id)) {
            Roles roles = rolesMapper.toEntity(rolesDTO);
            roles.setSysIdRole(id);
            roles = rolesRepository.save(roles);
            return rolesMapper.toDTO(roles);
        }
        return null;
    }

    @Override
    public void deleteRole(int id) {
        rolesRepository.deleteById(id);
    }
}