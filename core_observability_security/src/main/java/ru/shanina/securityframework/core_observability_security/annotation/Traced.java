package ru.shanina.securityframework.core_observability_security.annotation;

import java.lang.annotation.*;

/**
 * @Traced annotation for security operations
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Traced {
    
    /**
     * Span name
     */
    String value();
    
    /**
     * Include arguments in span
     */
    boolean captureArgs() default true;
}

