package ru.shanina.securityframework.core_iam;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final int maxRequests = 10; // per minute
    private final long windowMs = 60_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();
        AtomicLong count = requestCounts.computeIfAbsent(clientIp, k -> new AtomicLong(0));
        long currentCount = count.incrementAndGet();

        if (currentCount > maxRequests) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
