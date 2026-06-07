# Security Framework v2.0 - Modules Overview

## 📦 Complete Module Reference

### 🟢 Core Modules (v1.0)

#### core_iam
**Purpose:** Identity & Access Management  
**Features:**
- OAuth2 / OpenID Connect
- JWT token validation
- Type-safe RBAC with enums
- Role extraction from tokens
- Token refresh support

**Key Class:** `AuthorizationPolicy`, `@Authorize`

---

#### core_audit
**Purpose:** Auditing & Compliance Logging  
**Features:**
- Comprehensive audit trail
- Context provider abstraction
- Works with async/batch/messaging
- Plugin-based event handlers
- Multi-destination support

**Key Class:** `AuditEventHandler`, `AuditAspect`

---

#### core_vault
**Purpose:** Secret Management  
**Features:**
- HashiCorp Vault integration
- Caffeine-based caching (5m TTL)
- Automatic cache refresh
- Async-safe architecture
- Lease management

**Key Class:** `CachingVaultPropertyResolver`

---

#### core_k8s_generator
**Purpose:** Kubernetes Manifest Generation  
**Features:**
- NetworkPolicy from code analysis
- RBAC from policies
- SecurityContext configuration
- StatefulSet/Deployment templates
- Auto-discovery of service dependencies

**Key Class:** `K8sManifestGeneratorMojo`, `SecurityAnalyzer`

---

### 🆕 New Modules (v2.0)

#### ✅ core_policy
**Purpose:** Advanced Authorization with Policies  
**Features:**
- OPA/Rego support (external)
- ABAC policy engine (included)
- Caffeine decision caching
- AOP-based enforcement
- Multi-condition policies

**Configuration:**
```yaml
securitas:
  policy:
    enabled: true
    engine: abac  # or opa
```

**Usage:**
```java
@Policy("employee-access")
public void operation() { }
```

**Dependencies:** core_iam, core_audit

---

#### ✅ core_mesh
**Purpose:** Service Mesh Integration  
**Features:**
- Istio / Linkerd support
- SPIFFE identity model
- mTLS enforcement (STRICT/PERMISSIVE)
- Certificate rotation (90-day)
- K8s resource generation

**Configuration:**
```yaml
securitas:
  mesh:
    enabled: true
    provider: istio
    mtls:
      enforcement: STRICT
```

**Usage:**
```java
@MeshSecured(mtlsRequired = true)
public class Service { }
```

**Dependencies:** core_k8s_generator

**Generated Resources:**
- `PeerAuthentication` (mTLS policy)
- `DestinationRule` (TLS config)
- `VirtualService` (traffic management)

---

#### ✅ core_scanner
**Purpose:** Vulnerability Detection  
**Features:**
- Trivy CLI integration
- Dependency-Check support
- Grype integration
- SBOM generation (CycloneDX)
- Severity gating (CRITICAL/HIGH/MEDIUM/LOW)
- Exception allowlisting
- CVE tracking

**Configuration:**
```yaml
securitas:
  scanner:
    enabled: true
    trivy:
      enabled: true
```

**Usage:**
```java
@Scanned(severity = "HIGH")
public class Service { }
```

**Generated Artifacts:**
- `sbom.cyclonedx.json` (Bill of Materials)
- Vulnerability reports
- Risk assessment

---

#### ✅ core_compliance
**Purpose:** Regulatory Compliance  
**Features:**
- Multi-framework support:
  - PCI DSS 3.2.1
  - ГОСТ 57580-2021
  - ISO 27001:2013
  - OWASP ASVS
- Automated verification
- Evidence collection
- Prometheus metrics
- Auditor reports

**Configuration:**
```yaml
securitas:
  compliance:
    enabled: true
    frameworks: [PCI_DSS, ISO_27001]
```

**Usage:**
```java
@Compliant(frameworks = ComplianceFramework.PCI_DSS)
public void operation() { }
```

**Metrics:**
- `securitas_compliance_score` (0-100)
- `securitas_control_status_*` (per control)

