package com.example.demo.auth.repository;

import com.example.demo.auth.model.RefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshRepositoryImpl implements RefreshRepository {
    @Override
    public void save(RefreshToken entity) {

    }

    @Override
    public String findTokenByEmail(String email) {
        return "";
    }

    @Override
    public String findEmailByToken(String token) {
        return "";
    }

    @Override
    public void deleteByToken(String token) {

    }
}
