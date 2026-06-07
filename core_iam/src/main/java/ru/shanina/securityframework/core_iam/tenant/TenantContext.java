package ru.shanina.securityframework.core_iam.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Tenant Context для multi-tenant приложений
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantContext {
    private String tenantId;            // ID организации/тенанта
    private String organizationId;      // Альтернативное наименование
    private String organizationName;    // Название организации
    private String environment;         // prod, staging, dev
    private Map<String, String> attributes = new HashMap<>();  // Дополнительные атрибуты
    
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }
    
    public String getAttribute(String key) {
        return attributes.get(key);
    }
}

