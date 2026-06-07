package ru.shanina.securityframework.core_api_security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API Key Model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {
    private String keyId;
    private String secretKey;
    private String name;
    private String scope;
    private boolean active;
    private long createdAt;
    private long expiresAt;
    private long lastUsedAt;
}

