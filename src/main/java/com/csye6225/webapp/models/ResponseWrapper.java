package com.csye6225.webapp.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseWrapper {

    private int code;

    private String message;

    public ResponseWrapper(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
