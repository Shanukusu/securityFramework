package ru.shanina.securityframework.core_compliance.engine;

import ru.shanina.securityframework.core_compliance.model.ComplianceFramework;
import java.util.List;

/**
 * Compliance Engine Interface
 */
public interface ComplianceEngine {

    /**
     * Validate compliance for framework
     */
    List<String> validateCompliance(ComplianceFramework framework);

    /**
     * Generate compliance report
     */
    String generateReport(ComplianceFramework framework);
}

