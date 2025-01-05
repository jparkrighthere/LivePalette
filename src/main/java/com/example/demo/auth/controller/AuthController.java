package com.example.demo.auth.controller;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.dto.UserSignupRequest;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.jwt.TokenStatus;
import com.example.demo.user.Role;
import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.model.User;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @GetMapping("/admin/test")
    public ResponseEntity<?> adminTest(HttpServletResponse response) {
        //get current username
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //get current user role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority grantedAuthority = iterator.next();
        String role = grantedAuthority.getAuthority();
        return ResponseEntity.status(200).body("admin/test success : " +userDetail.getUser().getUsername()+"["+role+"]");
    }
    @GetMapping("/test")
    public ResponseEntity<?> publicTest(HttpServletResponse response) {

        return ResponseEntity.status(200).body("/test success");
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest userSignupRequest) {
        try{
            //사용자 중복 확인
            if(userService.findByEmail(userSignupRequest.getEmail()) != null) {
                return ResponseEntity.status(409).body("Email already exists");
            }

            User user = new User();
            user.setEmail(userSignupRequest.getEmail());
            user.setPassword(userSignupRequest.getPassword());
            user.setUsername(userSignupRequest.getUsername());
            user.setRole(Role.USER);//회원가입으로는 항상 user 롤을 부여하도록 한다.

            userService.save(user);
            return ResponseEntity.status(201).body("Signup successful");
        } catch (Exception e) {
            log.error("Sign up error: {}",e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request,HttpServletResponse response) {
        String reissueToken = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(AuthConstants.REFRESH_PREFIX))
                reissueToken = cookie.getValue();
        }
        if(jwtUtil.validateRefreshToken(reissueToken)== TokenStatus.INVALID
                /*||jwtUtil.isExistRefreshToken(reissueToken)*/)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");

        if(jwtUtil.validateRefreshToken(reissueToken)==TokenStatus.EXPIRED)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired refresh token");

        String email = jwtUtil.getUserEmailByRefreshToken(reissueToken);
        String username = jwtUtil.getUserNameByRefreshToken(reissueToken);
        String role = jwtUtil.getUserRoleByRefreshToken(reissueToken);

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setRole(Role.valueOf(role));

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        //기존의 refresh token 삭제,새로 저장
        //jwtUtil.deleteRefreshToken(reissueToken);
        //jwtUtil.addRefreshToken(refreshToken,user.getEmail());

        //재발급시 refreshToken도 재발급 => Refresh Rotate 방식
        response.addHeader(AuthConstants.JWT_ISSUE_HEADER, AuthConstants.ACCESS_PREFIX + accessToken);
        response.addCookie(jwtUtil.createCookie(AuthConstants.REFRESH_PREFIX, refreshToken));

        return ResponseEntity.status(201).body("Reissue successful");
    }
}