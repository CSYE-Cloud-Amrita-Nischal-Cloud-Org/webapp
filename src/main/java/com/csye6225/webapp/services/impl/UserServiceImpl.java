package com.csye6225.webapp.services.impl;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;
import com.csye6225.webapp.entity.EmailVerificationEntity;
import com.csye6225.webapp.entity.UserEntity;
import com.csye6225.webapp.models.User;
import com.csye6225.webapp.repository.EmailVerificationsRepository;
import com.csye6225.webapp.repository.UserRepository;
import com.csye6225.webapp.services.EmailAuthTokenService;
import com.csye6225.webapp.services.UserService;
import com.csye6225.webapp.utils.JsonUtils;
import com.timgroup.statsd.StatsDClient;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder _passwordEncoder = new BCryptPasswordEncoder();

    private final String BASIC_AUTH = "Basic ";

    private AmazonSNSAsync _amazonSNSClient;

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    StatsDClient _statsDClient;

    @Autowired
    private EmailAuthTokenService _emailAuthTokenService;

    @Autowired
    private EmailVerificationsRepository _emailVerificationsRepository;

    @PostConstruct
    public void initializeSNSClient() {
        _amazonSNSClient = AmazonSNSAsyncClientBuilder.defaultClient();
    }

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
        long currentTime = System.currentTimeMillis();
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
                .isVerified(false)
                .build();
        userEntity = saveUser(userEntity);
        userEntity.setPassword(null);
        generateTokenAndSendMessage(user.getEmail());
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

    @Override
    public Boolean validateVerificationToken(String token) {
        Optional<EmailVerificationEntity> emailVerificationEntity = _emailVerificationsRepository.findByToken(token);
        if (emailVerificationEntity.isPresent()) {
            EmailVerificationEntity verificationEntity = emailVerificationEntity.get();
            Instant expirationTime = Instant.parse(verificationEntity.getExpirationTime());
            if (Instant.now().isBefore(expirationTime)) {
                String email = _emailAuthTokenService.getEmailFromToken(token);
                UserEntity userEntity = getUserByEmail(email);
                userEntity.setIsVerified(true);
                _userRepository.save(userEntity);
                return true;
            }
            return false;
        }
        return null;
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
        long currentTime = System.currentTimeMillis();
        UserEntity userEntity = _userRepository.save(user);
        _statsDClient.recordExecutionTimeToNow("save.user.execution.time", currentTime);
        return userEntity;
    }

    private void generateTokenAndSendMessage(String email)
    {
        Instant now = Instant.now();
        String expirationTime = now.plusSeconds(120).toString();
        String token = _emailAuthTokenService.getEmailAuthToken(email, expirationTime);
        EmailVerificationEntity emailVerificationEntity = EmailVerificationEntity.builder()
                        .email(email)
                        .token(token)
                        .expirationTime(expirationTime)
                        .build();
        _emailVerificationsRepository.save(emailVerificationEntity);
        sendMessage(email, token);
    }

    @SneakyThrows
    public void sendMessage(String emailId, String token) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", emailId);
        payload.put("token", token);
        String message = JsonUtils.toJson(payload);
        log.info("Sending Message - {} ", message);

        String topicArn = getTopicArn("email_verification");
        PublishRequest publishRequest = new PublishRequest(topicArn, message);
        Future<PublishResult> publishResultFuture = _amazonSNSClient.publishAsync(publishRequest);
        String messageId = publishResultFuture.get().getMessageId();

        log.info("Send Message {} with message Id {} ", message, messageId);
    }

    public String getTopicArn(String topicName) {

        String topicArn = null;

        try {
            Topic topic = _amazonSNSClient.listTopicsAsync().get().getTopics().stream()
                    .filter(t -> t.getTopicArn().contains(topicName)).findAny().orElse(null);

            if (null != topic) {
                topicArn = topic.getTopicArn();
            } else {
                log.info("No Topic found by the name : {}", topicName);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        log.info("Arn corresponding to topic name {} is {} ", topicName, topicArn);

        return topicArn;

    }
}
