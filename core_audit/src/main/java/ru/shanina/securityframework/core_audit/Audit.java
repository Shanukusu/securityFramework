package ru.shanina.securityframework.core_audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {
    String event() default "";
    String level() default "INFO";
}