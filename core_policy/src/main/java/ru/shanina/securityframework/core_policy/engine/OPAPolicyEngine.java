package ru.shanina.securityframework.core_policy.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.shanina.securityframework.core_policy.model.PolicyContext;
import ru.shanina.securityframework.core_policy.model.PolicyDecision;

import java.util.HashMap;
import java.util.Map;

/**
 * OPA (Open Policy Agent) Integration
 * Evaluates policies written in Rego language against OPA server
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "securitas.policy.engine", havingValue = "opa", matchIfMissing = false)
public class OPAPolicyEngine implements PolicyEngine {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${securitas.policy.opa.url:http://localhost:8181}")
    private String opaUrl;

    @Value("${securitas.policy.opa.timeout:5000}")
    private long timeout;

    public OPAPolicyEngine(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public PolicyDecision evaluate(String policyId, PolicyContext context) {
        try {
            long startTime = System.currentTimeMillis();

            // Prepare input for OPA
            Map<String, Object> input = new HashMap<>();
            input.put("user", context.getUserAttributes());
            input.put("resource", context.getResourceAttributes());
            input.put("action", context.getAction());
            input.put("environment", context.getEnvironmentAttributes());

            // Call OPA API: POST /data/{policyId}
            String opaPath = "/data/" + policyId.replace(".", "/");

            JsonNode response = webClient.post()
                .uri(opaUrl + opaPath)
                .bodyValue(Map.of("input", input))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            if (response != null && response.has("result")) {
                JsonNode result = response.get("result");

                // OPA returns allow: boolean
                boolean allowed = result.has("allow") && result.get("allow").asBoolean(false);
                String reason = result.has("reason") ? result.get("reason").asText() : "OPA decision";

                long evaluationTime = System.currentTimeMillis() - startTime;

                return PolicyDecision.builder()
                    .allowed(allowed)
                    .reason(reason)
                    .policyId(policyId)
                    .evaluatedBy("OPA")
                    .evaluationTimeMs(evaluationTime)
                    .context(input)
                    .build();
            }

            log.warn("OPA returned empty response for policy: {}", policyId);
            return new PolicyDecision(false, "OPA evaluation failed: empty response");

        } catch (Exception e) {
            log.error("OPA policy evaluation error for policy: " + policyId, e);
            return new PolicyDecision(false, "OPA evaluation error: " + e.getMessage());
        }
    }

    @Override
    public boolean policyExists(String policyId) {
        try {
            String checkPath = "/data/" + policyId.replace(".", "/");
            Boolean exists = webClient.head()
                .uri(opaUrl + checkPath)
                .retrieve()
                .toBodilessEntity()
                .map(r -> r.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .block();

            return exists != null && exists;
        } catch (Exception e) {
            log.warn("Error checking OPA policy existence: {}", policyId, e);
            return false;
        }
    }

    @Override
    public void reloadPolicies() {
        try {
            // OPA doesn't need explicit reload - it watches files
            log.info("OPA policies reloading (file watchers enabled)");
        } catch (Exception e) {
            log.error("OPA reload error", e);
        }
    }

    @Override
    public void registerPolicy(String policyId, String policyContent) {
        try {
            String policyPath = "/policies/" + policyId;
            webClient.put()
                .uri(opaUrl + policyPath)
                .bodyValue(Map.of("rules", policyContent))
                .retrieve()
                .toBodilessEntity()
                .block();

            log.info("Policy registered in OPA: {}", policyId);
        } catch (Exception e) {
            log.error("Failed to register policy in OPA: {}", policyId, e);
        }
    }
}

