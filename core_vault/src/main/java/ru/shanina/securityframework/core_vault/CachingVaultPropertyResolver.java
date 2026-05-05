package ru.shanina.securityframework.core_vault;

import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.env.VaultPropertySource;
import java.util.concurrent.*;

public class CachingVaultPropertyResolver {
    private final VaultOperations vaultOperations;
    private final VaultProperties properties;
    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public CachingVaultPropertyResolver(VaultOperations vaultOps, VaultProperties props) {
        this.vaultOperations = vaultOps;
        this.properties = props;
        scheduler.scheduleAtFixedRate(this::evictExpired, 1, 1, TimeUnit.MINUTES);
    }

    public String getSecret(String path) {
        CacheEntry entry = cache.get(path);
        if (entry != null && entry.expiryTime > System.currentTimeMillis()) {
            return entry.value;
        }
        // Исправленный порядок аргументов, если необходимо:
        VaultPropertySource source = new VaultPropertySource(vaultOperations, path);
        String value = (String) source.getProperty(path);
        cache.put(path, new CacheEntry(value, System.currentTimeMillis() + 300_000));
        return value;
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(e -> e.getValue().expiryTime <= now);
    }

    private static class CacheEntry {
        final String value;
        final long expiryTime;

        CacheEntry(String value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }
}
