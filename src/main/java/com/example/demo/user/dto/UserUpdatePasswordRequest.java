package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
@Data
public class UserUpdatePasswordRequest {
    @NotEmpty(message = "인증 번호를 입력해 주세요")
    private String authNum;

    @NotEmpty(message = "새로운 비밀번호를 입력해 주세요")
    private String password;
}
