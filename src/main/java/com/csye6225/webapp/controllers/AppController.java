package com.csye6225.webapp.controllers;

import com.csye6225.webapp.services.DbConnection;
import com.timgroup.statsd.StatsDClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@Slf4j
public class AppController {

    @Autowired
    DbConnection _dbConnection;

    @Autowired
    private StatsDClient _statsDClient;

    @GetMapping(path = "/healthz")
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        _statsDClient.incrementCounter("endpoint.healthcheck.api.get");
        long currentTime = System.currentTimeMillis();
        log.debug("[Health Check] -> Initiated . . . ");
        if (request.getContentLength() > 0 || !request.getParameterMap().isEmpty()) {
            log.error("[Health Check] -> Request contains invalid payload");
            _statsDClient.recordExecutionTimeToNow("endpoint.healthcheck.api.get.failure.execution.time", currentTime);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("cache-control", "no-cache")
                    .build();
        }
        if (BooleanUtils.isNotTrue(_dbConnection.isDbConnected())) {
            log.error("[Health Check] -> Database is not connected");
            _statsDClient.recordExecutionTimeToNow("endpoint.healthcheck.api.get.failure.execution.time", currentTime);
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("cache-control", "no-cache")
                    .build();
        }
        log.info("[Health Check] -> Database is connected");
        _statsDClient.recordExecutionTimeToNow("endpoint.healthcheck.api.get.success.execution.time", currentTime);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("cache-control", "no-cache")
                .build();
    }

    @RequestMapping(value = "/healthz", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowed() {
        _statsDClient.incrementCounter("endpoint.healthcheck.api.rest");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

}
