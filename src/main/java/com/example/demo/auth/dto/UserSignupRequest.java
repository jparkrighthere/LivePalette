package com.example.demo.auth.dto;

import lombok.Data;

@Data
public class UserSignupRequest {
    private String username;
    private String password;
    private String email;
}
