package vn.trungtq.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.web.bind.annotation.RestController;
import vn.trungtq.jobhunter.domain.Permission;
import vn.trungtq.jobhunter.domain.Resume;

import java.util.List;

@RestController
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    List<Permission> findByIdIn(List<Long> id);
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
}
