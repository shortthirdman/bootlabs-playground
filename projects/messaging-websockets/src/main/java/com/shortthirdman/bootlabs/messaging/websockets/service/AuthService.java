package com.shortthirdman.bootlabs.messaging.websockets.service;

import com.shortthirdman.bootlabs.messaging.websockets.dto.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String login(String username, String password) {
        // redisTemplate.opsForValue().set(userId, newRefreshToken, Duration.ofDays(7));
        return "token";
    }

    public AuthResponse authenticate(String username, String password) {
        String token = login(username, password);
        return new AuthResponse(token, token);
    }
}
