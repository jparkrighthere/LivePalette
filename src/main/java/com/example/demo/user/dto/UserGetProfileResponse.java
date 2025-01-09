package com.example.demo.user.dto;

import com.example.demo.user.Role;
import lombok.Data;

@Data
public class UserGetProfileResponse {
    private String username;
    private String email;
    private String profile;
    private Role userType;

    //디자이너 전용
    private String career;
    private String socialLink;
    private String[] portfolioImageList;
    private String portfolioDescription;
}
