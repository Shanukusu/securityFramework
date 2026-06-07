package ru.shanina.securityframework.core_policy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Policy Engine Decision Result
 * Contains decision (allow/deny) with reasoning and audit trail
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDecision {
    private boolean allowed;
    private String reason;
    private String policyId;
    private long evaluationTimeMs;
    private Map<String, Object> context = new HashMap<>();
    private String evaluatedBy; // "OPA", "ABAC", "RULE_ENGINE"

    public PolicyDecision(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
        this.evaluationTimeMs = System.currentTimeMillis();
    }
}

