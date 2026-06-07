package ru.shanina.securityframework.core_vault.transit;

/**
 * Transit Engine Interface - для шифрования/расшифровки без хранения ключей в приложении
 */
public interface TransitEngineService {
    
    /**
     * Зашифровать данные используя Vault Transit Engine
     */
    String encrypt(String plaintext, String keyName) throws Exception;
    
    /**
     * Расшифровать данные используя Vault Transit Engine
     */
    String decrypt(String ciphertext, String keyName) throws Exception;
    
    /**
     * Переключить зашифрованные данные на новый ключ
     */
    String rewrap(String ciphertext, String keyName) throws Exception;
    
    /**
     * Сгенерировать HMAC подпись
     */
    String generateHmac(String data, String keyName) throws Exception;
    
    /**
     * Получить информацию о ключе
     */
    KeyInfo getKeyInfo(String keyName) throws Exception;
    
    /**
     * Информация о ключе
     */
    interface KeyInfo {
        String getKeyName();
        int getKeyVersion();
        boolean isSupportsEncryption();
        boolean isSupportsDecryption();
        boolean isSupportsHashing();
        boolean isSupportsSigning();
    }
}

