package com.csye6225.webapp.services;

public interface EmailAuthTokenService {

    String getEmailAuthToken(String email, String time);

    String bytesToHex(byte[] bytes);

    String getEmailFromToken(String email);
}
