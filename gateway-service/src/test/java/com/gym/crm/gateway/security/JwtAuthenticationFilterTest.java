package com.gym.crm.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String USERNAME = "test.user";

    @InjectMocks
    private JwtAuthenticationFilter sut;
    @Mock
    private JwtValidator jwtValidator;
    @Mock
    private RouterValidator routerValidator;
    @Mock
    private GatewayFilterChain filterChain;

    @Test
    void unsecuredRequest_shouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/public").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        when(routerValidator.isSecured(request)).thenReturn(false);

        sut.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
        verify(jwtValidator, never()).extractToken(any());
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void securedRequest_withoutToken_shouldReturnUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routerValidator.isSecured(request)).thenReturn(true);
        when(jwtValidator.extractToken(request)).thenReturn(Optional.empty());

        sut.filter(exchange, filterChain).block();

        verify(filterChain, never()).filter(any());
        verify(jwtValidator, never()).validateToken(anyString());
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void securedRequest_withValidToken_shouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Claims claims = createClaims();

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        when(routerValidator.isSecured(request)).thenReturn(true);
        when(jwtValidator.extractToken(request)).thenReturn(Optional.of(VALID_TOKEN));
        when(jwtValidator.validateToken(VALID_TOKEN)).thenReturn(Mono.just(claims));

        sut.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    private Claims createClaims() {
        return Jwts.claims()
                .subject(USERNAME)
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .add("type", "access")
                .build();
    }
}