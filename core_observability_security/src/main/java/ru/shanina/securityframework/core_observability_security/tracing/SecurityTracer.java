package ru.shanina.securityframework.core_observability_security.tracing;

import io.opentelemetry.api.trace.Tracer;

/**
 * Security Tracer Interface
 */
public interface SecurityTracer {

    /**
     * Get OpenTelemetry tracer
     */
    Tracer getTracer();

    /**
     * Trace authentication event
     */
    void traceAuthentication(String userId, boolean success);

    /**
     * Trace authorization decision
     */
    void traceAuthorization(String userId, String resource, boolean allowed);
}

