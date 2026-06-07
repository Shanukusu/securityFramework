package ru.shanina.securityframework.core_policy.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shanina.securityframework.core_policy.model.PolicyContext;
import ru.shanina.securityframework.core_policy.model.PolicyDecision;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ABACPolicyEngineTest {

    private ABACPolicyEngine policyEngine;

    @BeforeEach
    void setUp() {
        policyEngine = new ABACPolicyEngine();
    }

    @Test
    void testAdminAccessPolicy() {
        // GIVEN: Admin user context
        PolicyContext context = PolicyContext.builder()
            .userId("admin1")
            .build();
        context.addUserAttribute("role", "ADMIN");

        // WHEN: Evaluate admin-access policy
        PolicyDecision decision = policyEngine.evaluate("admin-access", context);

        // THEN: Decision should be allowed
        assertTrue(decision.isAllowed());
        assertEquals("admin-access", decision.getPolicyId());
        assertEquals("ABAC", decision.getEvaluatedBy());
    }

    @Test
    void testAdminAccessPolicyDenied() {
        // GIVEN: Regular user context
        PolicyContext context = PolicyContext.builder()
            .userId("user1")
            .build();
        context.addUserAttribute("role", "USER");

        // WHEN: Evaluate admin-access policy
        PolicyDecision decision = policyEngine.evaluate("admin-access", context);

        // THEN: Decision should be denied
        assertFalse(decision.isAllowed());
    }

    @Test
    void testEmployeeAccessPolicy() {
        // GIVEN: Employee user context
        PolicyContext context = PolicyContext.builder()
            .userId("emp1")
            .build();
        context.addUserAttribute("role", "EMPLOYEE");

        // WHEN: Evaluate employee-access policy
        PolicyDecision decision = policyEngine.evaluate("employee-access", context);

        // THEN: Decision should be allowed
        assertTrue(decision.isAllowed());
    }

    @Test
    void testVerifiedAccessPolicy() {
        // GIVEN: Verified user
        PolicyContext context = PolicyContext.builder()
            .userId("user1")
            .build();
        context.addUserAttribute("verified", true);

        // WHEN: Evaluate verified-access policy
        PolicyDecision decision = policyEngine.evaluate("verified-access", context);

        // THEN: Decision should be allowed
        assertTrue(decision.isAllowed());
    }

    @Test
    void testVerifiedAccessPolicyDenied() {
        // GIVEN: Unverified user
        PolicyContext context = PolicyContext.builder()
            .userId("user1")
            .build();
        context.addUserAttribute("verified", false);

        // WHEN: Evaluate verified-access policy
        PolicyDecision decision = policyEngine.evaluate("verified-access", context);

        // THEN: Decision should be denied
        assertFalse(decision.isAllowed());
    }

    @Test
    void testPolicyNotFound() {
        // GIVEN: Non-existent policy
        PolicyContext context = PolicyContext.builder().userId("user1").build();

        // WHEN: Evaluate non-existent policy
        PolicyDecision decision = policyEngine.evaluate("non-existent", context);

        // THEN: Decision should be denied
        assertFalse(decision.isAllowed());
        assertTrue(decision.getReason().contains("not found"));
    }

    @Test
    void testPolicyExists() {
        // GIVEN: Known policy ID
        String policyId = "admin-access";

        // WHEN: Check if policy exists
        boolean exists = policyEngine.policyExists(policyId);

        // THEN: Policy should exist
        assertTrue(exists);
    }

    @Test
    void testPolicyDoesNotExist() {
        // GIVEN: Unknown policy ID
        String policyId = "non-existent-policy";

        // WHEN: Check if policy exists
        boolean exists = policyEngine.policyExists(policyId);

        // THEN: Policy should not exist
        assertFalse(exists);
    }
}

