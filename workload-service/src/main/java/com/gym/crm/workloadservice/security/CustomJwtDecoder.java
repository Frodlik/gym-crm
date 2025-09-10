package com.gym.crm.workloadservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);

            return Keys.hmacShaKeyFor(paddedKey);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                throw new JwtException("Invalid token type: " + tokenType);
            }

            Instant issuedAt = claims.getIssuedAt() != null ? claims.getIssuedAt().toInstant() : Instant.now();
            Instant expiresAt = claims.getExpiration().toInstant();

            Map<String, Object> headers = Map.of(
                    "typ", "JWT",
                    "alg", "HS256"
            );

            return new Jwt(token, issuedAt, expiresAt, headers, claims);
        } catch (Exception e) {
            throw new JwtException("Failed to decode JWT token", e);
        }
    }
}