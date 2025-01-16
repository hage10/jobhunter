package vn.trungtq.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.Permission;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.service.PermissionService;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

@RestController
public class PermissionController {
    private final PermissionService permissionService;
    public PermissionController(final PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create new permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {

        boolean isExisted = this.permissionService.isPermissionExist(permission);
        if (isExisted) {
            throw  new IdInvalidException("Permission đã tồn tại");
        }
        Permission newPermission = this.permissionService.handleCreatePermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED). body(newPermission);
    }
    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        Permission curPermission = this.permissionService.handleGetPermission(permission.getId());
        if (curPermission == null) {
            throw  new IdInvalidException(
                    "Permission không tồn tại"
            );
        }
        boolean isExisted = this.permissionService.isPermissionExist(permission);
        if (isExisted) {
            if(this.permissionService.isSameName(permission)){
                throw  new IdInvalidException("Permission đã tồn tại");
            }
        }
        Permission updatedPermission = this.permissionService.handleUpdatePermission(permission);

        return ResponseEntity.status(HttpStatus.OK).body(updatedPermission);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Permission> spec, Pageable pageable){
        ResultPaginationDTO rs = this.permissionService.handleGetAllPermissions(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id)  throws IdInvalidException {
        Permission curPermission = this.permissionService.handleGetPermission(id);
        if (curPermission == null) {
            throw  new IdInvalidException(
                    "Permission không tồn tại"
            );
        }
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().body(null);
    }

}
