package ru.shanina.securityframework.core_iam;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authorize {
    Role[] value() default Role.USER;
    boolean requireAll() default false;
}
