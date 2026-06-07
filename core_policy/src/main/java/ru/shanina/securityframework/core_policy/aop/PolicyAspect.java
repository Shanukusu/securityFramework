package ru.shanina.securityframework.core_policy.aop;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.shanina.securityframework.core_policy.annotation.Policy;
import ru.shanina.securityframework.core_policy.engine.PolicyEngine;
import ru.shanina.securityframework.core_policy.model.PolicyContext;
import ru.shanina.securityframework.core_policy.model.PolicyDecision;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Policy Enforcement Aspect
 * Intercepts methods annotated with @Policy and evaluates policies
 */
@Slf4j
@Aspect
@Component
public class PolicyAspect {

    private final PolicyEngine policyEngine;
    private final Cache<String, PolicyDecision> decisionCache;

    public PolicyAspect(PolicyEngine policyEngine) {
        this.policyEngine = policyEngine;
        this.decisionCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .recordStats()
            .build();
    }

    @Around("@annotation(policy)")
    public Object enforcePolicy(ProceedingJoinPoint joinPoint, Policy policy) throws Throwable {
        String policyId = policy.value();

        try {
            // Build policy context from current security context
            PolicyContext context = buildPolicyContext();

            // Check cache first
            String cacheKey = buildCacheKey(policyId, context);
            PolicyDecision cachedDecision = null;

            if (policy.cacheable()) {
                cachedDecision = decisionCache.getIfPresent(cacheKey);
                if (cachedDecision != null) {
                    log.debug("Policy decision cache hit for: {}", policyId);
                    return enforceDecision(joinPoint, cachedDecision, policy);
                }
            }

            // Evaluate policy
            PolicyDecision decision = policyEngine.evaluate(policyId, context);

            // Cache decision if enabled
            if (policy.cacheable()) {
                decisionCache.put(cacheKey, decision);
            }

            // Log decision
            log.info("Policy evaluation: {} - Decision: {} ({}ms)",
                policyId, decision.isAllowed() ? "ALLOW" : "DENY", decision.getEvaluationTimeMs());

            return enforceDecision(joinPoint, decision, policy);

        } catch (Exception e) {
            log.error("Policy enforcement error for: {}", policyId, e);
            if (policy.denyOnFailure()) {
                throw new PolicyEnforcementException("Policy enforcement failed: " + policyId, e);
            }
            return joinPoint.proceed();
        }
    }

    private Object enforceDecision(ProceedingJoinPoint joinPoint, PolicyDecision decision, Policy policy) throws Throwable {
        if (!decision.isAllowed()) {
            log.warn("Policy denied access: {} - Reason: {}", decision.getPolicyId(), decision.getReason());
            if (policy.denyOnFailure()) {
                throw new PolicyAccessDeniedException("Access denied by policy: " + decision.getReason());
            }
        }

        return joinPoint.proceed();
    }

    private PolicyContext buildPolicyContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        PolicyContext context = PolicyContext.builder()
            .userId(auth != null ? auth.getName() : "anonymous")
            .build();

        if (auth != null) {
            context.addUserAttribute("principal", auth.getPrincipal());
            context.addUserAttribute("authenticated", auth.isAuthenticated());
            context.addUserAttribute("authorities", auth.getAuthorities());
        }

        context.addEnvironmentAttribute("timestamp", System.currentTimeMillis());

        return context;
    }

    private String buildCacheKey(String policyId, PolicyContext context) {
        return policyId + ":" + context.getUserId() + ":" + context.getAction();
    }

    /**
     * Custom exception for policy enforcement failures
     */
    public static class PolicyEnforcementException extends RuntimeException {
        public PolicyEnforcementException(String message) {
            super(message);
        }

        public PolicyEnforcementException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when policy denies access
     */
    public static class PolicyAccessDeniedException extends RuntimeException {
        public PolicyAccessDeniedException(String message) {
            super(message);
        }
    }
}

