package ru.shanina.securityframework.core_secret_rotation.annotation;

import java.lang.annotation.*;

/**
 * @RotatedSecret annotation
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RotatedSecret {

    /**
     * Rotation interval in days
     */
    int rotationIntervalDays() default 30;

    /**
     * Secret type
     */
    String type() default "password";
}

