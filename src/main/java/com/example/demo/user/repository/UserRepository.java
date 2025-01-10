package com.example.demo.user.repository;

import com.example.demo.user.Role;
import com.example.demo.user.model.User;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUserName(String userName);
    List<User> findByUserType(Role userType);
}
