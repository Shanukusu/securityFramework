# Security Framework v2.0 - Полная интеграция 8 новых модулей

## 📚 Введение

Это полное руководство по внедрению расширенного Security Framework с 8 новыми модулями безопасности.

## 🎯 Архитектурный обзор

```
┌─────────────────────────────────────────────────────────┐
│         Security Framework v2.0 Architecture            │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌─────────────────────────────────────────────────┐   │
│  │        Application Layer (Spring Boot)          │   │
│  └─────────────────────────────────────────────────┘   │
│                        ↓                                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │          Security Framework Layer                │  │
│  ├──────────────────────────────────────────────────┤  │
│  │                                                   │  │
│  │  ┌────────────┐  ┌────────────┐  ┌──────────┐  │  │
│  │  │Policy      │  │API         │  │Service   │  │  │
│  │  │Engine      │  │Security    │  │Mesh      │  │  │
│  │  │(OPA/ABAC)  │  │(RateLimit) │  │(mTLS)    │  │  │
│  │  └────────────┘  └────────────┘  └──────────┘  │  │
│  │                                                   │  │
│  │  ┌────────────┐  ┌────────────┐  ┌──────────┐  │  │
│  │  │Security    │  │Compliance  │  │Secret    │  │  │
│  │  │Scanner     │  │(PCI/GOST)  │  │Rotation  │  │  │
│  │  │(Trivy)     │  │            │  │          │  │  │
│  │  └────────────┘  └────────────┘  └──────────┘  │  │
│  │                                                   │  │
│  │  ┌────────────┐  ┌────────────┐  ┌──────────┐  │  │
│  │  │Observ.     │  │Supply      │  │Core IAM  │  │  │
│  │  │Security    │  │Chain       │  │          │  │  │
│  │  │(OTel/      │  │(SBOM/      │  │(OAuth2)  │  │  │
│  │  │Jaeger)     │  │Cosign)     │  │          │  │  │
│  │  └────────────┘  └────────────┘  └──────────┘  │  │
│  │                                                   │  │
│  └──────────────────────────────────────────────────┘  │
│                        ↓                                │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Core Modules (IAM, Audit, Vault, K8s)          │  │
│  └──────────────────────────────────────────────────┘  │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 📦 Модули

### 1. Policy Engine Module (core_policy)
**Назначение:** Централизованная политика авторизации через OPA/ABAC

```yaml
# application.yml
securitas:
  policy:
    enabled: true
    engine: abac  # or opa
    opa:
      url: http://localhost:8181
      timeout: 5000
    cache:
      ttlMinutes: 5
      maxSize: 1000
```

**Использование:**
```java
@Policy("employee-access")
@Authorize(Role.EMPLOYEE)
@PostMapping("/data")
public DataResponse getData() { }
```

---

### 2. Service Mesh Security Module (core_mesh)
**Назначение:** Istio/Linkerd интеграция с mTLS и управлением трафиком

```yaml
securitas:
  mesh:
    enabled: true
    provider: istio  # or linkerd
    mtls:
      enforcement: STRICT  # STRICT, PERMISSIVE, DISABLED
      rotationIntervalDays: 90
```

**Использование:**
```java
@MeshSecured(mtlsRequired = true)
@Component
public class PaymentService { }
```

**Генерируемые K8s ресурсы:**
- `PeerAuthentication` - mTLS enforcement
- `DestinationRule` - TLS configuration
- `VirtualService` - traffic management

---

### 3. Security Scanner Module (core_scanner)
**Назначение:** Trivy, Dependency-Check, Grype интеграция для поиска CVE

```yaml
securitas:
  scanner:
    enabled: true
    trivy:
      enabled: true
    dependencyCheck:
      enabled: true
```

**Использование:**
```java
@Scanned(severity = "HIGH")
@Service
public class SensitiveService { }
```

**Генерируемые артефакты:**
- `sbom.cyclonedx.json` - Bill of Materials
- Vulnerability reports
- HTML/JSON compliance reports

---

### 4. Compliance Module (core_compliance)
**Назначение:** PCI DSS, ГОСТ 57580, ISO 27001, OWASP ASVS проверки

```yaml
securitas:
  compliance:
    enabled: true
    frameworks:
      - PCI_DSS
      - GOST_57580
      - ISO_27001
      - OWASP_ASVS
