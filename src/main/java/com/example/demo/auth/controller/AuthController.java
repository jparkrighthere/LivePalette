package com.example.demo.auth.controller;

import com.example.demo.auth.dto.LoginResponse;
import com.example.demo.auth.dto.UserLoginRequest;
import com.example.demo.auth.dto.UserSignupRequest;
import com.example.demo.auth.service.JwtService;
import com.example.demo.user.model.User;
import com.example.demo.user.service.CustomUserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomUserDetailService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest, HttpServletResponse response) {
        try{
            //사용자 검증
            User user = userService.findByEmail(loginRequest.getEmail());
            if(user == null||!user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).body("Invalid email or password");
            }
            //AccessToken 및 refreshToken 생성
            String accessToken = jwtService.generateAccessToken(response,user);
            String refreshToken = jwtService.generateRefreshToken(response,user);

            return ResponseEntity.status(201).body(new LoginResponse(accessToken,refreshToken));
        }catch (Exception e) {
            log.error("Login error: {}",e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error");
        }
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
            user.setPassword(userSignupRequest.getPassword());//비밀번호는 해싱이 필요하다.
            user.setName(userSignupRequest.getUsername());

            userService.save(user);
            return ResponseEntity.status(201).body("Signup successful");
        } catch (Exception e) {
            log.error("Sign up error: {}",e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response) {
        try{
            //쿠키에서 토큰을 제거
            jwtService.resetTokenToCookies(response);
            return ResponseEntity.status(200).body("Logout successful");
        } catch (Exception e) {
            log.error("logout error: {}",e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
