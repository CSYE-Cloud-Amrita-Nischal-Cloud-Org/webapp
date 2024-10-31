package com.csye6225.webapp.services.impl;

import com.csye6225.webapp.entity.UserEntity;
import com.csye6225.webapp.models.User;
import com.csye6225.webapp.repository.UserRepository;
import com.csye6225.webapp.services.UserService;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder _passwordEncoder = new BCryptPasswordEncoder();

    private final String BASIC_AUTH = "Basic ";

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    StatsDClient _statsDClient;

    @Override
    public boolean isEmailValid(String emailAddress) {
        String emailPattern = "[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        return emailAddress.matches(emailPattern);
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        long currentTime = Instant.now().toEpochMilli();
        UserEntity userEntity = _userRepository.findByemail(email);
        _statsDClient.recordExecutionTimeToNow("find.user.by.email.execution.time", currentTime);
        return userEntity;
    }

    @Override
    public UserEntity createUser(User user) {
        Instant now = Instant.now();
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(encryptPassword(user.getPassword()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accountCreated(now.toString())
                .accountUpdated(now.toString())
                .build();
        userEntity = saveUser(userEntity);
        userEntity.setPassword(null);
        return userEntity;
    }

    @Override
    public String encryptPassword(String password) {
        return _passwordEncoder.encode(password);
    }

    @Override
    public UserEntity validateUserByToken(String token) {
        Pair<String, String> emailPassPair = getEmailPasswordPair(token);
        log.info("username/email_address = {}", emailPassPair.getLeft());
        UserEntity user = getUserByEmail(emailPassPair.getLeft());
        log.info("user = {}", user);
        if(user == null) {
            log.info("user not found");
            return null;
        }
        if (!_passwordEncoder.matches(emailPassPair.getRight(), user.getPassword())) {
            log.info("Password does not match");
            return null;
        }
        return user;
    }

    @Override
    public UserEntity updateUser(User user) {
        UserEntity userEntity = getUserByEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPassword(encryptPassword(user.getPassword()));
        userEntity.setAccountUpdated(Instant.now().toString());
        userEntity = saveUser(userEntity);
        userEntity.setPassword(null);
        return userEntity;
    }

    private Pair<String, String> getEmailPasswordPair(String token) {
        String base64Credentials = token.substring(BASIC_AUTH.length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        // credentials = username:password
        final int delimiterIndex = credentials.indexOf(":");
        String email = credentials.substring(0, delimiterIndex);
        String password = credentials.substring(delimiterIndex + 1);
        return new ImmutablePair<>(email, password);
    }

    private UserEntity saveUser(UserEntity user) {
        long currentTime = Instant.now().toEpochMilli();
        UserEntity userEntity = _userRepository.save(user);
        _statsDClient.recordExecutionTimeToNow("save.user.execution.time", currentTime);
        return userEntity;
    }

}
