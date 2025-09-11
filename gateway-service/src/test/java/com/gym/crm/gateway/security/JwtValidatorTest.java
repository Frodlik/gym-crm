package com.gym.crm.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class JwtValidatorTest {
    private static final String JWT_SECRET = "myVeryLongSecretKeyForJWTTokenGenerationThatIsAtLeast32CharactersLong";
    private static final String JWT_COOKIE_NAME = "access-token";
    private static final String USERNAME = "test.user";

    @InjectMocks
    private JwtValidator validator;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(validator, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(validator, "jwtCookieName", JWT_COOKIE_NAME);

        byte[] keyBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void testValidateToken_whenValidToken_shouldReturnClaims() {
        String token = generateValidToken();

        Mono<Claims> actual = validator.validateToken(token);

        StepVerifier.create(actual)
                .assertNext(claims -> {
                    assertEquals(USERNAME, claims.getSubject());
                    assertEquals("access", claims.get("type"));
                })
                .verifyComplete();
    }

    @Test
    void testValidateToken_whenExpiredToken_shouldReturnError() {
        String expiredToken = generateExpiredToken();

        Mono<Claims> actual = validator.validateToken(expiredToken);

        StepVerifier.create(actual)
                .expectError(JwtException.class)
                .verify();
    }

    @Test
    void testValidateToken_whenInvalidToken_shouldReturnError() {
        String invalidToken = "invalid.jwt.token";

        Mono<Claims> actual = validator.validateToken(invalidToken);

        StepVerifier.create(actual)
                .expectError(JwtException.class)
                .verify();
    }

    @Test
    void testValidateToken_whenMalformedToken_shouldReturnError() {
        String malformedToken = "not-a-jwt";

        Mono<Claims> actual = validator.validateToken(malformedToken);

        StepVerifier.create(actual)
                .expectError(JwtException.class)
                .verify();
    }

    @Test
    void testExtractToken_whenCookieDoesNotExist_shouldReturnEmpty() {
        ServerHttpRequest request = MockServerHttpRequest.get("/test").build();

        Optional<String> actual = validator.extractToken(request);

        assertFalse(actual.isPresent());
    }

    @Test
    void testCreateSecretKey_whenSecretLessThan32Bytes_shouldPadKey() {
        String shortSecret = "shortkey";
        ReflectionTestUtils.setField(validator, "jwtSecret", shortSecret);
        ReflectionTestUtils.setField(validator, "secretKey", null);

        String token = generateValidTokenWithSecret(shortSecret);

        Mono<Claims> actual = validator.validateToken(token);

        StepVerifier.create(actual)
                .assertNext(claims -> assertEquals(USERNAME, claims.getSubject()))
                .verifyComplete();
    }

    @Test
    void testValidateToken_whenTokenWithDifferentSecret_shouldReturnError() {
        String differentSecret = "differentVeryLongSecretKeyForJWTTokenGenerationThatIsAtLeast32Chars";
        String tokenWithDifferentSecret = generateValidTokenWithSecret(differentSecret);

        Mono<Claims> actual = validator.validateToken(tokenWithDifferentSecret);

        StepVerifier.create(actual)
                .expectError(JwtException.class)
                .verify();
    }

    private String generateValidToken() {
        return generateTokenWithExpiration(System.currentTimeMillis() + 3600000);
    }

    private String generateExpiredToken() {
        return generateTokenWithExpiration(System.currentTimeMillis() - 1000);
    }

    private String generateTokenWithExpiration(long expirationTime) {
        return Jwts.builder()
                .subject(USERNAME)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(expirationTime))
                .signWith(secretKey)
                .compact();
    }

    private String generateValidTokenWithSecret(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            keyBytes = paddedKey;
        }
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(USERNAME)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}