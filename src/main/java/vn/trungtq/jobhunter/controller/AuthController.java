package vn.trungtq.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.request.ReqLoginDTO;
import vn.trungtq.jobhunter.domain.response.ResLoginDTO;
import vn.trungtq.jobhunter.service.UserService;
import vn.trungtq.jobhunter.util.SecurityUtil;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private  final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${trungtq.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SecurityUtil securityUtil,
                          UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                reqLoginDTO.getUsername(), reqLoginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User userDb= this.userService.handleGetUserByUsername(reqLoginDTO.getUsername());

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                userDb.getId(),
                userDb.getName(),
                userDb.getEmail());
        res.setUser(userLogin);
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(),res.getUser());
        res.setAccessToken(accessToken);

        //create refresh token
        String refreshToken =  this.securityUtil.createRefreshToken(res);
        System.out.println(refreshToken);
        this.userService.updateUserToken(refreshToken, reqLoginDTO.getUsername());

        //set cookie
        ResponseCookie resCookies = ResponseCookie.from("refresh_token",refreshToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(refreshTokenExpiration)
//                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString()) // Thêm cookie vào tiêu đề Set-Cookie
                .body(res);
    }

    @GetMapping("/account")
    @ApiMessage("Get user information")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User userDb = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount =  new ResLoginDTO.UserGetAccount();
        if (userDb != null) {
            userLogin.setEmail(userDb.getEmail());
            userLogin.setName(userDb.getName());
            userLogin.setId(userDb.getId());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }
    @GetMapping("/refresh")
    @ApiMessage("Refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refreshToken) throws IdInvalidException {
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        //Check user by refreshToken and email
        User currentUser =  this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }
        ResLoginDTO res = new ResLoginDTO();
        User userDb= this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                userDb.getId(),
                userDb.getName(),
                userDb.getEmail());
        res.setUser(userLogin);
        String accessToken = this.securityUtil.createAccessToken(email,res.getUser());
        res.setAccessToken(accessToken);

        //create refresh token
        String newRefreshToken =  this.securityUtil.createRefreshToken(res);
        System.out.println(refreshToken);
        this.userService.updateUserToken(newRefreshToken,email);

        //set cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token",newRefreshToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(refreshTokenExpiration)
//                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString()) // Thêm cookie vào tiêu đề Set-Cookie
                .body(res);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if(email.equals("")){
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        //xóa refresh token khỏi database
        this.userService.updateUserToken(null,email);
        // Xóa cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token",null)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).build();
    }
}
