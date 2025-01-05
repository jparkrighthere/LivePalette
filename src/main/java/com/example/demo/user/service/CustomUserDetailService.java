package com.example.demo.user.service;

import com.example.demo.user.dto.UpdateUserRequest;
import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService,UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //CustomUserDetails 객체를 생성하여 넘겨줘야한다.
        User user = findByEmail(email);

        if (user == null)
            return null;
        return new CustomUserDetail(user);//UserDetail instance 에 넘겨주면 AuthenicationManager가 검증한다.
    }

    //UserService method//
    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findById(email).orElse(null);
    }

    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteById(email);
    }

    public User updateProfile(User user, UpdateUserRequest updateUserRequest) {
        // 업데이트할 필드들 처리
        updateUserRequest.getUsername().ifPresent(user::setUsername);
        updateUserRequest.getProfile().ifPresent(user::setProfile);
        updateUserRequest.getField().ifPresent(user::setField);
        updateUserRequest.getCareer().ifPresent(user::setCareer);

        userRepository.save(user);
        return user;
    }
}