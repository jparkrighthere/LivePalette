package com.example.demo.user.dto;

import lombok.Data;
import java.util.Optional;

@Data
public class UserUpdateProfileRequest {
    private Optional<String> username = Optional.empty();
    private Optional<String> profile = Optional.empty();
    private Optional<String> field = Optional.empty();
    private Optional<Long> career = Optional.empty();
}