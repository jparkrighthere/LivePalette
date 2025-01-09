package com.example.demo.user.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.service.UserService;

import lombok.RequiredArgsConstructor;

import com.example.demo.user.dto.UserGetProfileResponse;
import com.example.demo.user.dto.UserUpdateProfileRequest;
import com.example.demo.user.model.User;

import java.util.Base64;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    // private final로, 필드 주입하지말고 생성자 주입으로 하는걸로, spring 실행되면 spring context
    // continer에 잇는 빈들을 번저 생성하고 등록, user controller로 빈으로 등록되어있으니까 생성할때 생성자 주입이 아니면 null을 갖고있어도
    // 오류가 안나는데 생성자 주입을 하게 되면 자동으로 생성해줘서 에러 방지 가능, 자동 생성해주니까 객체 생성할 필요가 없음
    //


    @PatchMapping("/profile/{base64Email}")
    public ResponseEntity<?> updateProfile(@PathVariable String base64Email, @RequestBody UserUpdateProfileRequest updateUserRequest) {
        // Base64로 인코딩된 이메일 디코딩
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenEmail = customUserDetail.getUsername();
        if (!decodedEmail.equals(tokenEmail)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User user = userService.findByEmail(decodedEmail);

        // 프로필 업데이트
        user = userService.updateProfile(user, updateUserRequest);

        return ResponseEntity.status(200).body("Profile updated successfully");
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

        User user = userService.findByEmail(decodedEmail);

        UserGetProfileResponse userGetProfileResponse = new UserGetProfileResponse();
        userGetProfileResponse.setUsername(user.getUserName());
        userGetProfileResponse.setEmail(user.getEmail());
        userGetProfileResponse.setProfile(user.getProfile());
        userGetProfileResponse.setUserType(user.getUserType());
        userGetProfileResponse.setCareer(user.getCareer());
        userGetProfileResponse.setSocialLink(user.getSocialLink());
        userGetProfileResponse.setPortfolioImageList(user.getPortfolioImageList());
        userGetProfileResponse.setPortfolioDescription(user.getPortfolioDescription());

        return ResponseEntity.status(200).body(userGetProfileResponse);
    }

}