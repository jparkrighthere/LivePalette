package com.example.demo.user.model;

import com.example.demo.user.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String email;
    private String password;
    private String userName;
    private String profile;
    private Role userType;


    //디자이너 전용
    private String career;
    private String socialLink;
    private String[] portfolioImageList;
    private String portfolioDescription;

}