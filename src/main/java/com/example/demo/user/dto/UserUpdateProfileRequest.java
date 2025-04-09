package com.example.demo.user.dto;

import com.example.demo.user.Role;
import lombok.Data;

@Data
public class UserUpdateProfileRequest {
    private String userName;
    private String profile;
    private Role userType;
    private String career;
    private String socialLink;
    private String[] designerDetails;
    private String portfolioDescription;
}
