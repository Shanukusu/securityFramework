package ru.shanina.securityframework.core_mesh.mtls;

import io.fabric8.kubernetes.api.model.HasMetadata;
import org.springframework.stereotype.Component;

/**
 * Interface for mTLS Management
 */
public interface MtlsConfig {

    /**
     * Apply mTLS policies to namespace
     */
    void applyMtlsPolicy(String namespace, String enforcementMode);

    /**
     * Rotate certificates for service
     */
    void rotateCertificates(String namespace, String serviceAccount);

    /**
     * Check mTLS status
     */
    MtlsStatus getMtlsStatus(String namespace);
}

