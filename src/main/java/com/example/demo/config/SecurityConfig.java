package com.example.demo.config;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.filter.CustomLogoutFilter;
import com.example.demo.auth.filter.JWTFilter;
import com.example.demo.auth.filter.LoginFilter;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.model.RefreshToken;
import com.example.demo.auth.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //authenticationManager가 인자로 받을 AuthenticationConfiguration객체
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception {
        return authenticationConfiguration.getAuthenticationManager();

    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //disable crsf
                .formLogin(AbstractHttpConfigurer::disable)// disable formLogin
                .httpBasic(AbstractHttpConfigurer::disable)//disable http basic
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(AuthConstants.ADMIN_URI).hasRole("ADMIN")
                        .requestMatchers(AuthConstants.PERMITTED_URI).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new CustomLogoutFilter(jwtUtil), LogoutFilter.class)
                .addFilterBefore(new JWTFilter(jwtUtil),LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil,objectMapper()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위해 BCrypt 사용
    }
    @Bean
    public ObjectMapper objectMapper() {return new ObjectMapper();}

}
