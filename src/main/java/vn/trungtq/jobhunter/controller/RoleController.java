package vn.trungtq.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.Role;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.service.RoleService;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

@RestController
public class RoleController {
    private final RoleService roleService;
    public RoleController(final RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a new role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws IdInvalidException {
        if(this.roleService.checkNameExist(role.getName())) {
            throw new IdInvalidException("Role " + role.getName()+ " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED). body(this.roleService.handleCreateRole(role));
    }
    @PutMapping("/roles")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role) throws IdInvalidException {
        Role curRole = this.roleService.handleGetRole(role.getId());
        if (curRole == null) {
            throw  new IdInvalidException(
                    "Role không tồn tại"
            );
        }
        Role updatedRole = this.roleService.handleUpdateRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRole);
    }
    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getAllRole(@Filter Specification<Role> spec, Pageable pageable){
        ResultPaginationDTO rs = this.roleService.handleGetAllRoles(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id)  throws IdInvalidException {
        Role curRole = this.roleService.handleGetRole(id);
        if (curRole == null) {
            throw  new IdInvalidException(
                    "Role không tồn tại"
            );
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRole(@PathVariable("id") long id) throws IdInvalidException {
        Role curRole = this.roleService.handleGetRole(id);
        if (curRole == null) {
            throw  new IdInvalidException(
                    "Role không tồn tại"
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(curRole);
    }

}
