package ru.shanina.securityframework.securityframework.core_vault;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponseSupport;

@Component
@ConditionalOnProperty(name = "securitas.vault.enabled", havingValue = "true")
public class VaultSecretManager {
    @Autowired(required = false)
    private VaultOperations vaultOperations;

    public String getSecret(String path, String key) {
        if (vaultOperations == null) return null;
        VaultResponseSupport<Map> response = vaultOperations.read(path);
        if (response != null && response.getData() != null) {
            Object value = response.getData().get(key);
            return value != null ? value.toString() : null;
        }
        return null;
    }
}
