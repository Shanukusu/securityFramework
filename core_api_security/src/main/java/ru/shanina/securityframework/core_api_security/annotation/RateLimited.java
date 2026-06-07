package ru.shanina.securityframework.core_api_security.annotation;

import java.lang.annotation.*;

/**
 * @RateLimited annotation
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {

    /**
     * Requests per second
     */
    int requestsPerSecond() default 100;

    /**
     * Burst capacity
     */
    int burstSize() default 200;
}

