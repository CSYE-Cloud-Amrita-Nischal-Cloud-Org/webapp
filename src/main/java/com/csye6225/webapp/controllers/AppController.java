package com.csye6225.webapp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class AppController {

    @GetMapping(path = "/healthz")
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        if (request.getContentLength() > 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("cache-control", "no-cache")
                    .build();
        }
        return ResponseEntity.ok().build();
    }

}
