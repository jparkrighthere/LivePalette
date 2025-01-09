package com.example.demo.auth.dto;

import lombok.Data;

@Data
public class ClientSignupRequest {
    private String username;
    private String password;
    private String email;
}
