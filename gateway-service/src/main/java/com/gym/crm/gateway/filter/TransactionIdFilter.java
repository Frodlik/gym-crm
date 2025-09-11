package com.gym.crm.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TransactionIdFilter implements GlobalFilter, Ordered {
    private static final String TX_ID_HEADER = "X-Transaction-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String txId = request.getHeaders().getFirst(TX_ID_HEADER);

        if (txId == null || txId.isBlank()) {
            txId = UUID.randomUUID().toString();
        }

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TX_ID_HEADER, txId)
                .build();
        MDC.put("transactionId", txId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signal -> MDC.clear());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