---

#### ✅ core_api_security
**Purpose:** API Protection  
**Features:**
- Rate limiting (token bucket)
- API key validation
- Anti-replay detection
- DDoS detection
- Request signing
- Distributed rate limiting (Redis support)

**Configuration:**
```yaml
securitas:
  api-security:
    enabled: true
    rateLimit:
      requestsPerSecond: 1000
    ddos:
      enabled: true
```

**Usage:**
```java
@RateLimited(requestsPerSecond = 100)
@ApiKeyRequired(scope = "api:write")
public void operation() { }
```

---

#### ✅ core_secret_rotation
**Purpose:** Automated Secret Management  
**Features:**
- Automatic secret rotation
- Certificate renewal
- Database credential rotation
- API key rotation
- Rotation strategies:
  - Safe (with health checks)
  - Blue-Green (zero downtime)
  - Graceful (connection draining)
  - Canary (gradual rollout)
- Rollback on failure
- K8s CronJob generation

**Configuration:**
```yaml
securitas:
  secret-rotation:
    enabled: true
    interval: 30d
    policy: graceful
```

**Usage:**
```java
@RotatedSecret(rotationIntervalDays = 30)
@Value("${db.password}")
private String dbPassword;

@RotationCallback
public void onRotation() { }
```

---

#### ✅ core_observability_security
**Purpose:** Security Event Tracing & Metrics  
**Features:**
- OpenTelemetry integration
- Jaeger/Tempo backend support
- Distributed tracing hierarchy
- Security span creation
- Prometheus metrics export
- Auto-generated Grafana dashboards
- Anomaly detection hooks

**Configuration:**
```yaml
securitas:
  observability:
    enabled: true
    tracing:
      backend: jaeger
      jaeger:
        endpoint: http://localhost:14268/api/traces
```

**Usage:**
```java
@Traced("payment.process")
public void operation() { }
```

**Metrics Generated:**
- Authentication attempts/success/failure
- Authorization denials
- Policy evaluation duration
- Vulnerability count by severity
- Compliance score
- Secret rotation status

---

#### ✅ core_supply_chain
**Purpose:** Supply Chain Security & Attestation  
**Features:**
- SBOM generation (CycloneDX/SPDX)
- Artifact signing:
  - Cosign
  - Sigstore (keyless)
  - OpenPGP
- SLSA attestation (v1.0, Level 1-4)
- Build provenance tracking
- Transitive CVE detection
- Supply chain risk assessment
- License compliance checking

**Configuration:**
```yaml
securitas:
  supply-chain:
    enabled: true
    sbom:
      format: cyclonedx
    signing:
      provider: sigstore
    slsa:
      minimumLevel: 2
```

**Usage:**
```java
@SupplyChainSecured(
    requireSlsaLevel = 2,
    verifySignatures = true
)
public class Application { }
```

**Generated Artifacts:**
- `sbom.cyclonedx.json`
- `image.sig` (Cosign)
- `attestation.slsa.json`
- Risk assessment report

---

## 📊 Module Comparison Matrix

| Feature | Policy | Mesh | Scanner | Compliance | API Sec | Rotation | Obs | Supply Chain |
|---------|--------|------|---------|-----------|---------|----------|-----|--------------|
| **RBAC** | ✅ | - | - | - | ✅ | - | - | - |
| **Policies** | ✅ | - | - | ✅ | - | - | - | - |
| **mTLS** | - | ✅ | - | - | - | - | - | - |
| **Scanning** | - | - | ✅ | ✅ | - | - | - | ✅ |
| **Tracing** | - | - | - | - | - | - | ✅ | - |
| **Metrics** | ✅ | - | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **K8s Gen** | ✅ | ✅ | ✅ | ✅ | - | ✅ | - | - |
| **Audit** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

---

## 🔄 Module Interaction Diagram

