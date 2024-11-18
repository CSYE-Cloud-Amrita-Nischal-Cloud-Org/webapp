package com.csye6225.webapp.services.impl;

import com.csye6225.webapp.services.EmailAuthTokenService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class EmailAuthTokenServiceImpl implements EmailAuthTokenService {

    @Override
    @SneakyThrows
    public String getEmailAuthToken(String email, String time) {
        // Convert email and time into byte arrays
        byte[] userEmailBytes = email.getBytes(StandardCharsets.UTF_8);
        byte[] expirationTimeBytes = time.getBytes(StandardCharsets.UTF_8);

        // Create HMAC SHA256 hash using the expirationTime as the key
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(expirationTimeBytes, "HmacSHA256");
        hmac.init(secretKey);

        // Generate the HMAC digest
        byte[] hmacDigestBytes = hmac.doFinal(userEmailBytes);

        // Convert the HMAC digest and email to hexadecimal strings
        String hmacDigestHex = bytesToHex(hmacDigestBytes);
        String userEmailHex = bytesToHex(userEmailBytes);

        // Combine the HMAC hash and hex-encoded email
        return hmacDigestHex + userEmailHex;
    }

    @Override
    public String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    @SneakyThrows
    public String getEmailFromToken(String token) {
        // Split the token to get the email in hex
        String userEmailHex = token.substring(64);

        // Convert userEmailHex back to the original email
        return hexToString(userEmailHex);

    }

    private String hexToString(String hex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            result.append((char) Integer.parseInt(str, 16));
        }
        return result.toString();
    }

}
