package com.csye6225.webapp.models;

import lombok.Getter;

@Getter
public enum ResponseMessage {

    USER_ALREADY_EXISTS("User already exists!!"),
    INVALID_EMAIL("Email address is invalid!!"),
    USER_VERIFIED("User verification successful!!"),
    INVALID_PASSWORD("Password must be 8 or more characters");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

}
