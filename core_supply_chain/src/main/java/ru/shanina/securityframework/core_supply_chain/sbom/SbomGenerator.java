package ru.shanina.securityframework.core_supply_chain.sbom;

import ru.shanina.securityframework.core_supply_chain.model.Sbom;

/**
 * SBOM Generator Interface
 */
public interface SbomGenerator {

    /**
     * Generate SBOM from project
     */
    Sbom generateSbom(String projectPath);

    /**
     * Export SBOM to file
     */
    void exportSbom(Sbom sbom, String outputPath);

    /**
     * Get generator name
     */
    String getGeneratorName();
}

