package ru.shanina.securityframework.core_vault.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cache Encryption Service - AES-GCM для защиты секретов в памяти
 */
public class CacheEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BIT = 128;
    private static final int GCM_IV_LENGTH_BYTE = 12;
    private static final int KEY_SIZE = 256;

    private final SecretKey secretKey;

    public CacheEncryptionService() {
        this.secretKey = generateKey();
    }

    /**
     * Зашифровать секрет для хранения в кэше
     */
    public String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[GCM_IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        // Объединить IV + ciphertext
        byte[] encryptedData = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryptedData, iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Расшифровать секрет из кэша
     */
    public String decrypt(String encryptedText) throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(encryptedText);

        // Извлечь IV
        byte[] iv = new byte[GCM_IV_LENGTH_BYTE];
        System.arraycopy(encryptedData, 0, iv, 0, GCM_IV_LENGTH_BYTE);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] plaintext = cipher.doFinal(encryptedData, GCM_IV_LENGTH_BYTE,
                                          encryptedData.length - GCM_IV_LENGTH_BYTE);

        return new String(plaintext);
    }

    private SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_SIZE);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }
}

