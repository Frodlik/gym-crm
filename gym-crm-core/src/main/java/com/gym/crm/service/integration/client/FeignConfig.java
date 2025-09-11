package com.gym.crm.service.integration.client;

import com.gym.crm.util.TokenExtractor;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FeignConfig {
    private final TokenExtractor tokenExtractor;

    @Bean
    public RequestInterceptor authAndTxIdInterceptor() {
        return requestTemplate -> {
            extractToken().ifPresentOrElse(
                    token -> requestTemplate.header("Authorization", "Bearer " + token),
                    () -> log.warn("No JWT token found for Feign request")
            );

            String txId = MDC.get("transactionId");
            if (txId != null) {
                requestTemplate.header("X-Transaction-Id", txId);
            } else {
                log.warn("No txId found in MDC for Feign request");
            }
        };
    }

    private Optional<String> extractToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            return tokenExtractor.extractAccessToken(request);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String token) {
            return Optional.of(token);
        }

        log.warn("Could not extract JWT token from request or SecurityContext");
        return Optional.empty();
    }
}
