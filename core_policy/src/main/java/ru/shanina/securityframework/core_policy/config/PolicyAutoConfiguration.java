package ru.shanina.securityframework.core_policy.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Auto-Configuration for Policy Engine Module
 * Enables when securitas.policy.enabled=true
 */
@AutoConfiguration
@Configuration
@EnableAspectJAutoProxy
@ConditionalOnProperty(name = "securitas.policy.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("ru.shanina.securityframework.core_policy")
public class PolicyAutoConfiguration {
    
    public PolicyAutoConfiguration() {
        System.out.println("✅ Policy Engine Module initialized");
    }
}

