package com.example.demo.auth.controller;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.dto.ClientSignupRequest;
import com.example.demo.auth.dto.DesignerSignupRequest;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.jwt.TokenStatus;
import com.example.demo.user.Role;
import com.example.demo.user.model.User;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PostMapping("/signup/client")
    public ResponseEntity<?> signupClient(@RequestBody ClientSignupRequest clientSignupRequest) {
        return signup(clientSignupRequest.getEmail(), clientSignupRequest.getPassword(),
                clientSignupRequest.getUsername(), null,null,Role.CLIENT);
    }
    @PostMapping("/signup/designer")
    public ResponseEntity<?> signupDesigner(@RequestBody DesignerSignupRequest designerSignupRequest) {
        return signup(designerSignupRequest.getEmail(), designerSignupRequest.getPassword(),
                designerSignupRequest.getUsername(), designerSignupRequest.getCareer(),
                designerSignupRequest.getSocialLink(), Role.DESIGNER);
    }

    private ResponseEntity<?> signup(String email,String password,String username,String career,String socialLink,Role userType) {
        try{
            //사용자 중복 확인
            if(userService.findByEmail(email) != null||userService.findByUsername(username) != null) {
                return ResponseEntity.status(409).body("Email or Username already exists");
            }

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setUserName(username);
            user.setCareer(career);
            user.setSocialLink(socialLink);
            user.setUserType(userType);

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
                ||!jwtUtil.isExistRefreshToken(reissueToken))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");

        if(jwtUtil.validateRefreshToken(reissueToken)==TokenStatus.EXPIRED) {
            jwtUtil.deleteRefreshToken(reissueToken);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired refresh token");
        }
        String email = jwtUtil.getUserEmailByRefreshToken(reissueToken);
        String username = jwtUtil.getUserNameByRefreshToken(reissueToken);
        String role = jwtUtil.getUserRoleByRefreshToken(reissueToken);

        User user = new User();
        user.setEmail(email);
        user.setUserName(username);
        user.setUserType(Role.valueOf(role));

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        //기존의 refresh token 삭제,새로 저장
        jwtUtil.deleteRefreshToken(reissueToken);
        jwtUtil.addRefreshToken(refreshToken,user.getEmail());

        //재발급시 refreshToken도 재발급 => Refresh Rotate 방식
        response.addHeader(AuthConstants.JWT_ISSUE_HEADER, AuthConstants.ACCESS_PREFIX + accessToken);
        response.addCookie(jwtUtil.createCookie(AuthConstants.REFRESH_PREFIX, refreshToken));

        return ResponseEntity.status(201).body("Reissue successful");
    }
}