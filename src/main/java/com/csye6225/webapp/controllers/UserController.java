package com.csye6225.webapp.controllers;

import com.csye6225.webapp.entity.ImageEntity;
import com.csye6225.webapp.entity.UserEntity;
import com.csye6225.webapp.models.ResponseWrapper;
import com.csye6225.webapp.models.User;
import com.csye6225.webapp.services.ImageService;
import com.csye6225.webapp.services.UserService;
import com.csye6225.webapp.models.ResponseMessage;
import com.csye6225.webapp.utils.JsonUtils;
import com.timgroup.statsd.StatsDClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping(path = "/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService _userService;

    @Autowired
    private ImageService _imageService;

    @Autowired
    private StatsDClient _statsDClient;

    @PostMapping(path = "", produces = "application/json")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        _statsDClient.incrementCounter("endpoint.user.api.post");
        long currentTime = System.currentTimeMillis();
        log.info("[Create User] -> Initiated . . . ");
        UserEntity existingUser = _userService.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            log.info("[Create User] -> User already exists . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.post.failure.execution.time", currentTime);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.USER_ALREADY_EXISTS.getMessage())));
        }
        if (!this._userService.isEmailValid(user.getEmail())) {
            log.info("[Create User] -> Email is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.post.failure.execution.time", currentTime);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.INVALID_EMAIL.getMessage())));
        }
        if (!this._userService.isPasswordValid(user.getPassword())) {
            log.info("[Create User] -> Password is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.post.failure.execution.time", currentTime);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.INVALID_PASSWORD.getMessage())));
        }
        UserEntity newUser = _userService.createUser(user);
        log.info("Created New User");
        _statsDClient.recordExecutionTimeToNow("endpoint.user.api.post.success.execution.time", currentTime);
        return ResponseEntity.status(HttpStatus.CREATED).body(JsonUtils.toJson(newUser));
    }

    @GetMapping(path = "/self")
    public ResponseEntity<UserEntity> getUser(HttpServletRequest request) {
        log.info("[Get User] -> Initiated . . . ");
        _statsDClient.incrementCounter("endpoint.user.self.api.get");
        long currentTime = System.currentTimeMillis();
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            log.info("[Get User] -> Authorization is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.get.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = _userService.validateUserByToken(authorization);
        if (user == null) {
            log.info("[Get User] -> User authentication failed. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.get.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("[Get User] -> User found. . . . ");
        _statsDClient.recordExecutionTimeToNow("endpoint.user.api.get.success.execution.time", currentTime);
        return ResponseEntity.ok(user);
    }

    @PutMapping(path = "/self")
    public ResponseEntity<?> updateUser(@RequestBody User user, HttpServletRequest request) {
        log.info("[Update User] -> Initiated . . . ");
        _statsDClient.incrementCounter("endpoint.user.self.api.put");
        long currentTime = System.currentTimeMillis();
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            log.info("[Update User] -> Authorization is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.put.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity userEntity = _userService.validateUserByToken(authorization);
        if (userEntity == null) {
            log.info("[Update User] -> User does not exist. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.put.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!userEntity.getEmail().equals(user.getEmail())) {
            log.info("[Update User] -> Email is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.put.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (!this._userService.isPasswordValid(user.getPassword())) {
            log.info("[Update User] -> Password is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.api.put.failure.execution.time", currentTime);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.INVALID_PASSWORD.getMessage())));
        }

        log.info("[Update User] -> User information updated. . . . ");
        _statsDClient.recordExecutionTimeToNow("endpoint.user.api.put.success.execution.time", currentTime);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(_userService.updateUser(user));
    }

    @PostMapping(path = "/self/pic", produces = "application/json")
    public ResponseEntity<ImageEntity> addProfilePic(@RequestParam(value="profilePic") MultipartFile image,
                                                     HttpServletRequest request) {
        log.info("[Add Profile Pic] -> Initiated . . . ");
        _statsDClient.incrementCounter("endpoint.user.self.pic.api.post");
        long currentTime = System.currentTimeMillis();
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            log.info("[Add Profile Pic] -> Authorization is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.post.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity userEntity = _userService.validateUserByToken(authorization);
        if (userEntity == null) {
            log.info("[Add Profile Pic] -> User does not exist. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.post.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!_imageService.isImageValid(image)) {
            log.info("[Add Profile Pic] -> Image is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.post.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<ImageEntity> imageEntityOptional = _imageService.getImage(userEntity.getId());

        if (imageEntityOptional.isEmpty()) {
            log.info("[Add Profile Pic] -> Image currently does not have an image. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.post.success.execution.time", currentTime);
            log.info("[Add Profile Pic] -> Image successfully added. . . . ");
            return ResponseEntity.status(HttpStatus.CREATED).body(_imageService.addImage(image, userEntity.getId()));
        }

        log.info("[Add Profile Pic] -> User already has an image. . . . ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping(path = "/self/pic", produces = "application/json")
    public ResponseEntity<ImageEntity> getProfilePic(HttpServletRequest request) {
        log.info("[Get Profile Pic] -> Initiated . . . ");
        _statsDClient.incrementCounter("endpoint.user.self.pic.api.get");
        long currentTime = System.currentTimeMillis();
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            log.info("[Get Profile Pic] -> Authorization is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.get.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity userEntity = _userService.validateUserByToken(authorization);
        if (userEntity == null) {
            log.info("[Get Profile Pic] -> User does not exist. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.get.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<ImageEntity> imageEntityOptional = _imageService.getImage(userEntity.getId());

        if (imageEntityOptional.isEmpty()) {
            log.info("[Get Profile Pic] -> User does not have an image. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.get.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("[Get Profile Pic] -> Fetching the profile pic. . . . ");
        _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.get.success.execution.time", currentTime);
        return ResponseEntity.status(HttpStatus.OK).body(imageEntityOptional.get());
    }

    @DeleteMapping(path = "/self/pic")
    public ResponseEntity<Void> deleteProfilePic(HttpServletRequest request) {
        log.info("[Delete Profile Pic] -> Initiated . . . ");
        _statsDClient.incrementCounter("endpoint.user.self.pic.api.delete");
        long currentTime = System.currentTimeMillis();
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            log.info("[Delete Profile Pic] -> Authorization is invalid. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.delete.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity userEntity = _userService.validateUserByToken(authorization);
        if (userEntity == null) {
            log.info("[Delete Profile Pic] -> User does not exist. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.delete.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<ImageEntity> imageEntityOptional = _imageService.getImage(userEntity.getId());

        if (imageEntityOptional.isEmpty()) {
            log.info("[Delete Profile Pic] -> User does not have an image. . . . ");
            _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.delete.failure.execution.time", currentTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        _imageService.deleteFile(imageEntityOptional.get());
        log.info("[Delete Profile Pic] -> Profile pic deleted. . . . ");
        _statsDClient.recordExecutionTimeToNow("endpoint.user.self.pic.api.delete.success.execution.time", currentTime);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/self", method = {RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowedSelf() {
        _statsDClient.incrementCounter("endpoint.user.self.api.rest");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowed() {
        _statsDClient.incrementCounter("endpoint.user.api.rest");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @RequestMapping(value = "/self/pic", method = {RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowedSelfPic() {
        _statsDClient.incrementCounter("endpoint.user.self.pic.api.rest");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
