package ru.shanina.securityframework.core_policy.engine;

import ru.shanina.securityframework.core_policy.model.PolicyContext;
import ru.shanina.securityframework.core_policy.model.PolicyDecision;

/**
 * Core Policy Engine Interface
 * Implementations: OPA, ABAC, Rule-based engines
 */
public interface PolicyEngine {
    
    /**
     * Evaluate a policy against context
     * @param policyId Policy identifier or name
     * @param context Policy evaluation context
     * @return Policy decision (allow/deny with reasoning)
     */
    PolicyDecision evaluate(String policyId, PolicyContext context);
    
    /**
     * Check if policy exists
     */
    boolean policyExists(String policyId);
    
    /**
     * Load or reload policies from backend
     */
    void reloadPolicies();
    
    /**
     * Add custom policy at runtime
     */
    void registerPolicy(String policyId, String policyContent);
}

