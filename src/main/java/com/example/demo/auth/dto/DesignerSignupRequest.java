package com.example.demo.auth.dto;

import lombok.Data;

@Data
public class DesignerSignupRequest {
    private String username;
    private String password;
    private String email;
    private String career;
    private String socialLink;
}
