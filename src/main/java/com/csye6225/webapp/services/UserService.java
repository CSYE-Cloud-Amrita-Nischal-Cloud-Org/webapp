package com.csye6225.webapp.services;

import com.csye6225.webapp.entity.UserEntity;
import com.csye6225.webapp.models.User;

public interface UserService {

    boolean isEmailValid(String emailAddress);

    boolean isPasswordValid(String password);

    UserEntity getUserByEmail(String email);

    UserEntity createUser(User user);

    String encryptPassword(String password);

    UserEntity validateUserByToken(String token);
}
