package com.shortthirdman.bootlabs.authentication.controller;

import com.shortthirdman.bootlabs.authentication.common.JwtTokenUtil;
import com.shortthirdman.bootlabs.authentication.dto.AuthRequest;
import com.shortthirdman.bootlabs.authentication.dto.AuthResponse;
import com.shortthirdman.bootlabs.authentication.dto.OtpVerificationRequest;
import com.shortthirdman.bootlabs.authentication.dto.UserRegistrationRequest;
import com.shortthirdman.bootlabs.authentication.entity.User;
import com.shortthirdman.bootlabs.authentication.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request);

        // Initiate email verification
        userService.initiateEmailVerification(user.getEmail());
        var response = Map.of("message", "User has been registered successfully. Verification OTP sent to email.");

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        User user = userService.getUserByEmail(request.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail(), user.isVerified()));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody OtpVerificationRequest request) {
        boolean isValid = userService.verifyEmailOtp(request.getUserId(), request.getCode());

        if (isValid) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }

    @PostMapping("/resend-email-otp")
    public ResponseEntity<?> resendEmailOtp(@RequestParam String email) {
        userService.initiateEmailVerification(email);
        return ResponseEntity.ok("OTP resent successfully");
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestBody OtpVerificationRequest request) {
        boolean isValid = userService.verifyPhoneOtp(request.getUserId(), request.getCode());

        if (isValid) {
            return ResponseEntity.ok("Phone verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }

    @PostMapping("/initiate-phone-verification")
    public ResponseEntity<?> initiatePhoneVerification(@RequestParam Long userId) {
        userService.initiatePhoneVerification(userId);
        return ResponseEntity.ok("OTP sent to phone number");
    }
}
