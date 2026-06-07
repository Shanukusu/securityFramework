package ru.shanina.securityframework.core_compliance.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-Configuration for Compliance Module
 */
@AutoConfiguration
@Configuration
@ConditionalOnProperty(name = "securitas.compliance.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("ru.shanina.securityframework.core_compliance")
public class ComplianceAutoConfiguration {

    public ComplianceAutoConfiguration() {
        System.out.println("✅ Compliance Module initialized");
    }
}

