package com.example.demo.user.service;

import com.example.demo.user.Role;
import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    //private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //CustomUserDetails 객체를 생성하여 넘겨줘야한다.
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("1234"));
        user.setRole(Role.ADMIN);
        user.setName("admin");
        if(email.equals("admin")){
            log.info("User is admin");
            return new CustomUserDetail(user);}
        user = findByEmail(email);

        if (user == null)
            return null;
        return new CustomUserDetail(user);//UserDetail instance 에 넘겨주면 AuthenicationManager가 검증한다.
    }

    //UserRepository method//
    public User findByEmail(String email){
        return null;
    }

    public void save(User user){
    }
}
