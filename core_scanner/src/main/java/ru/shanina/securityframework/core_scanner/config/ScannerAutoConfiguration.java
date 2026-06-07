package ru.shanina.securityframework.core_scanner.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-Configuration for Security Scanner Module
 */
@AutoConfiguration
@Configuration
@ConditionalOnProperty(name = "securitas.scanner.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("ru.shanina.securityframework.core_scanner")
public class ScannerAutoConfiguration {
    
    public ScannerAutoConfiguration() {
        System.out.println("✅ Security Scanner Module initialized");
    }
}

