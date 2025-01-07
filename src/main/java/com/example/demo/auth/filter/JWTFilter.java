package com.example.demo.auth.filter;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.jwt.TokenStatus;
import com.example.demo.user.Role;
import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //permittedURI는 토큰 검사를 진행하지 않는다.
        if(isPermittedURI(request.getRequestURI())){
            log.info("허용된 uri : " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        //request 에서 Authorization 헤더를 찾음.
        String authorizationHeader = request.getHeader(AuthConstants.JWT_ISSUE_HEADER);
        String token = authorizationHeader.replace(AuthConstants.ACCESS_PREFIX, "");

        if(jwtUtil.validateAccessToken(token)==TokenStatus.INVALID) {
            log.error("Token is invalid");
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token invalid");
            //response status
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if(jwtUtil.validateAccessToken(token)==TokenStatus.EXPIRED) {
            log.error("Token is expired");
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            //response status
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String userName = jwtUtil.getUserNameByAccessToken(token);
        String userEmail = jwtUtil.getUserEmailByAccessToken(token);
        String role = jwtUtil.getUserRoleByAccessToken(token);
        User user = new User();
        user.setUsername(userName);
        user.setEmail(userEmail);
        user.setPassword("tmp");
        user.setRole(Role.valueOf(role));

        CustomUserDetail customUserDetail = new CustomUserDetail(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetail, null, customUserDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
    private boolean isPermittedURI(String uri) {
        return Arrays.stream(AuthConstants.PERMITTED_URI)
                .anyMatch(permitted->{
                    String replace = permitted.replace("*","");
                    return uri.contains(replace)||replace.contains(uri);
                });
    }
}
