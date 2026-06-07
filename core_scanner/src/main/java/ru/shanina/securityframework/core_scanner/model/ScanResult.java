package ru.shanina.securityframework.core_scanner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Scan Result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResult {
    private String scanId;
    private String target; // image, jar, or source
    private String scanTime;
    private String status; // SUCCESS, FAILED, IN_PROGRESS
    private List<Vulnerability> vulnerabilities;
    private int criticalCount;
    private int highCount;
    private int mediumCount;
    private int lowCount;
    private String sbomLocation; // Path to SBOM file
}

