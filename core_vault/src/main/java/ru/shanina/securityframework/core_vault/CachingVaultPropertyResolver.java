package ru.shanina.securityframework.core_vault;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.env.VaultPropertySource;

import java.time.Duration;

@Slf4j
public class CachingVaultPropertyResolver {
    private final LoadingCache<String, String> cache;
    private final VaultOperations vaultOperations;

    public CachingVaultPropertyResolver(VaultOperations vaultOps, VaultProperties props) {
        this.vaultOperations = vaultOps;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .refreshAfterWrite(Duration.ofMinutes(1))
                .recordStats()
                .build(this::loadSecretFromVault);
    }

    public String getSecret(String path) {
        try {
            return cache.get(path);
        } catch (Exception e) {
            log.error("Failed to get secret from Vault: {}", path, e);
            return null;
        }
    }

    private String loadSecretFromVault(String path) {
        try {
            VaultPropertySource source = new VaultPropertySource(vaultOperations, path);
            Object value = source.getProperty(path);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.warn("Failed to load secret from Vault at path: {}", path, e);
            return null;
        }
    }

    public void invalidate(String path) {
        cache.invalidate(path);
        log.info("Invalidated cache for path: {}", path);
    }

    public void invalidateAll() {
        cache.invalidateAll();
        log.info("Invalidated all cache entries");
    }
}
