package com.gym.crm.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Component
@WebFilter(urlPatterns = "/api/*", filterName = "LoggingFilter")
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String TX_ID_KEY = "transactionId";
    private static final String TX_ID_HEADER = "X-Transaction-Id";
    private static final String PASSWORD_REPLACEMENT = "$1****$3";
    private static final int MAX_PAYLOAD_LENGTH = 1000;
    private static final Set<String> SENSITIVE_ENDPOINTS = Set.of(
            "/api/v1/trainees/register",
            "/api/v1/trainers/register"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        String txId = requestWrapper.getHeader(TX_ID_HEADER);
        if (txId == null || txId.isBlank()) {
            txId = UUID.randomUUID().toString();
        }
        MDC.put(TX_ID_KEY, txId);

        long startTime = System.currentTimeMillis();

        try {
            logRequestMeta(requestWrapper);
            chain.doFilter(requestWrapper, responseWrapper);
            logRequestBody(requestWrapper);
            logResponse(requestWrapper, responseWrapper, startTime);
        } finally {
            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void logRequestMeta(ContentCachingRequestWrapper request) {
        logger.info("!INCOMING REQUEST! {} {}", request.getMethod(), request.getRequestURI());
    }

    private void logRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0 && shouldLogRequestBody(request.getMethod())) {
            String body = new String(buf, StandardCharsets.UTF_8);
            body = maskSensitiveData(body);
            body = truncateIfNeeded(body);
            body = toSingleLine(body);
            logger.info("REQUEST BODY: {}", body);
        }
    }

    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        logger.info("!RESPONSE COMPLETED! Status: {} | Duration: {}ms", response.getStatus(), duration);

        if (!isSensitiveEndpoint(request.getRequestURI())) {
            byte[] buf = response.getContentAsByteArray();
            if (buf.length > 0) {
                String body = new String(buf, StandardCharsets.UTF_8);
                body = truncateIfNeeded(body);
                logger.info("RESPONSE BODY: {}", body);
            }
        } else {
            logger.info("RESPONSE BODY: [HIDDEN - SENSITIVE ENDPOINT]");
        }
    }

    private boolean shouldLogRequestBody(String method) {
        return Set.of("POST", "PUT", "PATCH").contains(method.toUpperCase());
    }

    private boolean isSensitiveEndpoint(String uri) {
        return SENSITIVE_ENDPOINTS.stream().anyMatch(uri::contains);
    }

    private String maskSensitiveData(String body) {
        return body.replaceAll("(\"password\"\\s*:\\s*\")([^\"]*)(\")", PASSWORD_REPLACEMENT)
                .replaceAll("(\"newPassword\"\\s*:\\s*\")([^\"]*)(\")", PASSWORD_REPLACEMENT)
                .replaceAll("(\"oldPassword\"\\s*:\\s*\")([^\"]*)(\")", PASSWORD_REPLACEMENT);
    }

    private String truncateIfNeeded(String content) {
        return content.length() > MAX_PAYLOAD_LENGTH
                ? content.substring(0, MAX_PAYLOAD_LENGTH) + "... [TRUNCATED]"
                : content;
    }

    private String toSingleLine(String body) {
        return body.replaceAll("[\\n\\r\\t]+", "");
    }
}
