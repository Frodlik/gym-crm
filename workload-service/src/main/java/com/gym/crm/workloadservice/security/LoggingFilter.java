package com.gym.crm.workloadservice.security;

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
import java.util.UUID;

@Component
@WebFilter(urlPatterns = "/api/*", filterName = "LoggingFilter")
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String TX_ID_HEADER = "X-Transaction-Id";
    private static final String TX_ID_KEY = "transactionId";
    private static final int MAX_PAYLOAD_LENGTH = 1000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper reqWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        String txId = reqWrapper.getHeader(TX_ID_HEADER);
        if (txId == null || txId.isBlank()) {
            txId = UUID.randomUUID().toString();
        }
        MDC.put(TX_ID_KEY, txId);

        long start = System.currentTimeMillis();
        try {
            logger.info("!INCOMING REQUEST! {} {}", reqWrapper.getMethod(), reqWrapper.getRequestURI());

            chain.doFilter(reqWrapper, resWrapper);

            long duration = System.currentTimeMillis() - start;
            logger.info("!REQUEST COMPLETED! Status={} Duration={}ms", resWrapper.getStatus(), duration);

            logBodiesIfNecessary(reqWrapper, resWrapper);
        } finally {
            resWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void logBodiesIfNecessary(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        byte[] reqBody = request.getContentAsByteArray();
        if (reqBody.length > 0) {
            String body = new String(reqBody, StandardCharsets.UTF_8);
            logger.info("REQUEST BODY: {}", body);
        }

        byte[] resBody = response.getContentAsByteArray();
        if (resBody.length > 0) {
            String body = new String(resBody, StandardCharsets.UTF_8);
            logger.info("RESPONSE BODY: {}", truncate(body));
        }
    }

    private String truncate(String content) {
        return (content.length() > MAX_PAYLOAD_LENGTH)
                ? content.substring(0, MAX_PAYLOAD_LENGTH) + "... [TRUNCATED]"
                : content;
    }
}