```
                    ┌─────────────┐
                    │   core_iam  │
                    │  (RBAC)     │
                    └──────┬──────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │   core_     │ │ core_api    │ │   core_     │
    │  policy     │ │  security   │ │   mesh      │
    │ (ABAC/OPA)  │ │  (RateLimit)│ │   (mTLS)    │
    └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
           │               │               │
           │               │               │
      ┌────┴───────────────┼───────────────┴────┐
      │                    │                    │
      ▼                    ▼                    ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   core_     │    │   core_     │    │   core_     │
│  compliance │    │  scanner    │    │  secret_    │
│(PCI/GOST)   │    │(Trivy/SBOM) │    │ rotation    │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                  │
       │                  │                  │
       └──────────────────┼──────────────────┘
                          │
                          ▼
              ┌─────────────────────────┐
              │  core_observability_    │
              │      security           │
              │  (Tracing/Metrics)      │
              └──────────┬──────────────┘
                         │
                         ▼
              ┌─────────────────────────┐
              │  core_supply_chain      │
              │  (SBOM/Signing/SLSA)    │
              └─────────────────────────┘
```

---

## 🎯 Use Cases by Module

### core_policy
- Fine-grained authorization based on attributes
- Temporary access grants
- Context-aware access control
- Team-based authorization

### core_mesh
- Service-to-service communication security
- Multi-cluster deployments
- Compliance with zero-trust networking
- Automatic certificate management

### core_scanner
- Continuous vulnerability monitoring
- Supply chain security validation
- Compliance with security standards
- Build-time security gates

### core_compliance
- Regulatory requirement verification
- Audit evidence collection
- Compliance scoring & reporting
- Control implementation tracking

### core_api_security
- Public API protection
- Rate limiting enforcement
- DDoS protection
- API key management

### core_secret_rotation
- Database password rotation
- API key expiration
- Certificate renewal
- Kubernetes secret updates

### core_observability_security
- Real-time security monitoring
- Anomaly detection
- Performance analysis
- Compliance trend tracking

### core_supply_chain
- Container image signing
- Build provenance tracking
- Dependency verification
- License compliance

---

## 🔌 Extension Points

Each module provides clear interfaces for customization:

```java
// Implement custom policy engine
public class CustomPolicyEngine implements PolicyEngine { }

// Implement custom audit handler
public class ElasticsearchAuditHandler implements AuditEventHandler { }

// Implement custom scanner
public class CustomVulnerabilityScanner implements VulnerabilityScanner { }

// Implement custom compliance validator
public class CustomComplianceEngine implements ComplianceEngine { }

// Implement custom rotation manager
public class CustomSecretRotationManager implements SecretRotationManager { }
```

---

## 📈 Deployment Patterns

### Minimal (Development)
```yaml
securitas:
  policy: {enabled: true}
  api-security: {enabled: true}
```

### Standard (Staging)
```yaml
securitas:
  policy: {enabled: true}
  scanner: {enabled: true}
  compliance: {enabled: true}
  api-security: {enabled: true}
  observability: {enabled: true}
```

### Enterprise (Production)
```yaml
securitas:
  policy: {enabled: true, engine: opa}
  mesh: {enabled: true}
  scanner: {enabled: true}
  compliance: {enabled: true}
  api-security: {enabled: true}
  secret-rotation: {enabled: true}
  observability: {enabled: true}
  supply-chain: {enabled: true}
```

---

## 🚀 Recommended Module Combinations

### Combination 1: Startup
- core_policy (light authorization)
- core_api_security (protect APIs)
- core_audit (logging)

### Combination 2: Mid-Market
- core_policy (ABAC)
- core_mesh (service security)
- core_scanner (vulnerability)
- core_compliance (ISO 27001)
- core_api_security (rate limiting)
- core_observability_security (monitoring)

### Combination 3: Enterprise
- All modules enabled
- OPA for complex policies
- Istio for mesh
- Supply chain security
- Full compliance automation

---

**Version:** 2.0.0 | **Last Updated:** May 7, 2026

