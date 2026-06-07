# Security Framework - Architecture & Design

## 🏗️ Overall Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                    Application Layer                           │
│  (Your Spring Boot Application)                                 │
└────────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────────┐
│               Security Framework Layer                          │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   Policy     │  │  API         │  │  Service     │         │
│  │   Engine     │  │  Security    │  │  Mesh        │         │
│  │              │  │              │  │              │         │
│  │ • OPA        │  │ • Rate Limit │  │ • mTLS       │         │
│  │ • ABAC       │  │ • API Keys   │  │ • Istio      │         │
│  │ • Policies   │  │ • Anti-Replay│  │ • Linkerd    │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   Security   │  │  Compliance  │  │  Secret      │         │
│  │   Scanner    │  │              │  │  Rotation    │         │
│  │              │  │ • PCI DSS    │  │              │         │
│  │ • Trivy      │  │ • GOST       │  │ • Auto       │         │
│  │ • Dep Check  │  │ • ISO 27001  │  │   Rotation   │         │
│  │ • SBOM       │  │ • OWASP ASVS │  │ • Certs      │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐                           │
│  │Observability │  │   Supply     │                           │
│  │   Security   │  │   Chain      │                           │
│  │              │  │              │                           │
│  │ • OTel       │  │ • SBOM Gen   │                           │
│  │ • Jaeger     │  │ • Cosign     │                           │
│  │ • Tracing    │  │ • SLSA       │                           │
│  └──────────────┘  └──────────────┘                           │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────────┐
│                  Core Modules Layer                            │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   core_iam   │  │  core_audit  │  │ core_vault   │         │
│  │              │  │              │  │              │         │
│  │ • OAuth2     │  │ • Audit Logs │  │ • Vault API  │         │
│  │ • JWT        │  │ • Context    │  │ • Caching    │         │
│  │ • RBAC       │  │   Providers  │  │ • Secrets    │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         core_k8s_generator + Extensions                 │  │
│  │                                                          │  │
│  │ • K8s Manifest Generation                               │  │
│  │ • NetworkPolicy (from code analysis)                    │  │
│  │ • RBAC (from policies)                                  │  │
│  │ • SecurityPolicy (from scanner)                         │  │
│  │ • PeerAuthentication (from mesh)                        │  │
│  │ • CronJob (from secret rotation)                        │  │
│  │ • ServiceMonitor (from observability)                   │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────────┐
│             External Services & Infrastructure                 │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────┐  ┌────────────┐  ┌──────────────┐  ┌────────┐│
│  │ HashiCorp  │  │    OPA     │  │   Jaeger/   │  │Prometheus
│  │   Vault    │  │  (Policies)│  │   Tempo     │  │        ││
│  └────────────┘  └────────────┘  └──────────────┘  └────────┘│
│                                                                 │
│  ┌────────────┐  ┌────────────┐  ┌──────────────┐             │
│  │ Kubernetes │  │   Istio    │  │    Docker   │             │
│  │  API       │  │  / Linkerd │  │   Registry  │             │
│  └────────────┘  └────────────┘  └──────────────┘             │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

## 📐 Module Dependencies

```
core_iam (foundation)
├── core_audit
├── core_vault
├── core_k8s_generator
│
├── core_policy (depends on: core_iam, core_audit)
│   └── core_compliance (depends on: all core modules + policy)
│
├── core_mesh (depends on: core_k8s_generator)
│   └── core_secret_rotation (depends on: core_vault, core_mesh)
│
├── core_scanner (independent)
│   └── core_supply_chain (depends on: core_scanner)
│
├── core_api_security (depends on: core_iam)
│   └── core_observability_security (depends on: all modules)
│
└── sample_app (depends on: all modules)
```

## 🔄 Data Flow

### Authentication & Authorization Flow

```
HTTP Request
    ↓
[ApiKeyAuthenticationFilter] → Validate API Key
    ↓
[Spring Security Filter Chain] → OAuth2/JWT
    ↓
[RateLimitingFilter] → Check Rate Limit
    ↓
[AntiReplayFilter] → Detect Replay Attacks
    ↓
[DDoSProtectionFilter] → Detect DDoS
    ↓
[@Authorize Interceptor] → RBAC Check
    ↓
[@Policy AOP] → Policy Evaluation (OPA/ABAC)
    ↓
[Business Logic]
    ↓
[@Audit AOP] → Log Security Event
    ↓
[SecurityTracer] → Create OpenTelemetry Span
    ↓
HTTP Response
```

### Secret Management Flow

```
Application Startup
    ↓
[VaultPropertyResolver] → Fetch Secret from Vault
    ↓
[CachingVaultPropertyResolver] → Cache in Caffeine (TTL 5m)
    ↓
[SecretRotationTask] (daily) → Check Expiration
    ↓
[SafeRotationExecutor] → Execute Rotation
    ↓
[HealthCheckValidator] → Verify Success
    ↓
[K8sSecretSyncManager] → Update K8s Secrets
    ↓
[ApplicationReloader] → Reload Configuration
    ↓
[AuditRotationEvents] → Log to Audit
```

