package vn.trungtq.jobhunter.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Permission;
import vn.trungtq.jobhunter.domain.Role;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.repository.PermissionRepository;
import vn.trungtq.jobhunter.repository.RoleRepository;
import vn.trungtq.jobhunter.repository.UserRepository;
import vn.trungtq.jobhunter.util.enums.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a company", "/companies", "POST", "COMPANIES"));
            arr.add(new Permission("Update a company", "/companies", "PUT", "COMPANIES"));
            arr.add(new Permission("Delete a company", "/companies/{id}", "DELETE", "COMPANIES"));
            arr.add(new Permission("Get a company by id", "/companies/{id}", "GET", "COMPANIES"));
            arr.add(new Permission("Get companies with pagination", "/companies", "GET", "COMPANIES"));

            arr.add(new Permission("Create a job", "/jobs", "POST", "JOBS"));
            arr.add(new Permission("Update a job", "/jobs", "PUT", "JOBS"));
            arr.add(new Permission("Delete a job", "/jobs/{id}", "DELETE", "JOBS"));
            arr.add(new Permission("Get a job by id", "/jobs/{id}", "GET", "JOBS"));
            arr.add(new Permission("Get jobs with pagination", "/jobs", "GET", "JOBS"));

            arr.add(new Permission("Create a permission", "/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a resume", "/resumes", "POST", "RESUMES"));
            arr.add(new Permission("Update a resume", "/resumes", "PUT", "RESUMES"));
            arr.add(new Permission("Delete a resume", "/resumes/{id}", "DELETE", "RESUMES"));
            arr.add(new Permission("Get a resume by id", "/resumes/{id}", "GET", "RESUMES"));
            arr.add(new Permission("Get resumes with pagination", "/resumes", "GET", "RESUMES"));

            arr.add(new Permission("Create a role", "/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/users", "GET", "USERS"));

            arr.add(new Permission("Create a subscriber", "/subscribers", "POST", "SUBSCRIBERS"));
            arr.add(new Permission("Update a subscriber", "/subscribers", "PUT", "SUBSCRIBERS"));
            arr.add(new Permission("Delete a subscriber", "/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
            arr.add(new Permission("Get a subscriber by id", "/subscribers/{id}", "GET", "SUBSCRIBERS"));
            arr.add(new Permission("Get subscribers with pagination", "/subscribers", "GET", "SUBSCRIBERS"));

            arr.add(new Permission("Download a file", "/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}
