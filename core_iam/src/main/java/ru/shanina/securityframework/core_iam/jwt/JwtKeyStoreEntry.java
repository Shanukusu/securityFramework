package ru.shanina.securityframework.core_iam.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * JWT Key Store Entry - поддержка key rotation с kid (Key ID)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtKeyStoreEntry {
    private String kid;                    // Key ID (например: "2026-01")
    private String secretKey;              // Base64-encoded secret
    private String algorithm;              // HS256, HS512, RS256, etc.
    private Instant createdAt;             // Время создания ключа
    private Instant rotatedAt;             // Время последней ротации
    private Instant expiresAt;             // Время истечения (если есть)
    private boolean isActive;              // Активный ключ (для подписи)
    private boolean isValid;               // Валидный ключ (для проверки старых токенов)
    private int version;                   // Версия ключа
    private Map<String, Object> metadata;  // Метаданные (source, reason, etc.)
}

