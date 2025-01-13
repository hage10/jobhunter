package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.dto.Meta;
import vn.trungtq.jobhunter.domain.dto.ResultPaginationDTO;
import vn.trungtq.jobhunter.domain.dto.response.ResCreateUserDTO;
import vn.trungtq.jobhunter.domain.dto.response.ResUpdateUserDTO;
import vn.trungtq.jobhunter.domain.dto.response.ResUserDTO;
import vn.trungtq.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }
    public void handleDeleteUser(long id) {
         this.userRepository.deleteById(id);
    }
    public User handleGetUser(long id) {
        return this.userRepository.findById(id).orElse(null);
    }
    public ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUsers = userRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageUsers.getTotalElements());
        meta.setPages(pageUsers.getTotalPages());
        rs.setMeta(meta);
        List<ResUserDTO> listUser = pageUsers.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());
        rs.setResult(listUser);
        return rs;
    }
    public User handleUpdateUser(User user) {
        User curUser = handleGetUser(user.getId());
        if (curUser != null) {
            curUser.setName(user.getName());
            curUser.setAddress(user.getAddress());
            curUser.setAge(user.getAge());
            curUser.setGender(user.getGender());
            curUser = this.userRepository.save(curUser);
        }
        return  curUser;
    }
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
    public boolean checkEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO rs = new ResCreateUserDTO();
        rs.setId(user.getId());
        rs.setName(user.getName());
        rs.setEmail(user.getEmail());
        rs.setAge(user.getAge());
        rs.setAddress(user.getAddress());
        rs.setGender(user.getGender());
        rs.setCreateBy(user.getCreateBy());
        rs.setCreatedAt(user.getCreatedAt());
        return rs;
    }
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO rs = new ResUserDTO();
        rs.setId(user.getId());
        rs.setName(user.getName());
        rs.setEmail(user.getEmail());
        rs.setAge(user.getAge());
        rs.setAddress(user.getAddress());
        rs.setGender(user.getGender());
        rs.setCreateBy(user.getCreateBy());
        rs.setUpdateBy(user.getUpdateBy());
        rs.setCreatedAt(user.getCreatedAt());
        rs.setUpdatedAt(user.getUpdatedAt());
        return rs;
    }
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO rs = new ResUpdateUserDTO();
        rs.setId(user.getId());
        rs.setName(user.getName());
        rs.setEmail(user.getEmail());
        rs.setAge(user.getAge());
        rs.setAddress(user.getAddress());
        rs.setGender(user.getGender());
        rs.setCreateBy(user.getCreateBy());
        rs.setUpdateBy(user.getUpdateBy());
        rs.setCreatedAt(user.getCreatedAt());
        rs.setUpdatedAt(user.getUpdatedAt());
        return rs;
    }
    public void updateUserToken(String refreshToken,String email){
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken,String email){
       return this.userRepository.findByRefreshTokenAndEmail(refreshToken,email);
    }
}
