package com.example.demo.user.dto;

import com.example.demo.user.Role;
import lombok.Data;
import java.util.Optional;

@Data
public class UserUpdateProfileRequest {
    private Optional<String> username = Optional.empty();
    private Optional<String> profile = Optional.empty();
    private Optional<Role> userType = Optional.empty();
    private Optional<String> career = Optional.empty();
    private Optional<String> socialLink = Optional.empty();
    private Optional<String[]> portfolioImageList = Optional.empty();
    private Optional<String> portfolioDescription = Optional.empty();
}