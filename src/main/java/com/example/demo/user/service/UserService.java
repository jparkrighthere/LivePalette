package com.example.demo.user.service;

import com.example.demo.user.dto.UserUpdatePasswordRequest;
import com.example.demo.user.model.User;
import org.springframework.stereotype.Service;

import com.example.demo.user.Role;
import com.example.demo.user.dto.UserUpdateProfileRequest;
import java.util.List;

@Service
public interface UserService {
    User save(User user);
    List<User> findAll();
    User findByEmail(String email);
    User findByUsername(String username);
    void deleteByEmail(String email);
    void updateProfile(User user, UserUpdateProfileRequest updateUserRequest);
    void updatePassword(User user, UserUpdatePasswordRequest updatePasswordRequest);
    List<User> findUsersByRole(Role userType);
}
