package ru.shanina.securityframework.sample_app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.shanina.securityframework.core_api_security.annotation.RateLimited;
import ru.shanina.securityframework.core_compliance.annotation.Compliant;
import ru.shanina.securityframework.core_compliance.model.ComplianceFramework;
import ru.shanina.securityframework.core_observability_security.annotation.Traced;
import ru.shanina.securityframework.core_policy.annotation.Policy;
import ru.shanina.securityframework.core_scanner.annotation.Scanned;
import ru.shanina.securityframework.core_supply_chain.annotation.SupplyChainSecured;

/**
 * Secure API Controller demonstrating all Security Framework v2.0 features
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/secure")
@SupplyChainSecured(requireSlsaLevel = 2, verifySignatures = true)
public class SecureApiController {
    
    /**
     * Layer 1: Multi-layer protection example
     * 
     * Security layers applied:
     * 1. Rate Limiting (100 req/sec max)
     * 2. Policy Engine (ABAC evaluation)
     * 3. Vulnerability Gate (HIGH severity blocks)
     * 4. Compliance (PCI DSS + ISO 27001)
     * 5. Distributed Tracing (OpenTelemetry)
     */
    @RateLimited(requestsPerSecond = 100, burstSize = 200)
    @Policy("payment-policy")
    @Scanned(severity = "HIGH")
    @Compliant(frameworks = {
        ComplianceFramework.PCI_DSS,
        ComplianceFramework.ISO_27001
    })
    @Traced("payment.process")
    @PostMapping("/payment")
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        log.info("Processing payment for amount: {}", request.getAmount());
        
        // All security checks have already been performed by:
        // 1. RateLimitingFilter
        // 2. PolicyAspect
        // 3. @Scanned gate
        // 4. ComplianceEngine
        // 5. OpenTelemetry tracing
        
        return PaymentResponse.builder()
            .transactionId("TXN-" + System.currentTimeMillis())
            .status("SUCCESS")
            .amount(request.getAmount())
            .message("Payment processed securely")
            .build();
    }
    
    /**
     * Layer 2: Data access with compliance & observability
     */
    @RateLimited(requestsPerSecond = 1000)
    @Policy("employee-access")
    @Compliant(frameworks = ComplianceFramework.ISO_27001)
    @Traced("employee.data.access")
    @GetMapping("/employee/{id}")
    public EmployeeData getEmployeeData(@PathVariable String id) {
        log.info("Retrieving employee data for ID: {}", id);
        
        return EmployeeData.builder()
            .employeeId(id)
            .name("John Doe")
            .department("Engineering")
            .salary("REDACTED")  // Never expose in logs!
            .build();
    }
    
    /**
     * Layer 3: Health check (no security - public endpoint)
     */
    @GetMapping("/health")
    public HealthResponse health() {
        return HealthResponse.builder()
            .status("HEALTHY")
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    // ========== DTO Classes ==========
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentRequest {
        private String cardNumber;
        private String cardholderName;
        private double amount;
        private String currency;
        private String merchantId;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentResponse {
        private String transactionId;
        private String status;
        private double amount;
        private String message;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EmployeeData {
        private String employeeId;
        private String name;
        private String department;
        private String salary;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class HealthResponse {
        private String status;
        private long timestamp;
    }
}

