package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Permission;
import vn.trungtq.jobhunter.domain.Role;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.repository.CompanyRepository;
import vn.trungtq.jobhunter.repository.PermissionRepository;
import vn.trungtq.jobhunter.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CompanyRepository companyRepository;
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository,
                       CompanyRepository companyRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.companyRepository = companyRepository;
    }

    public Role handleCreateRole(Role role) {
        if(role.getPermissions() != null){
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        Role newRole = this.roleRepository.save(role);
        return newRole;
    }

    public boolean checkNameExist(String name) {
        return this.roleRepository.existsByName(name);
    }
    public Role handleGetRole(long id) {
        return this.roleRepository.findById(id).orElse(null);
    }
    public Role handleUpdateRole(Role r) {
        Role roleDB = this.handleGetRole(r.getId());
        // check permissions
        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());
        roleDB.setPermissions(r.getPermissions());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;
    }
    public ResultPaginationDTO handleGetAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRoles = roleRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageRoles.getTotalElements());
        meta.setPages(pageRoles.getTotalPages());
        rs.setMeta(meta);
        rs.setResult(pageRoles.getContent());
        return rs;
    }
    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }
}
