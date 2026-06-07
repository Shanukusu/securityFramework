package ru.shanina.securityframework.core_api_security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-Configuration for API Security Module
 */
@AutoConfiguration
@Configuration
@ConditionalOnProperty(name = "securitas.api-security.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("ru.shanina.securityframework.core_api_security")
public class ApiSecurityAutoConfiguration {

    public ApiSecurityAutoConfiguration() {
        System.out.println("✅ API Security Module initialized");
    }
}

