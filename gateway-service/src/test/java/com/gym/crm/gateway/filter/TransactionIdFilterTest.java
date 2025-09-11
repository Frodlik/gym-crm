package com.gym.crm.gateway.filter;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionIdFilterTest {
    private final TransactionIdFilter filter = new TransactionIdFilter();

    @Test
    void shouldUseProvidedTxId() {
        String providedId = UUID.randomUUID().toString();
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/test")
                .header("X-Transaction-Id", providedId)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> actual = filter.filter(exchange, ex -> {
            assertThat(exchange.getRequest().getHeaders().getFirst("X-Transaction-Id"))
                    .isEqualTo(providedId);
            return Mono.empty();
        });

        StepVerifier.create(actual).verifyComplete();
        assertThat(MDC.get("transactionId")).isNull();
    }

    @Test
    void shouldGenerateTxIdIfMissing() {
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/test")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        final String[] txIdFromFilter = new String[1];

        Mono<Void> actual = filter.filter(exchange, ex -> {
            txIdFromFilter[0] = ex.getRequest().getHeaders().getFirst("X-Transaction-Id");
            assertThat(txIdFromFilter[0]).isNotBlank();
            return Mono.empty();
        });

        StepVerifier.create(actual).verifyComplete();
        assertThat(MDC.get("transactionId")).isNull();
        assertThat(UUID.fromString(txIdFromFilter[0])).isInstanceOf(UUID.class);
    }
}