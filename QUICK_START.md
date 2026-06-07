# Security Framework v2.0 - Quick Start Guide

## ⚡ 5-Minute Setup

### 1. Clone & Build
```bash
git clone <repo>
cd securityFramework
mvn clean install -DskipTests
```

### 2. Run Sample App
```bash
cd sample_app
mvn spring-boot:run
```

### 3. Test Secure Endpoint
```bash
curl -X POST http://localhost:8080/api/v1/secure/payment \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "****1234",
    "cardholderName": "John Doe",
    "amount": 100.00,
    "currency": "USD",
    "merchantId": "MERCHANT123"
  }'
```

**Response:**
```json
{
  "transactionId": "TXN-1714846234567",
  "status": "SUCCESS",
  "amount": 100.0,
  "message": "Payment processed securely"
}
```

---

## 🔧 Configuration (5 minutes)

Create `application.yml`:

```yaml
spring:
  application:
    name: my-secure-app

securitas:
  # Enable the modules you need
  policy:
    enabled: true
    engine: abac  # or opa
  
  api-security:
    enabled: true
    rateLimit:
      requestsPerSecond: 1000
  
  compliance:
    enabled: true
    frameworks: [PCI_DSS, ISO_27001]
  
  observability:
    enabled: true
    tracing:
      backend: jaeger
      jaeger:
        endpoint: http://localhost:14268/api/traces
```

---

## 🔐 Secure Your Endpoint (1 minute)

### Before (Unprotected)
```java
@PostMapping("/payment")
public PaymentResponse process(@RequestBody PaymentRequest req) {
    return paymentService.process(req);
}
```

### After (Multi-layer Protected)
```java
@RateLimited(requestsPerSecond = 100)
@Policy("payment-policy")
@Scanned(severity = "HIGH")
@Compliant(frameworks = ComplianceFramework.PCI_DSS)
@PostMapping("/payment")
public PaymentResponse process(@RequestBody PaymentRequest req) {
    return paymentService.process(req);
}
```

**Automatically Enforced:**
- ✅ Rate limiting (100 req/sec)
- ✅ Policy evaluation (ABAC)
- ✅ Vulnerability gate (blocks HIGH severity)
- ✅ Compliance verification (PCI DSS)
- ✅ Audit logging
- ✅ Distributed tracing
- ✅ Metrics collection

---

## 📊 View Metrics & Traces

### Prometheus Metrics
```bash
curl http://localhost:8080/actuator/prometheus | grep securitas_
```

### Key Metrics
```
securitas_authentication_attempts_total
securitas_authorization_denials_total
securitas_policy_evaluation_duration_seconds
securitas_rate_limit_exceeded_total
securitas_compliance_score
securitas_vulnerability_critical_count
```

---

## 🎯 Multi-Module Configuration

### Option 1: Core Only (Fastest)
```yaml
securitas:
  policy:
    enabled: true
  api-security:
    enabled: true
```

### Option 2: Standard (Recommended)
```yaml
securitas:
  policy:
    enabled: true
  mesh:
    enabled: false  # Set to true in K8s
  scanner:
    enabled: true
  compliance:
    enabled: true
  api-security:
    enabled: true
  secret-rotation:
    enabled: true
  observability:
    enabled: true
```

### Option 3: Full (Enterprise)
```yaml
securitas:
  policy:
    enabled: true
    engine: opa  # Requires OPA server
  mesh:
    enabled: true
    provider: istio
  scanner:
    enabled: true
  compliance:
    enabled: true
  api-security:
    enabled: true
  secret-rotation:
    enabled: true
  observability:
    enabled: true
  supply-chain:
    enabled: true
```

---

## 🚀 Deploy to Kubernetes (10 minutes)

### 1. Build Docker Image
```bash
mvn clean package -DskipTests
docker build -t my-secure-app:latest .
docker push my-registry/my-secure-app:latest
```

### 2. Generate K8s Manifests
```bash
mvn compile \
  ru.shanina.securityFramework:core_k8s_generator:k8s-generate
```

**Generated Files:**
- `k8s/deployment.yaml` - Pod with security context
- `k8s/service.yaml` - Service exposure
- `k8s/network-policy.yaml` - Auto-generated from code analysis
- `k8s/role.yaml` - Minimal RBAC
- `k8s/role-binding.yaml` - Role bindings
- `k8s/service-account.yaml` - Identity

### 3. Deploy
```bash
kubectl apply -f k8s/
kubectl get pods -w
kubectl logs -f deployment/my-secure-app
```

### 4. Enable Mesh (Optional)
```bash
# Install Istio
istioctl install --set profile=demo -y

# Deploy additional manifests
kubectl apply -f k8s/istio-manifests/peer-authentication.yaml
kubectl apply -f k8s/istio-manifests/virtual-service.yaml
```

---

## 🔍 Verify Security

