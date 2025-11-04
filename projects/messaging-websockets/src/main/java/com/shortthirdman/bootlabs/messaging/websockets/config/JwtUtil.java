package com.shortthirdman.bootlabs.messaging.websockets.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public final class JwtUtil {

    @Value( "${bootlabs.jwt.secret}")
    private String secretKeyValue;

    public Claims validateToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyValue.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> jwsClaims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        return jwsClaims.getPayload();
    }
}
