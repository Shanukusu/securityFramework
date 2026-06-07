package ru.shanina.securityframework.core_compliance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Compliance Framework Enum
 */
public enum ComplianceFramework {
    PCI_DSS("Payment Card Industry Data Security Standard"),
    GOST_57580("ГОСТ 57580-2021 - Russian Security Standard"),
    ISO_27001("ISO 27001:2013 Information Security Management"),
    OWASP_ASVS("OWASP Application Security Verification Standard"),
    SOC2("SOC 2 Compliance"),
    HIPAA("Health Insurance Portability and Accountability Act");

    private final String description;

    ComplianceFramework(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