```

**Использование:**
```java
@Compliant(frameworks = {ComplianceFramework.PCI_DSS, ComplianceFramework.ISO_27001})
@Service
public class PaymentService { }
```

**Метрики:**
- `compliance_score` - Gauge metric
- `control_status` - каждого контроля
- Audit trail для regulators

---

### 5. API Security Module (core_api_security)
**Назначение:** Rate limiting, API Keys, Anti-Replay, DDoS protection

```yaml
securitas:
  api-security:
    enabled: true
    rateLimit:
      requestsPerSecond: 1000
      burstSize: 2000
    ddos:
      enabled: true
      threshold: 5000
    apiKey:
      enabled: true
```

**Использование:**
```java
@RateLimited(requestsPerSecond = 100, burstSize = 200)
@ApiKeyRequired(scope = "payments:create")
@AntiReplay
@PostMapping("/api/v1/payments")
public PaymentResponse createPayment(@RequestBody PaymentRequest req) { }
```

---

### 6. Secret Rotation Module (core_secret_rotation)
**Назначение:** Автоматическая ротация секретов, сертификатов и API ключей

```yaml
securitas:
  secret-rotation:
    enabled: true
    interval: 30d
    policy: graceful  # safe, blue-green, canary
```

**Использование:**
```java
@RotatedSecret(rotationIntervalDays = 30)
@Component
public class DatabaseConnector {
    @Value("${db.password}")
    private String dbPassword;
    
    @RotationCallback
    public void reconnectAfterRotation() {
        // Re-initialize connections
    }
}
```

**Генерируемые K8s ресурсы:**
- `CronJob` для scheduler rotation
- `ServiceAccount` для Vault access
- Health checks для validation

---

### 7. Observability Security Module (core_observability_security)
**Назначение:** OpenTelemetry трейсинг для всех security events

```yaml
securitas:
  observability:
    enabled: true
    tracing:
      backend: jaeger  # or tempo
      jaeger:
        endpoint: http://localhost:14268/api/traces
      sampling:
        rate: 1.0  # 100% для security events
    metrics:
      enabled: true
      prometheus:
        endpoint: /actuator/prometheus
```

**Использование:**
```java
@Traced("authentication.attempt")
public void authenticate(String username) { }
```

**Генерируемые метрики:**
- `auth_success_rate` - успешные аутентификации
- `authz_denial_rate` - отклоненные запросы
- `policy_evaluation_duration_ms` - время вычисления политик
- `vuln_critical_count` - критические уязвимости
- `compliance_score` - итоговый compliance score

**Grafana Dashboard автоматически генерируется с:**
- Real-time security events
- Authentication/Authorization metrics
- Vulnerability trends
- Compliance status

---

### 8. Supply Chain Security Module (core_supply_chain)
**Назначение:** SBOM generation, artifact signing, SLSA attestation

```yaml
securitas:
  supply-chain:
    enabled: true
    sbom:
      enabled: true
      format: cyclonedx
    signing:
      enabled: true
      provider: sigstore  # or cosign
    slsa:
      minimumLevel: 3
```

**Использование:**
```java
@SupplyChainSecured(
    requireSlsaLevel = 3,
    verifySignatures = true,
    checkTransitiveCves = true
)
@SpringBootApplication
public class SecureApplication { }
```

**Генерируемые артефакты:**
- `sbom.cyclonedx.json` - Software Bill of Materials
- `image.sig` - Cosign signature
- `attestation.slsa.json` - SLSA v1.0 provenance
- `supply-chain-risk-report.html` - Risk assessment

---

## 🔧 Конфигурация (application.yml)

```yaml
spring:
  application:
    name: secure-app
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth-server.example.com

