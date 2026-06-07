package ru.shanina.securityframework.core_compliance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * Compliance Control
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceControl {
    private String controlId;
    private String controlName;
    private String framework;
    private String status; // COMPLIANT, NON_COMPLIANT, PARTIAL, NOT_TESTED
    private String description;
    private String evidence;
    private Map<String, Object> metadata;
}

