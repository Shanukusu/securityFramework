package ru.shanina.securityframework.core_policy.engine;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.shanina.securityframework.core_policy.model.PolicyContext;
import ru.shanina.securityframework.core_policy.model.PolicyDecision;

import java.util.*;

/**
 * ABAC (Attribute-Based Access Control) Policy Engine
 * Simple rule engine for ABAC policies without external OPA dependency
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "securitas.policy.engine", havingValue = "abac", matchIfMissing = true)
public class ABACPolicyEngine implements PolicyEngine {
    
    private final Map<String, ABACRule> rules = new HashMap<>();
    
    public ABACPolicyEngine() {
        initializeDefaultRules();
    }
    
    @Override
    public PolicyDecision evaluate(String policyId, PolicyContext context) {
        try {
            long startTime = System.currentTimeMillis();
            ABACRule rule = rules.get(policyId);
            
            if (rule == null) {
                log.warn("ABAC policy not found: {}", policyId);
                return new PolicyDecision(false, "Policy not found: " + policyId);
            }
            
            boolean allowed = rule.evaluate(context);
            long evaluationTime = System.currentTimeMillis() - startTime;
            
            return PolicyDecision.builder()
                .allowed(allowed)
                .reason(allowed ? "Policy satisfied" : "Policy denied: " + rule.getDenyReason())
                .policyId(policyId)
                .evaluatedBy("ABAC")
                .evaluationTimeMs(evaluationTime)
                .build();
            
        } catch (Exception e) {
            log.error("ABAC policy evaluation error: {}", policyId, e);
            return new PolicyDecision(false, "ABAC evaluation error: " + e.getMessage());
        }
    }
    
    @Override
    public boolean policyExists(String policyId) {
        return rules.containsKey(policyId);
    }
    
    @Override
    public void reloadPolicies() {
        log.info("ABAC policies reloaded");
    }
    
    @Override
    public void registerPolicy(String policyId, String policyContent) {
        // Parse and register custom ABAC policy
        // For now, this is a placeholder
        log.info("Custom ABAC policy registered: {}", policyId);
    }
    
    private void initializeDefaultRules() {
        // Example: Only admins can access /admin
        rules.put("admin-access", new ABACRule()
            .addCondition(ctx -> "ADMIN".equals(
                ctx.getUserAttributes().get("role")))
        );
        
        // Example: Employees can access /employee
        rules.put("employee-access", new ABACRule()
            .addCondition(ctx -> {
                String role = (String) ctx.getUserAttributes().get("role");
                return "ADMIN".equals(role) || "EMPLOYEE".equals(role);
            })
        );
        
        // Example: Only verified users can access /sensitive
        rules.put("verified-access", new ABACRule()
            .addCondition(ctx -> (boolean) ctx.getUserAttributes().getOrDefault("verified", false))
        );
    }
    
    /**
     * Simple ABAC Rule implementation
     */
    public static class ABACRule {
        private final List<ABACCondition> conditions = new ArrayList<>();
        private String denyReason = "Condition not satisfied";
        
        public boolean evaluate(PolicyContext context) {
            for (ABACCondition condition : conditions) {
                if (!condition.evaluate(context)) {
                    return false;
                }
            }
            return true;
        }
        
        public ABACRule addCondition(ABACCondition condition) {
            this.conditions.add(condition);
            return this;
        }
        
        public String getDenyReason() {
            return denyReason;
        }
        
        public ABACRule setDenyReason(String reason) {
            this.denyReason = reason;
            return this;
        }
    }
    
    @FunctionalInterface
    public interface ABACCondition {
        boolean evaluate(PolicyContext context);
    }
}

