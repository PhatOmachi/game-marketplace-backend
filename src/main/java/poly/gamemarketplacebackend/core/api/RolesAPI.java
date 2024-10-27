package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.dto.RolesDTO;
import poly.gamemarketplacebackend.core.service.RolesService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RolesAPI {

    private final RolesService rolesService;

    @GetMapping
    public List<RolesDTO> getAllRoles() {
        return rolesService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RolesDTO getRoleById(@PathVariable int id) {
        return rolesService.getRoleById(id);
    }

    @PostMapping
    public RolesDTO createRole(@RequestBody RolesDTO rolesDTO) {
        return rolesService.createRole(rolesDTO);
    }

    @PutMapping("/{id}")
    public RolesDTO updateRole(@PathVariable int id, @RequestBody RolesDTO rolesDTO) {
        return rolesService.updateRole(id, rolesDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable int id) {
        rolesService.deleteRole(id);
    }
}