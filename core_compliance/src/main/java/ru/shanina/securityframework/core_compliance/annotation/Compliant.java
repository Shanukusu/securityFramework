package ru.shanina.securityframework.core_compliance.annotation;

import ru.shanina.securityframework.core_compliance.model.ComplianceFramework;
import java.lang.annotation.*;

/**
 * @Compliant annotation for compliance enforcement
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Compliant {

    /**
     * Frameworks to validate against
     */
    ComplianceFramework[] frameworks();
}