### Vulnerability Scanning Flow

```
Build Time / CI/CD
    ↓
[TrivyScanner] → Scan Docker Image/JAR
    ↓
[DependencyCheckScanner] → Scan Dependencies
    ↓
[GrypeScanner] → Alternative Scanner
    ↓
[VulnerabilityAnalyzer] → Aggregate Results
    ↓
[SbomGenerator] → Generate CycloneDX SBOM
    ↓
[ComplianceMapper] → Map to Compliance Frameworks
    ↓
[AlertingService] → Send Alerts if CRITICAL
    ↓
[ReportGenerator] → HTML/JSON Reports
```

## 🎯 Key Design Patterns

### 1. Annotation-Based Security

**Pattern:** Declarative security through annotations

```java
@RateLimited @ApiKeyRequired @Authorize @Policy @Scanned @Compliant
@PostMapping("/endpoint")
public Response handle(Request req) { }
```

**Benefits:**
- Single source of truth for security requirements
- IDE autocompletion
- Compile-time verification (enums)
- AOP aspect weaving

### 2. Multi-Layer Defense

**Pattern:** Defense-in-depth with composable security layers

```
Layer 1: Transport (mTLS from core_mesh)
Layer 2: API Key (from core_api_security)
Layer 3: Rate Limiting (from core_api_security)
Layer 4: Replay Detection (from core_api_security)
Layer 5: RBAC (from core_iam)
Layer 6: ABAC Policies (from core_policy)
Layer 7: Compliance (from core_compliance)
Layer 8: Audit & Tracing (from core_audit + core_observability_security)
```

### 3. Context Providers (Decoupling)

**Pattern:** Abstract security context from HTTP/async/batch

```java
SecurityContextProvider       → Spring Security context
RequestContextProvider        → Servlet request context
AuditEventHandler (plugin)    → Custom audit destinations
```

**Benefits:**
- Works with async code
- Works with messaging (Kafka, RabbitMQ)
- Works with batch jobs
- Easy testing

### 4. Caching Strategy

**Pattern:** Multi-level caching for performance

```java
// Level 1: Local Caffeine Cache (TTL 5m)
LoadingCache<String, Secret> = Caffeine
  .newBuilder()
  .expireAfterWrite(5, MINUTES)
  .refreshAfterWrite(1, MINUTES)
  .build()

// Level 2: Policy Decision Cache (TTL 5m)
policyDecisionCache = Caffeine
  .newBuilder()
  .maximumSize(1000)
  .expireAfterWrite(5, MINUTES)
  .build()

// Level 3: Redis (optional, for distributed)
RedisTemplate<String, Secret>
```

### 5. Plugin Architecture

**Pattern:** Extensibility through interfaces

```java
AuditEventHandler {
  - DatabaseAuditHandler
  - KafkaAuditHandler
  - SyslogAuditHandler
  - CloudLoggingHandler
}

VulnerabilityScanner {
  - TrivyScanner
  - DependencyCheckScanner
  - GrypeScanner
}

PolicyEngine {
  - OPAPolicyEngine
  - ABACPolicyEngine
}
```

## 🔒 Security-First Design

### Principle of Least Privilege

```java
// NetworkPolicy: Deny-all by default
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: deny-all
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress

// Then explicitly allow what's needed
// Generated from code analysis by core_k8s_generator
```

### Defense in Depth

```java
// Multiple validation layers
Authentication      ← OAuth2/JWT
  ↓
Authorization      ← Role-Based (RBAC)
  ↓
Policy             ← Attribute-Based (ABAC)
  ↓
Validation         ← @Valid, custom validators
  ↓
Encryption         ← mTLS (core_mesh)
  ↓
Audit              ← All events logged
```

### Secure by Default

```yaml
securitas:
  policy:
    enabled: true              # Default enabled
    engine: abac              # Safest default
  
  mesh:
    mtls:
      enforcement: STRICT     # Never DISABLED
  
  api-security:
    rateLimit:
      requestsPerSecond: 1000 # Protect by default
```

## 📊 Observability Integration

### Tracing Hierarchy

```
Root Span: HTTP Request
├── Child Span: Authentication
│   ├── OAuth2 Token Validation
│   ├── JWT Signature Verification
│   └── Role Extraction
├── Child Span: Authorization
│   ├── RBAC Check
│   ├── Policy Evaluation
│   └── Compliance Verification
├── Child Span: Rate Limiting
├── Child Span: Replay Detection
├── Child Span: DDoS Detection
├── Child Span: Business Logic
└── Child Span: Response
    └── Audit Event Creation
```

### Metrics Hierarchy

```
securitas_*
├── authentication_*
│   ├── attempts_total
│   ├── successes_total
│   └── failures_total
├── authorization_*
│   ├── denials_total
│   ├── grants_total
│   └── duration_seconds
├── policy_*
│   ├── evaluations_total
│   └── duration_seconds
├── vulnerability_*
│   ├── critical_count
│   ├── high_count
│   └── scans_total
├── compliance_*
│   ├── score
│   └── control_status_*
└── rate_limit_*
    ├── exceeded_total
    └── remaining_quota
```

