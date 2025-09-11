package com.gym.crm.gateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouterValidator {
    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/api/v1/auth/",
            "/api/v1/trainees/register",
            "/api/v1/trainers/register",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/actuator/"
    );

    public boolean isSecured(ServerHttpRequest request) {
        String path = request.getURI().getPath();

        boolean isOpen = OPEN_ENDPOINTS.stream()
                .anyMatch(endpoint -> path.startsWith(endpoint) || path.contains(endpoint));

        return !isOpen;
    }
}
