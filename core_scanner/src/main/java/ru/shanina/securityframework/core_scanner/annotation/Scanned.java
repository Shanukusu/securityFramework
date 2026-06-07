package ru.shanina.securityframework.core_scanner.annotation;

import java.lang.annotation.*;

/**
 * @Scanned annotation for methods that should pass vulnerability scanning
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scanned {

    /**
     * Minimum severity level to fail on
     */
    String severity() default "HIGH";

    /**
     * Allow exceptions for known CVEs
     */
    String[] allowExceptions() default {};
}