## 🚀 Deployment Architecture

### Single Node (Development)

```
┌─────────────────────────┐
│   Docker Container      │
├─────────────────────────┤
│                         │
│  Spring Boot App        │
│  + All Frameworks       │
│                         │
│  [Embedded Vault Mode]  │
│  [Local OPA]            │
│  [In-Memory Traces]     │
│                         │
└─────────────────────────┘
```

### Multi-Node K8s (Production)

```
┌──────────────────────────────────────────┐
│         Kubernetes Cluster               │
├──────────────────────────────────────────┤
│                                          │
│  Namespace: securitas                    │
│  ├── Deployment: secure-app (3 replicas)│
│  ├── Service: secure-app                │
│  ├── NetworkPolicy: deny-all default    │
│  ├── PeerAuthentication: STRICT mTLS    │
│  ├── VirtualService: traffic policy     │
│  ├── ServiceMonitor: metrics scraping   │
│  ├── CronJob: secret rotation (daily)   │
│  ├── ConfigMap: compliance policies     │
│  ├── Secret: vault-token, tls-certs     │
│  ├── ServiceAccount: app, vault-auth    │
│  ├── Role: minimal permissions          │
│  ├── RoleBinding: attach roles          │
│  └── HorizontalPodAutoscaler: auto-scale│
│                                          │
│  External Services:                      │
│  ├── Vault (HA cluster)                 │
│  ├── OPA (3 instances + Bundles)        │
│  ├── Jaeger (tracing backend)           │
│  ├── Prometheus (metrics)               │
│  ├── Grafana (dashboards)               │
│  ├── Docker Registry (image scanning)   │
│  └── GitHub Actions (CI/CD)             │
│                                          │
└──────────────────────────────────────────┘
```

## 🔧 Configuration Hierarchy

```
1. Defaults
   ↓ Override with
2. application.yml (environment config)
   ↓ Override with
3. Environment Variables (SECURITAS_*)
   ↓ Override with
4. @ConfigurationProperties
   ↓ Override with
5. Runtime API Calls
```

## 💾 Data Storage

### Vault Storage
```
vault/
  ├── secret/
  │   ├── database/password
  │   ├── api-keys/*
  │   └── certificates/*
  ├── transit/
  │   └── keys/app-encryption
  └── auth/
      └── kubernetes/
          └── app
```

### Kubernetes Secrets
```
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
  namespace: securitas
type: Opaque
data:
  vault-token: <base64>
  tls-cert: <base64>
  tls-key: <base64>
```

### Audit Storage
```
PostgreSQL / MongoDB / Elasticsearch
  ├── audit_events
  │   ├── user_id
  │   ├── action
  │   ├── resource
  │   ├── timestamp
  │   ├── result
  │   └── details
  └── indices: user_id, action, timestamp
```

## 🔐 Cryptography

### TLS/mTLS
```
Service A ──[mTLS 1.3]──→ Service B
  ├── Client Certificate (SPIFFE SVID)
  ├── Server Certificate (SPIFFE SVID)
  ├── Mutual Authentication
  └── Automatic Rotation (90-day interval)
```

### Encryption at Rest
```
Database Encryption:
  └── PostgreSQL: pgcrypto extension
  
Vault Encryption:
  └── Encryption as a Service (Transit Engine)
  
Secrets Encryption:
  └── AES-256-GCM (Vault default)
```

### Secret Signing
```
Cosign Signatures:
  ├── Sign Docker Images
  ├── Sign JAR Artifacts
  └── OIDC Keyless Signing (Sigstore)
```

## 📈 Performance Considerations

### Caching Strategy
- Policy evaluations: 5m TTL, 1000 max entries
- Secret access: 5m TTL with async refresh
- Authentication tokens: 1h TTL (JWT exp)
- Rate limit counters: 1s precision (in-memory)

### Async Operations
- Audit logging: async batch writer
- Metric publishing: async push to Prometheus
- Compliance checks: scheduled background task
- Secret rotation: async safe executor

### Resource Usage
- Memory: ~200MB base + modules
- CPU: <1% at idle, scales with load
- Storage: Depends on audit retention (1M events ~500MB)

## 🎓 Extension Points

### Custom Policy Engine
```java
@Component
public class CustomPolicyEngine implements PolicyEngine {
    @Override
    public PolicyDecision evaluate(String policyId, PolicyContext context) {
        // Custom logic
    }
}
```

### Custom Audit Handler
```java
@Component
public class ElasticsearchAuditHandler implements AuditEventHandler {
    @Override
    public void handle(AuditEvent event) {
        // Send to Elasticsearch
    }
}
```

### Custom Scanner
```java
@Component
public class CustomVulnerabilityScanner implements VulnerabilityScanner {
    @Override
    public ScanResult scan(String target) {
        // Custom scanning logic
    }
}
```

---

**Version:** 2.0.0 | **Last Updated:** 2026-05-07

