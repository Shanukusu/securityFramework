package ru.shanina.securityframework.core_iam.jwt;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT Key Store - управление ключами с ротацией (kid поддержка)
 */
@Slf4j
@Component
public class JwtKeyStore {

    private final Map<String, JwtKeyStoreEntry> keyStore = new LinkedHashMap<>();
    private final Cache<String, JwtKeyStoreEntry> keyCache;
    private String currentActiveKeyId;  // ID активного ключа для подписи

    public JwtKeyStore() {
        this.keyCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(100)
            .recordStats()
            .build();
    }

    /**
     * Добавить новый ключ в хранилище
     */
    public void addKey(JwtKeyStoreEntry entry) {
        if (entry.isActive()) {
            // Дезактивировать старый активный ключ
            if (currentActiveKeyId != null) {
                JwtKeyStoreEntry oldActive = keyStore.get(currentActiveKeyId);
                if (oldActive != null) {
                    oldActive.setActive(false);
                    oldActive.setValid(true);  // Но остается валидным для проверки
                    log.info("Deactivated previous key: {}", currentActiveKeyId);
                }
            }
            currentActiveKeyId = entry.getKid();
        }

        keyStore.put(entry.getKid(), entry);
        keyCache.put(entry.getKid(), entry);
        log.info("Added key to store: {} (active: {}, version: {})",
            entry.getKid(), entry.isActive(), entry.getVersion());
    }

    /**
     * Получить активный ключ для подписи
     */
    public JwtKeyStoreEntry getActiveKey() {
        if (currentActiveKeyId == null) {
            throw new IllegalStateException("No active JWT key found");
        }
        return getKey(currentActiveKeyId);
    }

    /**
     * Получить ключ по kid
     */
    public JwtKeyStoreEntry getKey(String kid) {
        // Проверить кэш
        JwtKeyStoreEntry cached = keyCache.getIfPresent(kid);
        if (cached != null) {
            return cached;
        }

        // Получить из хранилища
        JwtKeyStoreEntry entry = keyStore.get(kid);
        if (entry != null) {
            keyCache.put(kid, entry);
            return entry;
        }

        throw new IllegalArgumentException("JWT key not found: " + kid);
    }

    /**
     * Проверить валидность ключа (для проверки подписи)
     */
    public boolean isValidKey(String kid) {
        try {
            JwtKeyStoreEntry entry = getKey(kid);

            // Проверить, не истек ли ключ
            if (entry.getExpiresAt() != null && entry.getExpiresAt().isBefore(Instant.now())) {
                log.warn("Key expired: {}", kid);
                return false;
            }

            return entry.isValid();
        } catch (IllegalArgumentException e) {
            log.warn("Key validation failed: {}", kid);
            return false;
        }
    }

    /**
     * Ротация ключей - создать новый активный ключ
     */
    public String rotateKey(String algorithm, Map<String, Object> metadata) {
        String newKid = generateKid();

        JwtKeyStoreEntry newKey = JwtKeyStoreEntry.builder()
            .kid(newKid)
            .secretKey(generateSecretKey())
            .algorithm(algorithm)
            .createdAt(Instant.now())
            .rotatedAt(Instant.now())
            .isActive(true)
            .isValid(true)
            .version(getNextVersion())
            .metadata(metadata != null ? metadata : new HashMap<>())
            .build();

        addKey(newKey);
        log.info("Key rotated successfully: {} -> {}",
            currentActiveKeyId != null ? currentActiveKeyId : "initial", newKid);

        return newKid;
    }

    /**
     * Получить все валидные ключи
     */
    public List<JwtKeyStoreEntry> getValidKeys() {
        return keyStore.values().stream()
            .filter(JwtKeyStoreEntry::isValid)
            .collect(Collectors.toList());
    }

    /**
     * Получить историю ключей (для аудита)
     */
    public List<JwtKeyStoreEntry> getKeyHistory() {
        return new ArrayList<>(keyStore.values());
    }

    /**
     * Инвалидировать ключ (отозвать)
     */
    public void invalidateKey(String kid, String reason) {
        JwtKeyStoreEntry entry = keyStore.get(kid);
        if (entry != null) {
            entry.setValid(false);
            entry.setActive(false);
            if (entry.getMetadata() == null) {
                entry.setMetadata(new HashMap<>());
            }
            entry.getMetadata().put("invalidationReason", reason);
            entry.getMetadata().put("invalidatedAt", Instant.now());

            keyCache.invalidate(kid);
            log.warn("Key invalidated: {} (reason: {})", kid, reason);
        }
    }

    /**
     * Очистить старые ключи (старше 90 дней)
     */
    public int cleanupExpiredKeys(int daysOld) {
        Instant cutoff = Instant.now().minus(java.time.Duration.ofDays(daysOld));

        List<String> toRemove = keyStore.entrySet().stream()
            .filter(e -> e.getValue().getCreatedAt().isBefore(cutoff))
            .filter(e -> !e.getValue().isActive())  // Не удаляем активные
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        toRemove.forEach(kid -> {
            keyStore.remove(kid);
            keyCache.invalidate(kid);
        });

        log.info("Cleaned up {} expired keys", toRemove.size());
        return toRemove.size();
    }

    /**
     * Размер хранилища
     */
    public int size() {
        return keyStore.size();
    }

    // ========== PRIVATE ==========

    private String generateKid() {
        // Формат: YYYY-MM
        return new java.text.SimpleDateFormat("yyyy-MM").format(new Date());
    }

    private String generateSecretKey() {
        // Генерировать случайный 64-байтный ключ
        byte[] randomBytes = new byte[64];
        new Random().nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    private int getNextVersion() {
        return keyStore.size() + 1;
    }
}

