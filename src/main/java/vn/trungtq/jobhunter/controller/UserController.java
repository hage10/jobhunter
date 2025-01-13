package vn.trungtq.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.domain.response.ResCreateUserDTO;
import vn.trungtq.jobhunter.domain.response.ResUpdateUserDTO;
import vn.trungtq.jobhunter.domain.response.ResUserDTO;
import vn.trungtq.jobhunter.service.UserService;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.checkEmailExist(user.getEmail());
        if (isEmailExist) {
            throw  new IdInvalidException(
                    "Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng Email khác"
            );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED). body(this.userService.convertToResCreateUserDTO(newUser));
    }


    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws  IdInvalidException {
        User curUser = this.userService.handleGetUser(id);

        if(curUser == null){
            throw new IdInvalidException("User không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.handleGetUser(id);
        if(user == null){
            throw new IdInvalidException("User không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> spec, Pageable pageable){
        ResultPaginationDTO rs = this.userService.handleGetAllUsers(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }
    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User newUser = this.userService.handleUpdateUser(user);
        if(newUser == null){
            throw new IdInvalidException("User không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateUserDTO(newUser));
    }
}
