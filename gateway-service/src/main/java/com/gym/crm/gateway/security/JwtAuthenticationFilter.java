package com.gym.crm.gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private static final String TRAINERS_PATH = "/api/v1/trainers";
    private static final String WORKLOAD_PATH = "/workload";

    private final JwtValidator jwtValidator;
    private final RouterValidator routerValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Incoming request: {} {}", request.getMethod(), request.getURI());

        if (!routerValidator.isSecured(request)) {
            return chain.filter(exchange);
        }

        return jwtValidator.extractToken(request)
                .map(token -> processAuthenticatedRequest(exchange, chain, token))
                .orElseGet(() -> unauthorized(exchange));
    }

    private Mono<Void> processAuthenticatedRequest(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        return jwtValidator.validateToken(token)
                .doOnSuccess(claims -> log.info("Token validated for path: {}", exchange.getRequest().getPath()))
                .map(claims -> createExchangeWithAuthHeader(exchange, token))
                .flatMap(chain::filter)
                .onErrorResume(this::handleValidationError);
    }

    private ServerWebExchange createExchangeWithAuthHeader(ServerWebExchange exchange, String token) {
        if (shouldAddBearerToken(exchange.getRequest().getPath().toString())) {
            return exchange.mutate()
                    .request(r -> r.headers(headers -> headers.setBearerAuth(token)))
                    .build();
        }

        return exchange;
    }

    private boolean shouldAddBearerToken(String path) {
        return path.startsWith(TRAINERS_PATH) || path.contains(WORKLOAD_PATH);
    }

    private Mono<Void> handleValidationError(Throwable error) {
        log.error("JWT validation failed: {}", error.getMessage());

        return Mono.empty();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

