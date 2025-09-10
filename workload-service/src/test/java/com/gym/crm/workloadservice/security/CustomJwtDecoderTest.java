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
    private final String secret = "supersecretkeysupersecretkey123456";

    private CustomJwtDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new CustomJwtDecoder();
        ReflectionTestUtils.setField(decoder, "secret", secret);
    }

    @Test
    void decode_ShouldReturnJwt_WhenAccessTokenValid() {
        String token = generateToken("access", secret);

        Jwt actual = decoder.decode(token);

        assertNotNull(actual);
        assertEquals("testuser", actual.getSubject());
        assertEquals("access", actual.getClaim("type"));
    }

    @Test
    void decode_ShouldThrowException_WhenTokenTypeNotAccess() {
        String token = generateToken("refresh", secret);

        JwtException actual = assertThrows(JwtException.class, () -> decoder.decode(token));

        assertTrue(actual.getMessage().contains("Failed to decode JWT token"));
    }

    @Test
    void decode_ShouldThrowException_WhenSignatureInvalid() {
        String anotherSecret = "anothersecretkeyanothersecretkey123";
        String token = generateToken("access", anotherSecret);

        JwtException actual = assertThrows(JwtException.class, () -> decoder.decode(token));

        assertTrue(actual.getMessage().contains("Failed to decode JWT token"));
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
}