securitas:
  # Policy Engine
  policy:
    enabled: true
    engine: abac
    opa:
      url: http://opa:8181
      timeout: 5000

  # Service Mesh
  mesh:
    enabled: true
    provider: istio
    mtls:
      enforcement: STRICT
      rotationIntervalDays: 90

  # Security Scanner
  scanner:
    enabled: true
    trivy:
      enabled: true
    dependencyCheck:
      enabled: true

  # Compliance
  compliance:
    enabled: true
    frameworks:
      - PCI_DSS
      - GOST_57580
      - ISO_27001
      - OWASP_ASVS

  # API Security
  api-security:
    enabled: true
    rateLimit:
      requestsPerSecond: 1000
      burstSize: 2000
    ddos:
      enabled: true
      threshold: 5000

  # Secret Rotation
  secret-rotation:
    enabled: true
    interval: 30d
    policy: graceful

  # Observability
  observability:
    enabled: true
    tracing:
      backend: jaeger
      jaeger:
        endpoint: http://jaeger:14268/api/traces
      sampling:
        rate: 1.0
    metrics:
      enabled: true

  # Supply Chain
  supply-chain:
    enabled: true
    sbom:
      enabled: true
      format: cyclonedx
    signing:
      enabled: true
      provider: sigstore
    slsa:
      minimumLevel: 3

management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info
  metrics:
    export:
      prometheus:
        enabled: true
```

---

## 🚀 Установка и использование

### 1. Добавить зависимость в pom.xml

```xml
<!-- Все модули в одном -->
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_policy</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_mesh</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_scanner</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_compliance</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_api_security</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_secret_rotation</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_observability_security</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_supply_chain</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Сборка

```bash
mvn clean install
```

### 3. Запуск с Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: secure
    depends_on:
      - vault
      - opa
      - jaeger
      - prometheus

  vault:
    image: vault:latest
    ports:
      - "8200:8200"
    environment:
      VAULT_DEV_ROOT_TOKEN_ID: mytoken

  opa:
    image: openpolicyagent/opa:latest
    ports:
      - "8181:8181"
    command: run --server

  jaeger:
    image: jaegertracing/all-in-one
    ports:
      - "14268:14268"
      - "16686:16686"

  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
```

---

## 💡 Примеры использования

### Пример 1: Защищённый API endpoint с многоуровневой защитой

```java
@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    
    @RateLimited(requestsPerSecond = 50)
    @ApiKeyRequired(scope = "payments:create")
    @AntiReplay
    @Policy("payment-policy")
    @Authorize(Role.MERCHANT)
    @Scanned(severity = "CRITICAL")
    @Compliant(frameworks = {ComplianceFramework.PCI_DSS})
    @Traced("payment.create")
    @PostMapping("/payments")
    public PaymentResponse createPayment(@RequestBody PaymentRequest request) {
        // 1. API Key validation
        // 2. Rate limiting check
        // 3. Replay attack detection
        // 4. Policy evaluation (ABAC/OPA)
        // 5. Role-based authorization
        // 6. Vulnerability scanning gate
        // 7. Compliance check
        // 8. Distributed tracing
        return paymentService.process(request);
    }
}
```

**Трейс в Jaeger будет включать:**
- Authentication attempt
- Authorization decision
- Policy evaluation result
- API key validation
- Rate limit status
- Execution time

---

### Пример 2: Автоматическая ротация секретов

```java
@Configuration
public class DatabaseConfig {
    
    @RotatedSecret(rotationIntervalDays = 30)
    @Bean
    public DataSource dataSource(
        @Value("${db.password}") String dbPassword
    ) {
        // На каждой ротации Spring переинициализирует bean
        return createDataSource(dbPassword);
    }
    
    @Component
    public class DatabaseConnector {
        @RotatedSecret(rotationIntervalDays = 30, type = "certificate")
        @Value("${db.ssl.cert}")
        private String sslCert;
        
        @RotationCallback
        public void onSecretRotated() {
            log.info("Secret rotated, reconnecting to database");
            // Close old connections, create new ones
        }
    }
}
```

**Автоматически:**
- K8s CronJob триггирует ротацию каждый день
- Новый secret генерируется в Vault
- Spring ConfigServer обновляет конфигурацию
- Health checks проверяют доступность
- Если failure - откатывает на старый secret
- Audit логирует все события

---

### Пример 3: Полностью соответствующий PCI DSS сервис

```java
@RestController
@RequestMapping("/api/v1/payments")
@Compliant(frameworks = ComplianceFramework.PCI_DSS)
@Slf4j
public class PciCompliantPaymentService {
    
    private final ComplianceEngine complianceEngine;
    private final AuditService auditService;
    
