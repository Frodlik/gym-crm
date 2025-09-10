package com.gym.crm.workloadservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CustomJwtDecoderTest {
    private CustomJwtDecoder decoder;
    private final String secret = "supersecretkeysupersecretkey123456";

    @BeforeEach
    void setUp() {
        decoder = new CustomJwtDecoder();
        ReflectionTestUtils.setField(decoder, "secret", secret);
    }

    private String generateToken(String type, String signingSecret) {
        SecretKey key = Keys.hmacShaKeyFor(signingSecret.getBytes(StandardCharsets.UTF_8));

        Instant now = Instant.now();
        return Jwts.builder()
                .subject("testuser")
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key)
                .compact();
    }

    @Test
    void decode_ShouldReturnJwt_WhenAccessTokenValid() {
        String token = generateToken("access", secret);

        Jwt jwt = decoder.decode(token);

        assertNotNull(jwt);
        assertEquals("testuser", jwt.getSubject());
        assertEquals("access", jwt.getClaim("type"));
    }

    @Test
    void decode_ShouldThrowException_WhenTokenTypeNotAccess() {
        String token = generateToken("refresh", secret);

        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode(token));
        assertTrue(ex.getMessage().contains("Failed to decode JWT token"));
    }

    @Test
    void decode_ShouldThrowException_WhenSignatureInvalid() {
        String anotherSecret = "anothersecretkeyanothersecretkey123";
        String token = generateToken("access", anotherSecret);

        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode(token));
        assertTrue(ex.getMessage().contains("Failed to decode JWT token"));
    }
}