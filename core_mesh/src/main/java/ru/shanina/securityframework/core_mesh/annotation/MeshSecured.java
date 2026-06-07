package ru.shanina.securityframework.core_mesh.annotation;

import java.lang.annotation.*;

/**
 * @MeshSecured - enables mTLS for service-to-service communication
 * Works with Istio or Linkerd service meshes
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeshSecured {

    /**
     * Enforce mutual TLS
     */
    boolean mtlsRequired() default true;

    /**
     * Sidecar injection policy
     */
    String sidecarInjection() default "enabled";
}

