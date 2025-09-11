package com.gym.crm.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouterValidatorTest {
    private RouterValidator sut;

    @BeforeEach
    void setUp() {
        sut = new RouterValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/v1/auth/refresh",
            "/api/v1/trainees/register",
            "/api/v1/trainers/register",
            "/swagger-ui/index.html",
            "/v3/api-docs/swagger-config",
            "/actuator/health"
    })
    void testIsSecured_whenOpenEndpoint_shouldReturnFalse(String path) {
        ServerHttpRequest request = MockServerHttpRequest.get(path).build();

        boolean isSecured = sut.isSecured(request);

        assertFalse(isSecured);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/trainers/1",
            "/api/v1/trainees/profile",
            "/api/v1/workload/monthly",
            "/api/v1/trainings",
            "/api/v1/users/change-password"
    })
    void testIsSecured_whenSecuredEndpoint_shouldReturnTrue(String path) {
        ServerHttpRequest request = MockServerHttpRequest.get(path).build();

        boolean isSecured = sut.isSecured(request);

        assertTrue(isSecured);
    }

    @Test
    void testIsSecured_whenRootPath_shouldReturnTrue() {
        ServerHttpRequest request = MockServerHttpRequest.get("/").build();

        boolean isSecured = sut.isSecured(request);

        assertTrue(isSecured);
    }

    @Test
    void testIsSecured_whenPathContainsOpenEndpointInMiddle_shouldReturnFalse() {
        ServerHttpRequest request = MockServerHttpRequest.get("/some/path/swagger-ui/test").build();

        boolean isSecured = sut.isSecured(request);

        assertFalse(isSecured);
    }
}