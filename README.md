# Security Framework v2.0

A **production-grade, compile-time safe security framework** for Java applications with enterprise security features.

## 🎯 Core Features

### 1. Type-Safe RBAC
- Enum-based roles instead of strings
- `@Authorize(Role.ADMIN)` compile-time checked
- Multi-layer security with policies

### 2. Enterprise Security Modules

#### 🔐 Policy Engine
- OPA/Rego integration for ABAC policies
- `@Policy("employee-access")` annotations
- Caffeine-based policy caching

#### 🔒 Service Mesh Security
- Istio/Linkerd mTLS automation
- Certificate rotation
- `@MeshSecured` annotation

#### 🔍 Security Scanner
- Trivy, Dependency-Check, Grype integration
- SBOM generation (CycloneDX)
- CVE vulnerability tracking

#### ✅ Compliance
- PCI DSS, ГОСТ 57580, ISO 27001, OWASP ASVS
- Automated compliance verification
- Report generation for auditors

#### 🛡️ API Security
- Rate limiting (token bucket, sliding window)
- API Key management
- Anti-replay protection
- DDoS detection

#### 🔄 Secret Rotation
- Automatic secret rotation
- Certificate renewal
- Graceful/Blue-Green deployment strategies
- K8s CronJob integration

#### 📊 Observability Security
- OpenTelemetry tracing for security events
- Jaeger/Tempo backends
- Prometheus metrics
- Auto-generated Grafana dashboards

#### 📦 Supply Chain Security
- SBOM generation (CycloneDX)
- Artifact signing (Cosign/Sigstore)
- SLSA attestation v1.0
- Transitive vulnerability detection

### 3. Existing Core Features
- **Production-Grade Vault Integration** with Caffeine caching
- **Decoupled Audit System** for async/batch/messaging
- **K8s Security Analyzer** with auto-generated NetworkPolicy
- **OAuth2/JWT** Support

## 📦 All Modules

| Module | Purpose | Status |
|--------|---------|--------|
| **core_iam** | OAuth2, JWT, RBAC | ✅ Stable |
| **core_audit** | Audit logging & context providers | ✅ Stable |
| **core_vault** | Secret management | ✅ Stable |
| **core_k8s_generator** | K8s manifest generation | ✅ Stable |
| **core_policy** | OPA/ABAC policies | ✅ New |
| **core_mesh** | Service mesh security | ✅ New |
| **core_scanner** | Vulnerability scanning | ✅ New |
| **core_compliance** | Compliance frameworks | ✅ New |
| **core_api_security** | API protection | ✅ New |
| **core_secret_rotation** | Secret rotation | ✅ New |
| **core_observability_security** | Security tracing | ✅ New |
| **core_supply_chain** | Supply chain security | ✅ New |

## 🚀 Quick Start

### 1. Add Dependencies
```xml
<!-- Core IAM -->
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_iam</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<!-- New: Policy Engine -->
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_policy</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<!-- New: API Security -->
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_api_security</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<!-- All other modules... -->
```

### 2. Configure (application.yml)
```yaml
securitas:
  policy:
    enabled: true
    engine: abac
  
  api-security:
    enabled: true
    rateLimit:
      requestsPerSecond: 1000
  
  security-scanner:
    enabled: true
    trivy:
      enabled: true
```

### 3. Use Type-Safe Annotations
```java
@RateLimited(requestsPerSecond = 100)
@Policy("payment-policy")
@Authorize(Role.MERCHANT)
@PostMapping("/payments")
public PaymentResponse createPayment(@RequestBody PaymentRequest req) { ... }
```

## 📚 Multi-Layer Security Example

```java
@RestController
@RequestMapping("/api/v1/sensitive")
public class SensitiveController {
    
    // Layer 1: Rate Limiting
    @RateLimited(requestsPerSecond = 50)
    
    // Layer 2: API Key Validation
    @ApiKeyRequired(scope = "sensitive:read")
    
    // Layer 3: Type-Safe RBAC
    @Authorize(Role.SENSITIVE_DATA_READER)
    
    // Layer 4: Policy Engine (ABAC)
    @Policy("sensitive-data-policy")
    
    // Layer 5: Vulnerability Gate
    @Scanned(severity = "CRITICAL")
    
    // Layer 6: Compliance
    @Compliant(frameworks = {ComplianceFramework.ISO_27001})
    
    // Layer 7: Distributed Tracing
    @Traced("sensitive.data.access")
    
    // Layer 8: Audit
    @Audit(event = "SENSITIVE_DATA_ACCESS", level = "CRITICAL")
    @GetMapping("/data")
    public SensitiveData getData() {
        return sensitiveService.getData();
    }
}
```

## 🔒 Kubernetes Security

```bash
# Generate security-aware K8s manifests
mvn compile ru.shanina.securityFramework:core_k8s_generator:k8s-generate

# Also generated automatically:
# - NetworkPolicy (from code analysis)
# - PeerAuthentication (mTLS from core_mesh)
# - SecurityPolicy (from core_scanner)
# - CronJob (secret rotation from core_secret_rotation)
# - ServiceMonitor (Prometheus from core_observability_security)
```

## 📋 Configuration

```yaml
securitas:
  # Policy Engine (OPA/ABAC)
  policy:
    enabled: true
    engine: abac  # or opa
  
  # Service Mesh (Istio/Linkerd)
  mesh:
    enabled: true
    provider: istio
    mtls:
      enforcement: STRICT
  
  # Vulnerability Scanning
  scanner:
    enabled: true
    trivy:
      enabled: true
  
  # Compliance
  compliance:
    enabled: true
    frameworks: [PCI_DSS, ISO_27001]
  
  # Secret Rotation
  secret-rotation:
    enabled: true
    interval: 30d
  
  # Observability
  observability:
    enabled: true
    tracing:
      backend: jaeger
```

## 🧪 Testing

```bash
mvn clean test

# With integration tests (requires Docker)
mvn verify -Dgroups=IntegrationTest

# Security scanning
mvn org.owasp:dependency-check-maven:check

# Generate SBOM
mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregate
```

## 📚 Documentation

- [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) - Complete integration guide
- [IMPROVEMENTS.md](IMPROVEMENTS.md) - v2.0 improvements
- [SUMMARY.md](SUMMARY.md) - Architecture overview

## 💡 Why This Framework

✅ Type-safe security code  
✅ Multi-layer protection  
✅ Kubernetes-native  
✅ OAuth2/JWT ready  
✅ Production-grade features  
✅ Automatic security analysis  
✅ Enterprise compliance ready  
✅ Cloud-native design  
✅ Zero-trust support  
✅ DevSecOps integrated
