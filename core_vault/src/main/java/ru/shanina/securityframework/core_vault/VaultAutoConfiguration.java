package ru.shanina.securityframework.core_vault;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultOperations;

@Configuration
@ConditionalOnProperty(name = "securitas.vault.enabled", havingValue = "true")
public class VaultAutoConfiguration {

    @Bean
    public CachingVaultPropertyResolver cachingVaultPropertyResolver(VaultOperations vaultOperations, VaultProperties properties) {
        return new CachingVaultPropertyResolver(vaultOperations, properties);
    }
}
