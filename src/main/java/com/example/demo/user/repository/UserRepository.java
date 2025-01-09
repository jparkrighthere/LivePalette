package com.example.demo.user.repository;

import com.example.demo.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    //이렇게만 하면 username으로 디비에서 값을 조회할 수 있는건지 의문이다.
}
