package com.example.demo.user.dto;

import lombok.Data;

@Data
public class UserGetProfileResponse {
    private String username;
    private String email;
    private String profile;
    private String field;
    private Long career;
}
