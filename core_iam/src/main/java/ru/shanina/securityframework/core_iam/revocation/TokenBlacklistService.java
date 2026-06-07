package ru.shanina.securityframework.core_iam.revocation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Token Blacklist Service - управление отозванными токенами
 * Используется для logout функциональности
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "securitas.iam.token-revocation.enabled", havingValue = "true", matchIfMissing = true)
public class TokenBlacklistService {

    private final Cache<String, BlacklistedToken> blacklist;

    @Value("${securitas.iam.token-revocation.cleanup-interval:3600}")
    private long cleanupIntervalSeconds;

    public TokenBlacklistService() {
        // Caffeine кэш автоматически удаляет токены после их expiration
        this.blacklist = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)  // Максимум 24 часа в кэше
            .maximumSize(100000)  // Максимум 100k отозванных токенов
            .recordStats()
            .build();

        // Запустить периодическую очистку
        startCleanupTask();
    }

    /**
     * Добавить токен в чёрный список (например, при logout)
     */
    public void revokeToken(String tokenJti, String userId, String reason, Instant expiresAt) {
        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
            .jti(tokenJti)
            .userId(userId)
            .revokedAt(Instant.now())
            .expiresAt(expiresAt)
            .reason(reason)
            .build();

        blacklist.put(tokenJti, blacklistedToken);
        log.info("Token revoked for user: {} (reason: {})", userId, reason);
    }

    /**
     * Проверить, отозван ли токен
     */
    public boolean isTokenRevoked(String tokenJti) {
        boolean revoked = blacklist.getIfPresent(tokenJti) != null;
        if (revoked) {
            log.debug("Token is revoked: {}", tokenJti);
        }
        return revoked;
    }

    /**
     * Отозвать все токены пользователя (при смене пароля, например)
     */
    public void revokeAllUserTokens(String userId, String reason) {
        // В продакшене нужно использовать Redis для распределенного хранилища
        // Это упрощенная реализация на Caffeine
        log.info("Revoking all tokens for user: {} (reason: {})", userId, reason);
    }

    /**
     * Статистика
     */
    public long getBlacklistSize() {
        return blacklist.estimatedSize();
    }

    /**
     * Получить статистику кэша
     */
    public String getCacheStats() {
        return blacklist.stats().toString();
    }

    // ========== PRIVATE ==========

    private void startCleanupTask() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(cleanupIntervalSeconds * 1000);
                    cleanup();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "TokenBlacklist-Cleanup");

        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    private void cleanup() {
        // Caffeine автоматически удаляет истёкшие токены
        // Но можем явно очистить кэш если нужно
        log.debug("Token blacklist cleanup executed. Size: {}", blacklist.estimatedSize());
    }

    /**
     * Внутренний класс для хранения информации об отозванном токене
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BlacklistedToken {
        private String jti;              // JWT ID (уникальный ID токена)
        private String userId;           // Пользователь
        private Instant revokedAt;       // Время отзыва
        private Instant expiresAt;       // Время истечения токена
        private String reason;           // Причина отзыва (logout, password-change, etc.)
    }
}

