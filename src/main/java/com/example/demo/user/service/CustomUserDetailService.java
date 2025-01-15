package com.example.demo.user.service;

import com.example.demo.user.Role;
import com.example.demo.user.dto.UserUpdatePasswordRequest;
import com.example.demo.user.dto.UserUpdateProfileRequest;
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
import org.springframework.transaction.annotation.Transactional;

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
    public User findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteById(email);
    }

    public void updateProfile(User user, UserUpdateProfileRequest updateUserRequest) {
        // 필수 필드인 userName, userType은 null이 아니므로 null 체크를 하지 않는다.
        if (updateUserRequest.getUserName() != null) {
            user.setUserName(updateUserRequest.getUserName());
        }
        if (updateUserRequest.getUserType() != null) {
            user.setUserType(updateUserRequest.getUserType());
        }

        // 선택 필드는 null일 경우에만 null로 설정한다.
        if (updateUserRequest.getProfile() != null) {
            user.setProfile(updateUserRequest.getProfile());
        }
        else if (user.getProfile() != null && updateUserRequest.getProfile() == null) {
            user.setProfile(null);
        }

        if (updateUserRequest.getCareer() != null) {
            user.setCareer(updateUserRequest.getCareer());
        }
        else if (user.getCareer() != null && updateUserRequest.getCareer() == null) {
            user.setCareer(null);
        }

        if (updateUserRequest.getSocialLink() != null) {
            user.setSocialLink(updateUserRequest.getSocialLink());
        }
        else if (user.getSocialLink() != null && updateUserRequest.getSocialLink() == null) {
            user.setSocialLink(null);
        }

        if (updateUserRequest.getPortfolioImageList() != null) {
            user.setPortfolioImageList(updateUserRequest.getPortfolioImageList());
        }
        else if (user.getPortfolioImageList() != null && updateUserRequest.getPortfolioImageList() == null) {
            user.setPortfolioImageList(null);
        }

        if (updateUserRequest.getPortfolioDescription() != null) {
            user.setPortfolioDescription(updateUserRequest.getPortfolioDescription());
        }
        else if (user.getPortfolioDescription() != null && updateUserRequest.getPortfolioDescription() == null) {
            user.setPortfolioDescription(null);
        }

        userRepository.save(user);
    }

    public void updatePassword(User user, UserUpdatePasswordRequest updatePasswordRequest) {
        //NonEmpty로 비밀번호 필수 필드
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));
        log.info(user.toString());
        userRepository.save(user);
        log.info(user.toString());
    }


    public List<User> findUsersByRole(Role userType) {
        return userRepository.findByUserType(userType);
    }
}