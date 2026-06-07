package ru.shanina.securityframework.core_secret_rotation.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-Configuration for Secret Rotation Module
 */
@AutoConfiguration
@Configuration
@ConditionalOnProperty(name = "securitas.secret-rotation.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("ru.shanina.securityframework.core_secret_rotation")
public class SecretRotationAutoConfiguration {

    public SecretRotationAutoConfiguration() {
        System.out.println("✅ Secret Rotation Module initialized");
    }
}

