package com.example.demo.user.controller;

import com.example.demo.user.dto.UserVrfEmailResponse;
import com.example.demo.user.dto.UserUpdatePasswordRequest;
import com.example.demo.user.email.EmailUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.service.UserService;

import lombok.RequiredArgsConstructor;

import com.example.demo.user.Role;
import com.example.demo.user.dto.UserGetProfileResponse;
import com.example.demo.user.dto.UserUpdateProfileRequest;
import com.example.demo.user.model.User;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailUtil emailUtil;

    @PatchMapping("/profile/{base64Email}")
    public ResponseEntity<?> updateProfile(@PathVariable String base64Email, @RequestBody UserUpdateProfileRequest updateUserRequest) {
        // Base64로 인코딩된 이메일 디코딩
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenEmail = customUserDetail.getUsername();
        // 토큰의 이메일과 디코딩된 이메일이 다르면
        if (!decodedEmail.equals(tokenEmail)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User user = userService.findByEmail(decodedEmail);
        // 유저가 없으면
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // 유저네임이 이미 존재하면
        if (userService.findByUsername(updateUserRequest.getUserName()) != null) {
            return ResponseEntity.status(409).body("Username already exists");
        }

        // 프로필 업데이트
        userService.updateProfile(user, updateUserRequest);

        return ResponseEntity.status(200).body("Profile updated successfully");
    }

    @GetMapping("/profile/{base64Email}")
    public ResponseEntity<?> getProfile(@PathVariable String base64Email) {
        // Base64로 인코딩된 이메일 디코딩
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenEmail = customUserDetail.getUsername();
        // 토큰의 이메일과 디코딩된 이메일이 다르면 
        if (!decodedEmail.equals(tokenEmail)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User user = userService.findByEmail(decodedEmail);
        // 유저가 없으면
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // 프로필 조회
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

    @GetMapping("/designers")
    public ResponseEntity<?> getDesigners() {
        List<User> designers = userService.findUsersByRole(Role.DESIGNER);
        if (designers.isEmpty()) {
            return ResponseEntity.status(404).body("No designers found");
        }
        return ResponseEntity.status(200).body(designers);
    }

    @DeleteMapping("/delete/{base64Email}")
    public ResponseEntity<?> deleteUser(@PathVariable String base64Email) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenEmail = customUserDetail.getUsername();
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));
        User user = userService.findByEmail(tokenEmail);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        if(user.getUserType() != Role.ADMIN){
            if(!decodedEmail.equals(tokenEmail)){
                return ResponseEntity.status(403).body("Forbidden");
            }
        }
        userService.deleteByEmail(user.getEmail());
        return ResponseEntity.status(200).body("User deleted successfully");
    }

    @GetMapping("/password/{base64Email}")
    public ResponseEntity<?> authUserPW(@PathVariable String base64Email) {
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));

        User user = userService.findByEmail(decodedEmail);
        // 유저가 없으면
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        //인증 이메일 전송
        String authNum = emailUtil.sendEmail(decodedEmail);
        if (authNum == null) {
            return ResponseEntity.status(403).body("Something went wrong");
        }

        UserVrfEmailResponse userVrfEmailResponse = new UserVrfEmailResponse();
        userVrfEmailResponse.setAuthNum(authNum);

        return ResponseEntity.status(200).body(userVrfEmailResponse);
    }

    @PatchMapping("/password/{base64Email}")
    public ResponseEntity<?> updatePassword(@PathVariable String base64Email, @RequestBody UserUpdatePasswordRequest updatePasswordRequest) {
        // Base64로 인코딩된 이메일 디코딩
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));

        User user = userService.findByEmail(decodedEmail);
        // 유저가 없으면
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        //AuthNum Check
        Boolean Checked = emailUtil.CheckAuthNum(decodedEmail,updatePasswordRequest.getAuthNum());
        if(!Checked){
            return ResponseEntity.status(403).body("Wrong Authorization Number");
        }

        //비밀번호 업데이트
        userService.updatePassword(user, updatePasswordRequest);

        return ResponseEntity.status(200).body("Password updated successfully");
    }

}