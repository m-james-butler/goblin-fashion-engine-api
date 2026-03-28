package com.jayice.goblinfashionengineapi.api.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class ApiRequestLoggingFilter extends OncePerRequestFilter {
    public static final String REQUEST_ID_ATTRIBUTE = ApiRequestLoggingFilter.class.getName() + ".REQUEST_ID";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRequestLoggingFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        return !requestPath.startsWith("/api");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        String sanitizedPath = LoggingSanitizer.sanitizePath(request.getRequestURI());
        String method = request.getMethod();
        long startedAt = System.currentTimeMillis();

        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        MDC.put("requestId", requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startedAt;
            int status = response.getStatus();
            if (status >= 500) {
                LOGGER.error("API request completed with server error: method={}, path={}, status={}, durationMs={}",
                        method,
                        sanitizedPath,
                        status,
                        durationMs);
            } else if (status >= 400) {
                LOGGER.warn("API request completed with client error: method={}, path={}, status={}, durationMs={}",
                        method,
                        sanitizedPath,
                        status,
                        durationMs);
            } else {
                LOGGER.info("API request completed: method={}, path={}, status={}, durationMs={}",
                        method,
                        sanitizedPath,
                        status,
                        durationMs);
            }
            MDC.remove("requestId");
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String providedRequestId = request.getHeader(REQUEST_ID_HEADER);
        if (providedRequestId != null && !providedRequestId.isBlank()) {
            return providedRequestId;
        }
        return UUID.randomUUID().toString();
    }
}
