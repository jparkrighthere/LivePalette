package com.example.demo.user.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.service.CustomUserDetailService;
import com.example.demo.user.service.UserService;

import lombok.RequiredArgsConstructor;

import com.example.demo.user.dto.UpdateUserRequest;
import com.example.demo.user.model.User;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;
    private final CustomUserDetailService customUserDetailService;

    @PatchMapping("/profile/{base64Email}")
    public ResponseEntity<?> updateProfile(@PathVariable String base64Email, @RequestBody UpdateUserRequest updateUserRequest) {
        // Base64로 인코딩된 이메일 디코딩
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenEmail = customUserDetail.getUsername();
        if (!decodedEmail.equals(tokenEmail)) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        UserDetails userDetails = customUserDetailService.loadUserByUsername(decodedEmail);
        User user = ((CustomUserDetail) userDetails).getUser();

        // 프로필 업데이트
        user = userService.updateProfile(user, updateUserRequest);

        return ResponseEntity.status(200).body(user);
    }

    @GetMapping("/profile/{base64Email}")
    public ResponseEntity<?> getProfile(@PathVariable String base64Email) {
        // Base64로 인코딩된 이메일 디코딩
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenEmail = customUserDetail.getUsername();
        if (!decodedEmail.equals(tokenEmail)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        UserDetails userDetails = customUserDetailService.loadUserByUsername(decodedEmail);
        User user = ((CustomUserDetail) userDetails).getUser();

        return ResponseEntity.status(200).body(user);
    }
}