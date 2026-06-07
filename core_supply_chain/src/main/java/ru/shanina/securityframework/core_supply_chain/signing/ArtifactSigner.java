package ru.shanina.securityframework.core_supply_chain.signing;

/**
 * Artifact Signer Interface
 * Implementations: Cosign, Sigstore, OpenPGP
 */
public interface ArtifactSigner {

    /**
     * Sign artifact
     */
    String signArtifact(String artifactPath);

    /**
     * Verify signature
     */
    boolean verifySignature(String artifactPath, String signaturePath);

    /**
     * Get signer name
     */
    String getSignerName();
}

