package com.shortthirdman.bootlabs.messaging.websockets.controller;

import com.shortthirdman.bootlabs.messaging.websockets.dto.AuthResponse;
import com.shortthirdman.bootlabs.messaging.websockets.dto.LoginRequest;
import com.shortthirdman.bootlabs.messaging.websockets.repository.UserRepository;
import com.shortthirdman.bootlabs.messaging.websockets.service.AuthService;
import com.shortthirdman.bootlabs.messaging.websockets.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        var user = authService.authenticate(request.username(), request.password());
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    @PostMapping("/auth/refresh")
    public AuthResponse refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (jwtService.validateRefreshToken(refreshToken)) {
            String username = jwtService.getUsernameFromToken(refreshToken);
            var user = userRepository.findByUsername(username);
            return new AuthResponse(
                    jwtService.generateAccessToken(user),
                    jwtService.generateRefreshToken(user)
            );
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    }
}
