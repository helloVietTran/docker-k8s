package com.viettran.reading_story_web.config;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HttpLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && path.contains("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String requestId = request.getHeader("X-Request-Id");

        try {
            filterChain.doFilter(request, response);
        } finally {
            long latencyMs = System.currentTimeMillis() - start;
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            int status = response.getStatus();
            String clientIp = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            log.info(
                    "time={} - service=http-server - info=request - message=endpoint={} method={} status={} latency_ms={} clientIp={} requestId={} userAgent={} queryString={}",
                    LocalDateTime.now(),
                    uri,
                    method,
                    status,
                    latencyMs,
                    clientIp,
                    requestId,
                    userAgent,
                    queryString);
        }
    }
}
