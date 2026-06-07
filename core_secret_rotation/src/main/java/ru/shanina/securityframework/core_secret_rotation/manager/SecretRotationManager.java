package ru.shanina.securityframework.core_secret_rotation.manager;

/**
 * Secret Rotation Manager Interface
 */
public interface SecretRotationManager {

    /**
     * Rotate secret
     */
    boolean rotateSecret(String secretName);

    /**
     * Check rotation status
     */
    String getRotationStatus(String secretName);
}

