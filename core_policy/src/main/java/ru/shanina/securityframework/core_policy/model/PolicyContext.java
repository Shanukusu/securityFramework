package ru.shanina.securityframework.core_policy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Policy Context for evaluation
 * Contains all attributes needed for ABAC policy evaluation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyContext {
    private String userId;
    private String action;
    private String resource;
    private String resourceType;
    private Map<String, Object> userAttributes = new HashMap<>();
    private Map<String, Object> resourceAttributes = new HashMap<>();
    private Map<String, Object> environmentAttributes = new HashMap<>();
    
    // Add attribute to context
    public void addUserAttribute(String key, Object value) {
        userAttributes.put(key, value);
    }
    
    public void addResourceAttribute(String key, Object value) {
        resourceAttributes.put(key, value);
    }
    
    public void addEnvironmentAttribute(String key, Object value) {
        environmentAttributes.put(key, value);
    }
}

