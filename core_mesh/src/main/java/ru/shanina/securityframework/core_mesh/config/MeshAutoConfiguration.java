package ru.shanina.securityframework.core_mesh.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-Configuration for Service Mesh Module
 */
@AutoConfiguration
@Configuration
@ConditionalOnProperty(name = "securitas.mesh.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("ru.shanina.securityframework.core_mesh")
public class MeshAutoConfiguration {
    
    public MeshAutoConfiguration() {
        System.out.println("✅ Service Mesh Security Module initialized");
    }
}

