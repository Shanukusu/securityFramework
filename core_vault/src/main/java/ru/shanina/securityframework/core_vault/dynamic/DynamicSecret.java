package ru.shanina.securityframework.core_vault.dynamic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * Dynamic Secret - краткосрочный секрет (например, учетные данные БД)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicSecret {
    private String name;
    private String type;  // postgresql, mysql, rabbitmq, mongodb, etc.
    private String username;  // Для DB credentials
    private String password;  // Для DB credentials
    private String hostname;  // Для connections
    private int port;
    private String database;
    
    private Instant createdAt;
    private Instant expiresAt;
    private long ttlSeconds;  // Time-to-live
    
    private String leaseId;  // Vault lease ID
    private long leaseValiditySeconds;
    
    /**
     * Проверить, истекла ли учетные данные
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
    
    /**
     * Получить оставшееся время до истечения (в секундах)
     */
    public long getSecondsUntilExpiry() {
        if (expiresAt == null) {
            return Long.MAX_VALUE;
        }
        long seconds = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(0, seconds);
    }
}

