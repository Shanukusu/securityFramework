package ru.shanina.securityframework.core_api_security.ratelimit;

/**
 * Rate Limiter Interface
 */
public interface RateLimiter {

    /**
     * Check if request is allowed
     */
    boolean allowRequest(String clientId);

    /**
     * Get remaining quota for client
     */
    long getRemainingQuota(String clientId);
}

