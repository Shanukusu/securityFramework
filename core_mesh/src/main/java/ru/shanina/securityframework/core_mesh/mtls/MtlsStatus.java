package ru.shanina.securityframework.core_mesh.mtls;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * mTLS Status Information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MtlsStatus {
    private String namespace;
    private boolean mtlsEnabled;
    private String enforcementMode; // STRICT, PERMISSIVE, DISABLED
    private String lastUpdate;
    private int certificatesRotated;
    private String status; // HEALTHY, WARNING, CRITICAL
}

