package ru.shanina.securityframework.core_supply_chain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SLSA Attestation for build provenance
 * SLSA Level 1, 2, 3, 4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlsaAttestation {
    private String version;
    private String slsaVersion; // 1.0
    private BuildMetadata buildMetadata;
    private MaterialsMetadata materialsMetadata;
    private String signature;
    private String keyId;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildMetadata {
        private String id;
        private String type;
        private String startedOn;
        private String finishedOn;
        private String builder;
        private String sourceUri;
        private String invoker;
        private String config;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialsMetadata {
        private String uri;
        private String digest;
        private String downloadLocation;
    }
}