### Check Rate Limiting
```bash
# Should succeed
curl http://localhost:8080/api/v1/secure/health

# Should be rate-limited after 100 requests
for i in {1..150}; do
  curl http://localhost:8080/api/v1/secure/payment
done
```

### Check Policy Enforcement
```bash
# Will trigger policy evaluation
curl -X POST http://localhost:8080/api/v1/secure/payment \
  -H "Content-Type: application/json" \
  -d '{"cardNumber":"****1234","amount":100}'
```

### Check Compliance
```bash
curl http://localhost:8080/actuator/prometheus | grep compliance_score
# Output: securitas_compliance_score{...} 95.0
```

### Check Tracing
```bash
# Open Jaeger UI
open http://localhost:16686

# Search for traces with tag "securitas"
```

---

## 📚 Common Patterns

### Pattern 1: Protect API with Multiple Layers
```java
@RateLimited(requestsPerSecond = 50)
@ApiKeyRequired(scope = "api:write")
@Policy("user-policy")
@Authorize(Role.USER)
@PostMapping("/resource")
public Resource createResource(@RequestBody ResourceRequest req) { }
```

### Pattern 2: Gradual Security Adoption
```java
// Phase 1: Just basic RBAC
@Authorize(Role.ADMIN)
public void operation1() { }

// Phase 2: Add policies
@Policy("admin-policy")
@Authorize(Role.ADMIN)
public void operation2() { }

// Phase 3: Add compliance
@Compliant(frameworks = ComplianceFramework.ISO_27001)
@Policy("admin-policy")
@Authorize(Role.ADMIN)
public void operation3() { }
```

### Pattern 3: Store & Rotate Secrets
```java
@Component
public class DatabaseConfig {
    
    @RotatedSecret(rotationIntervalDays = 30)
    @Bean
    public DataSource dataSource(
        @Value("${db.password}") String dbPassword
    ) {
        return createDataSource(dbPassword);
    }
    
    @RotationCallback
    public void onSecretRotated() {
        log.info("Database password rotated, reconnecting...");
    }
}
```

---

## 🐛 Troubleshooting

### Module Not Loading
```yaml
# Check if enabled
securitas:
  policy:
    enabled: true  # Must be true

# Check logs
mvn spring-boot:run -X | grep "Policy\|policy"
```

### Rate Limiting Not Working
```yaml
# Check configuration
securitas:
  api-security:
    enabled: true  # Must be enabled
    rateLimit:
      requestsPerSecond: 100
      burstSize: 200
```

### Tracing Not Showing
```yaml
# Check Jaeger is running
docker run -d --name jaeger \
  -p 6831:6831/udp \
  -p 16686:16686 \
  jaegertracing/all-in-one

# Check configuration
securitas:
  observability:
    enabled: true
    tracing:
      backend: jaeger
      jaeger:
        endpoint: http://localhost:14268/api/traces
```

---

## 📖 Next Steps

1. **Read INTEGRATION_GUIDE.md** - Complete integration guide
2. **Read ARCHITECTURE.md** - Technical architecture
3. **Customize application.yml** - For your environment
4. **Implement business policies** - Custom policy rules
5. **Deploy to Kubernetes** - Production deployment

---

## 🎯 Success Checklist

- [ ] App runs with `mvn spring-boot:run`
- [ ] Health endpoint returns 200
- [ ] Payment endpoint requires proper request
- [ ] Rate limiting works (tested with curl loop)
- [ ] Metrics visible in /actuator/prometheus
- [ ] Logs show security events
- [ ] Jaeger shows traces (if enabled)
- [ ] Docker image builds successfully
- [ ] K8s manifests generated
- [ ] Deployed to local K8s cluster

---

## 💡 Pro Tips

1. **Start with ABAC** - No external dependencies needed
2. **Enable logging** - See all security decisions
3. **Monitor metrics** - Use Prometheus scraping
4. **Test in dev first** - Use dev profile in application.yml
5. **Use env variables** - For sensitive config
6. **Check compliance** - Run compliance reports regularly
7. **Rotate secrets** - Set up automatic rotation early
8. **Monitor dashboards** - Set up Grafana alerts

---

## 🚨 Common Issues

| Issue | Solution |
|-------|----------|
| `ClassNotFoundException` | Run `mvn clean install` |
| Module not enabled | Set `securitas.*.enabled: true` |
| OPA not found | Set engine to `abac` instead |
| Rate limiting too strict | Increase `requestsPerSecond` |
| Jaeger not showing traces | Start Jaeger: `docker run ... jaegertracing/all-in-one` |
| K8s manifests not generated | Run `mvn compile` before generator |

---

## 📞 Support

- **Documentation:** See INTEGRATION_GUIDE.md
- **Examples:** Check SecureApiController.java
- **Architecture:** See ARCHITECTURE.md
- **Logs:** Check application logs for details

---

**Version:** 2.0.0 | **Last Updated:** May 7, 2026

🎉 **You're all set! Start building secure applications!**

