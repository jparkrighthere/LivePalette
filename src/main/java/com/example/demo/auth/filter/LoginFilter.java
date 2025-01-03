package com.example.demo.auth.filter;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.user.model.CustomUserDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {

        //클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        //로그 찍어보기
        log.info("username: {}", username);
        log.info("password: {}", password);


        //spring security에서 검증하기 위해서는 username, password를   token에 담아야 한다.
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        //token을 authenticationManager로 전달
        return authenticationManager.authenticate(authRequest);
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain,Authentication authentication){
        //login 성공시 실행하는 로직
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(customUserDetail.getUser());
        String refreshToken = jwtUtil.generateRefreshToken(customUserDetail.getUser());
        log.info("refreshToken: {}", refreshToken);
        log.info("accessToken: {}", accessToken);

        //redis 설정이 완료되면 테스트 해봐야 함
        //jwtUtil.addRefreshToken(refreshToken, customUserDetail.getUser().getEmail());

        response.addHeader(AuthConstants.JWT_ISSUE_HEADER, AuthConstants.ACCESS_PREFIX + accessToken);
        response.addCookie(jwtUtil.createCookie(AuthConstants.REFRESH_PREFIX, refreshToken));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException failed){
        //login 실패시 실행하는 로직
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
