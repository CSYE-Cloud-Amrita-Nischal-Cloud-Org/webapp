package com.csye6225.webapp.controllers;

import com.csye6225.webapp.entity.UserEntity;
import com.csye6225.webapp.models.ResponseWrapper;
import com.csye6225.webapp.models.User;
import com.csye6225.webapp.services.UserService;
import com.csye6225.webapp.models.ResponseMessage;
import com.csye6225.webapp.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService _userService;

    @PostMapping(path = "", produces = "application/json")
    public ResponseEntity<String> createUser(@RequestBody User user) throws JsonProcessingException {

        UserEntity existingUser = _userService.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.USER_ALREADY_EXISTS.getMessage())));
        }
        if (!this._userService.isEmailValid(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.INVALID_EMAIL.getMessage())));
        }
        if (!this._userService.isPasswordValid(user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.INVALID_PASSWORD.getMessage())));
        }
        UserEntity newUser = _userService.createUser(user);
        log.info("Created New User");
        return ResponseEntity.status(HttpStatus.CREATED).body(JsonUtils.toJson(newUser));
    }

    @GetMapping(path = "/self")
    public ResponseEntity<UserEntity> getUser(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = _userService.validateUserByToken(authorization);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping(path = "/self")
    public ResponseEntity<?> updateUser(@RequestBody User user, HttpServletRequest request) throws JsonProcessingException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity userEntity = _userService.validateUserByToken(authorization);
        if (userEntity == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!userEntity.getEmail().equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (!this._userService.isPasswordValid(user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.toJson(new ResponseWrapper(HttpStatus.BAD_REQUEST.value(),
                            ResponseMessage.INVALID_PASSWORD.getMessage())));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(_userService.updateUser(user));
    }

    @RequestMapping(value = "/self", method = {RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowedSelf() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

}
