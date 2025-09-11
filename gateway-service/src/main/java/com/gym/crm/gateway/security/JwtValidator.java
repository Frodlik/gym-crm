package com.gym.crm.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtValidator {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.cookie.name:access-token}")
    private String jwtCookieName;

    private SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = createSecretKey(jwtSecret);
        }

        return secretKey;
    }

    private SecretKey createSecretKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            keyBytes = paddedKey;
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Mono<Claims> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(getSecretKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                if (claims.getExpiration().before(new Date())) {
                    throw new JwtException("Token is expired");
                }

                return claims;
            } catch (Exception e) {
                throw new JwtException("Invalid JWT token", e);
            }
        });
    }

    public Optional<String> extractToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getCookies().getFirst(jwtCookieName))
                .map(HttpCookie::getValue)
                .filter(value -> !value.isBlank());
    }
}
