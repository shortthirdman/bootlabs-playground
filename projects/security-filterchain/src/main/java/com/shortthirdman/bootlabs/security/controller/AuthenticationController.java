package com.shortthirdman.bootlabs.security.controller;

import com.shortthirdman.bootlabs.security.model.Login;
import com.shortthirdman.bootlabs.security.service.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody Login login) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.username(), login.password()));

        var token = jwtService.generateToken(login.username());

        return ResponseEntity.ok(Map.of("info", "Basic Auth", "token", token));

    }
}
