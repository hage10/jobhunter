package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Permission;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.repository.JobRepository;
import vn.trungtq.jobhunter.repository.PermissionRepository;
import vn.trungtq.jobhunter.repository.UserRepository;
import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    public PermissionService(PermissionRepository permissionRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public Permission handleCreatePermission(Permission permission) {
       return this.permissionRepository.save(permission);
    }

    public Permission handleGetPermission(long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }
    public void getPermission(Permission permission) {

    }

    public Permission handleUpdatePermission(Permission permission) {
        Permission curPermission = handleGetPermission(permission.getId());
        if (curPermission != null) {
            curPermission.setName(permission.getName());
            curPermission.setApiPath(permission.getApiPath());
            curPermission.setMethod(permission.getMethod());
            curPermission.setModule(permission.getModule());
            return this.permissionRepository.save(curPermission);
        }
        return  null;
    }
    public ResultPaginationDTO handleGetAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermissions = permissionRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pagePermissions.getTotalElements());
        meta.setPages(pagePermissions.getTotalPages());
        rs.setMeta(meta);
        rs.setResult(pagePermissions.getContent());
        return rs;
    }
    public void handleDeletePermission(long id) {
        Optional<Permission> permission = this.permissionRepository.findById(id);
        Permission permissionToDelete = permission.get();
        permissionToDelete.getRoles().forEach(job -> job.getPermissions().remove(permissionToDelete));
        this.permissionRepository.delete(permissionToDelete);
    }
    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(), permission.getApiPath(), permission.getMethod()
        );
    }

}
