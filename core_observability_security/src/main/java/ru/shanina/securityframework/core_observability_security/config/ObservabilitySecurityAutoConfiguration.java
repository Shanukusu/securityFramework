package ru.shanina.securityframework.core_observability_security.config;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-Configuration for Observability Security Module
 */
@AutoConfiguration
@Configuration
@ConditionalOnProperty(name = "securitas.observability.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("ru.shanina.securityframework.core_observability_security")
public class ObservabilitySecurityAutoConfiguration {

    @Bean
    public SdkTracerProvider tracerProvider() {
        return SdkTracerProvider.builder().build();
    }

    @Bean
    public Tracer securityTracer(SdkTracerProvider tracerProvider) {
        return tracerProvider.get("security-tracer");
    }

    public ObservabilitySecurityAutoConfiguration() {
        System.out.println("✅ Observability Security Module initialized");
    }
}

