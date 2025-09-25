package com.shortthirdman.bootlabs.jwtauth.refreshtoken.controller;

import com.shortthirdman.bootlabs.jwtauth.refreshtoken.common.TokenRefreshException;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.configuration.jwt.JwtUtils;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.request.LogOutRequest;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.request.LoginRequest;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.request.SignupRequest;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.request.TokenRefreshRequest;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.response.JwtResponse;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.response.MessageResponse;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.response.TokenRefreshResponse;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.RefreshToken;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.service.AuthService;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        final var result = authService.userLogin(loginRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        var result = authService.createNewUser(signUpRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        refreshTokenService.removeUser(logOutRequest.getUserId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
