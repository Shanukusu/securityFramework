package ru.shanina.securityframework.core_audit.integrity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Audit Event с Integrity Protection
 * Blockchain-like структура для защиты от изменения логов
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventWithIntegrity {
    private long sequenceNumber;        // Порядковый номер события
    private String eventHash;           // SHA-256 текущего события
    private String previousEventHash;   // SHA-256 предыдущего события (chain)
    private String eventData;           // JSON event data
    private long timestamp;

    /**
     * Получить hashchain для проверки целостности
     */
    public String getHashChain() {
        return String.format("%d:%s:%s",
            sequenceNumber, eventHash, previousEventHash);
    }

    /**
     * Проверить целостность цепи (простая проверка)
     */
    public boolean verifyIntegrity(String expectedHash) {
        return eventHash.equals(expectedHash);
    }
}