    @RateLimited(requestsPerSecond = 100)
    @ApiKeyRequired(scope = "pci:write")
    @AntiReplay
    @Scanned(severity = "HIGH")
    @Policy("pci-payment-policy")
    @PostMapping
    @Audit(event = "PAYMENT_CREATED", level = "CRITICAL")
    public PaymentResponse createPayment(
        @RequestBody @Valid PaymentRequest request
    ) {
        // Compliance check перед processing
        List<String> violations = complianceEngine.validateCompliance(
            ComplianceFramework.PCI_DSS
        );
        
        if (!violations.isEmpty()) {
            log.error("PCI DSS violations detected: {}", violations);
            throw new ComplianceException("PCI DSS compliance failed");
        }
        
        // Обработка с полным логированием
        auditService.logPaymentAttempt(request);
        PaymentResponse response = processPayment(request);
        auditService.logPaymentSuccess(response);
        
        return response;
    }
    
    @Scheduled(cron = "0 0 * * * *")  // ежечасно
    public void verifyCompliance() {
        complianceEngine.generateReport(ComplianceFramework.PCI_DSS);
        // Отчёт автоматически отправляется auditors
    }
}
```

---

## 📊 Monitoring & Observability

### Prometheus метрики

```
# Security Framework метрики
securitas_authentication_attempts_total
securitas_authentication_failures_total
securitas_authorization_denials_total
securitas_policy_evaluation_duration_seconds
securitas_vulnerability_critical_count
securitas_vulnerability_high_count
securitas_compliance_score
securitas_secret_rotation_success_total
securitas_secret_rotation_failures_total
securitas_rate_limit_exceeded_total
securitas_api_key_invalid_total
```

### Grafana Dashboard

Автоматически генерируется dashboard с:
- Real-time authentication/authorization metrics
- Vulnerability trends
- Compliance score progression
- Secret rotation history
- API rate limit usage
- DDoS detection alerts

---

## 🔒 Security Best Practices

1. **Никогда не логируйте** secrets, passwords, API keys
   - Используйте `@SensitiveDataLogging` для masking
   
2. **Всегда используйте mTLS** между микросервисами
   - core_mesh автоматически генерирует Istio policies

3. **Проверяйте CVE регулярно**
   - core_scanner запускается на каждой build

4. **Ротируйте secrets часто**
   - core_secret_rotation делает это автоматически

5. **Логируйте все security events**
   - core_audit и core_observability_security tracят всё

6. **Соблюдайте compliance frameworks**
   - core_compliance проверяет контролы и генерирует отчёты

---

## 🚀 Deployment на K8s

```bash
# 1. Build Docker image
mvn clean package -DskipTests
docker build -t secure-app:latest .

# 2. Deploy with framework
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/vault-config.yaml
kubectl apply -f k8s/opa-config.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/network-policy.yaml  # сгенерирована core_k8s_generator
kubectl apply -f k8s/peer-authentication.yaml  # сгенерирована core_mesh
kubectl apply -f k8s/secret-rotation-cronjob.yaml  # сгенерирована core_secret_rotation

# 3. Проверить status
kubectl get pods -n securitas
kubectl logs -f deployment/secure-app -n securitas
```

---

## 📝 Testing

```bash
# Unit tests
mvn test

# Integration tests (требует Docker)
mvn -Dgroups=IntegrationTest verify

# Security scanning
mvn clean compile org.owasp:dependency-check-maven:check

# Generate SBOM
mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregate

# Generate compliance report
mvn securitas:compliance-report
```

---

## 📚 Дополнительные ресурсы

- [OPA Documentation](https://www.openpolicyagent.org/)
- [Istio Security](https://istio.io/latest/docs/concepts/security/)
- [Trivy GitHub](https://github.com/aquasecurity/trivy)
- [CycloneDX SBOM](https://cyclonedx.org/)
- [SLSA Framework](https://slsa.dev/)
- [OpenTelemetry](https://opentelemetry.io/)

---

## 🎯 Roadmap

- [ ] gRPC support in K8s analyzer
- [ ] ML-based anomaly detection
- [ ] Hardware security module (HSM) integration
- [ ] Multi-cloud support (AWS IAM, Azure AD)
- [ ] GraphQL security scanning
- [ ] Container image scanning in registry
- [ ] Zero-trust networking policies
- [ ] Quantum-safe cryptography

---

**Версия:** 2.0.0 | **Дата:** 2026-05-07 | **Статус:** Production Ready ✅

