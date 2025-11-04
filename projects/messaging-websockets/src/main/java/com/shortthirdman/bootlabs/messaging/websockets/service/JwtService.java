package com.shortthirdman.bootlabs.messaging.websockets.service;

import com.shortthirdman.bootlabs.messaging.websockets.dto.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final String ISSUER = "bootlabs";

    public String generateAccessToken(Object user) {
        return null;
    }

    public String generateRefreshToken(Object user) {
        return null;
    }

    public boolean validateAccessToken(String token) {
        return false;
    }

    public boolean validateRefreshToken(String token) {
        return false;
    }

    public String getUsernameFromToken(String refreshToken) {
        return null;
    }
}
