package com.example.demo.auth.repository;

import com.example.demo.auth.model.RefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository {
    void save(RefreshToken entity);

    String findTokenByEmail(String email);

    String findEmailByToken(String token);

    void deleteByToken(String token);
}
