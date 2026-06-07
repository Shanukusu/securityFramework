package ru.shanina.securityframework.core_supply_chain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Software Bill of Materials (SBOM)
 * CycloneDX or SPDX format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sbom {
    private String bomVersion;
    private String specVersion;
    private String format; // cyclonedx or spdx
    private List<SbomComponent> components;
    private String generatedAt;
    private String generatedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SbomComponent {
        private String type; // library, application, framework
        private String name;
        private String version;
        private String scope; // required, optional
        private List<String> licenses;
        private String packageUrl;
    }
}

