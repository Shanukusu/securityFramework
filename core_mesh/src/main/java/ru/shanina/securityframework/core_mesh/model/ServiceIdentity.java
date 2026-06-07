package ru.shanina.securityframework.core_mesh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Service Identity for mTLS
 * Based on SPIFFE standard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceIdentity {
    private String namespace;
    private String serviceAccount;
    private String trustDomain;
    private String certificateSubject;
    private long certificateExpiry;
    
    /**
     * Get SPIFFE ID format: spiffe://trust-domain/ns/namespace/sa/service-account
     */
    public String getSpiffeId() {
        return String.format("spiffe://%s/ns/%s/sa/%s",
            trustDomain, namespace, serviceAccount);
    }
}

