package ru.shanina.securityframework.core_policy.annotation;

import java.lang.annotation.*;

/**
 * @Policy annotation for method-level policy enforcement
 * Works in conjunction with @Authorize for multi-layer security
 *
 * Example:
 * @Policy("employee-access")
 * @Authorize(Role.EMPLOYEE)
 * @PostMapping("/data")
 * public void processData() { }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Policy {

    /**
     * Policy ID or name to evaluate
     * Must exist in configured PolicyEngine
     */
    String value();

    /**
     * Should deny access if policy evaluation fails
     */
    boolean denyOnFailure() default true;

    /**
     * Cache policy decision results
     */
    boolean cacheable() default true;

    /**
     * Cache TTL in seconds
     */
    int cacheTtlSeconds() default 300;
}

