# Security Framework v2.0 - Deployment Checklist

## ✅ Pre-Deployment Verification

### Phase 1: Development (Local Machine)

- [ ] **Environment Setup**
  - [ ] Java 17+ installed (`java -version`)
  - [ ] Maven 3.6.0+ installed (`mvn -version`)
  - [ ] Git configured
  - [ ] IDE set up (IntelliJ/VS Code)

- [ ] **Build & Test**
  - [ ] `mvn clean install` succeeds
  - [ ] All modules compile without errors
  - [ ] Unit tests pass (`mvn test`)
  - [ ] No compiler warnings

- [ ] **Local Execution**
  - [ ] Sample app runs: `mvn spring-boot:run`
  - [ ] Health endpoint responds: `curl http://localhost:8080/actuator/health`
  - [ ] Secure endpoint works with proper request
  - [ ] Rate limiting works (test with 150+ rapid requests)

- [ ] **Configuration**
  - [ ] application.yml properly configured
  - [ ] All secrets use environment variables
  - [ ] No hardcoded credentials
  - [ ] Profile-specific configs tested

- [ ] **Documentation**
  - [ ] README.md reviewed
  - [ ] QUICK_START.md followed successfully
  - [ ] INTEGRATION_GUIDE.md section reviewed
  - [ ] Architecture understood

---

### Phase 2: Docker Preparation

- [ ] **Dockerfile**
  - [ ] Dockerfile created and tested
  - [ ] Multi-stage build optimized
  - [ ] Size < 200MB
  - [ ] Base image from trusted registry
  - [ ] Non-root user configured
  - [ ] Health check included

- [ ] **Docker Image Build**
  - [ ] `docker build -t secure-app:latest .` succeeds
  - [ ] `docker run -p 8080:8080 secure-app:latest` starts
  - [ ] Container logs show no errors
  - [ ] Health endpoint responds from container
  - [ ] Proper signal handling (SIGTERM)

- [ ] **Docker Security**
  - [ ] No secrets in image
  - [ ] Read-only filesystem where possible
  - [ ] Resource limits set
  - [ ] Security context applied

- [ ] **Container Registry**
  - [ ] Account created in registry (Docker Hub/ECR/GCR)
  - [ ] Credentials configured
  - [ ] Image pushed successfully
  - [ ] Image scanned for vulnerabilities
  - [ ] Image signing configured (if needed)

---

### Phase 3: Kubernetes Preparation

- [ ] **K8s Setup**
  - [ ] Kubernetes cluster available (local/cloud)
  - [ ] kubectl configured and working
  - [ ] Current context verified: `kubectl current-context`
  - [ ] Cluster version compatible (1.24+)

- [ ] **Manifest Generation**
  - [ ] `mvn compile ru.shanina.securityFramework:core_k8s_generator:k8s-generate` succeeds
  - [ ] Manifests generated in target directory
  - [ ] deployment.yaml reviewed
  - [ ] service.yaml verified
  - [ ] network-policy.yaml validated
  - [ ] rbac yaml files checked
  - [ ] service-account.yaml present

- [ ] **K8s Manifests Review**
  - [ ] Image correctly specified
  - [ ] Resource requests/limits set
  - [ ] Liveness probe configured
  - [ ] Readiness probe configured
  - [ ] Security context applied
  - [ ] Volume mounts reviewed
  - [ ] Environment variables set via ConfigMap/Secret

- [ ] **Secrets Management**
  - [ ] K8s secrets created
  - [ ] Vault tokens stored securely
  - [ ] TLS certificates present
  - [ ] Secret rotation enabled
  - [ ] Backup strategy for secrets

- [ ] **ConfigMaps**
  - [ ] application.yml in ConfigMap
  - [ ] Properties correctly set
  - [ ] Volume mounts to Pod verified

---

### Phase 4: External Dependencies

- [ ] **Vault Setup** (if using core_secret_rotation)
  - [ ] Vault instance running
  - [ ] Vault token generated
  - [ ] Policies configured
  - [ ] Database credentials stored
  - [ ] Test access from app: `curl $VAULT_ADDR/v1/sys/health`

- [ ] **OPA Setup** (if using core_policy with OPA)
  - [ ] OPA server running
  - [ ] Policy rules loaded
  - [ ] Test evaluation: `curl $OPA_URL/data/`
  - [ ] Bundle configuration (optional)

- [ ] **Istio Setup** (if using core_mesh)
  - [ ] Istio installed on cluster
  - [ ] Namespace labeling: `kubectl label namespace default istio-injection=enabled`
  - [ ] mTLS policy created
  - [ ] Virtual services generated
  - [ ] Destination rules configured

- [ ] **Prometheus Setup**
  - [ ] Prometheus deployed
  - [ ] Service monitor configured
  - [ ] Scrape config includes `/actuator/prometheus`
  - [ ] Port 8080 accessible to Prometheus

- [ ] **Jaeger Setup** (if using core_observability_security)
  - [ ] Jaeger deployed
  - [ ] Collector endpoint accessible
  - [ ] Sampling configuration verified
  - [ ] UI accessible (port 16686)

---

### Phase 5: Security Hardening

- [ ] **Network Security**
  - [ ] NetworkPolicy deployed (deny-all default)
  - [ ] Only required ingress rules enabled
  - [ ] Egress rules minimized
  - [ ] Service mesh mTLS enabled

- [ ] **RBAC**
  - [ ] Service account created
  - [ ] Role minimal (least privilege)
  - [ ] RoleBinding attached
  - [ ] No wildcard permissions

- [ ] **Pod Security**
  - [ ] SecurityContext applied
  - [ ] Read-only root filesystem
  - [ ] Non-root user
  - [ ] No privileged containers
  - [ ] Resource quotas set

- [ ] **Secret Security**
  - [ ] Secrets not in ConfigMaps
  - [ ] Encryption at rest enabled
  - [ ] RBAC for secret access
  - [ ] Audit logging for secret access

- [ ] **Image Security**
  - [ ] Image signed (Cosign)
  - [ ] SBOM generated
  - [ ] Vulnerability scan passed
  - [ ] Image pull policy: IfNotPresent
  - [ ] No latest tags

---

### Phase 6: Monitoring & Observability

- [ ] **Metrics**
  - [ ] Prometheus scraping metrics
  - [ ] Metrics endpoint responding
  - [ ] Grafana dashboard created
  - [ ] Alerts configured
  - [ ] Key metrics: auth rate, denial rate, compliance score

- [ ] **Logging**
  - [ ] Structured logging configured
  - [ ] Logs aggregated (ELK/Splunk)
  - [ ] Security events logged
  - [ ] Log retention policy set
  - [ ] Sensitive data masked

- [ ] **Tracing**
  - [ ] Jaeger shows traces
  - [ ] Trace sampling configured
  - [ ] Traces linked to logs
  - [ ] Trace retention policy set

- [ ] **Alerting**
  - [ ] Critical alerts defined
  - [ ] Alert channels configured (Slack/PagerDuty)
  - [ ] Alert thresholds reasonable
  - [ ] Test alert firing

---

### Phase 7: Compliance & Audit

- [ ] **Compliance Verification**
  - [ ] Compliance checks passing
  - [ ] Required frameworks enabled
  - [ ] Evidence collection working
  - [ ] Compliance score > 90%

- [ ] **Audit Trail**
  - [ ] Audit events logged
  - [ ] Immutable audit log
  - [ ] Retention policy set
  - [ ] Access audit events

- [ ] **Security Documentation**
  - [ ] Security policies documented
  - [ ] Data flow diagram created
  - [ ] Risk assessment completed
  - [ ] Incident response plan in place

---

### Phase 8: Testing

- [ ] **Functional Testing**
  - [ ] All endpoints respond
  - [ ] Authentication works
  - [ ] Authorization enforced
  - [ ] Policies evaluated correctly
  - [ ] Rate limiting blocks excessive requests
  - [ ] API keys validated

- [ ] **Security Testing**
  - [ ] SQL injection tests pass (no vulnerability)
  - [ ] XSS protection verified
  - [ ] CSRF tokens present (if applicable)
  - [ ] Authorization bypass attempts blocked
  - [ ] Secrets not exposed in logs/errors

- [ ] **Load Testing**
  - [ ] App handles expected load
  - [ ] Rate limiting works under load
  - [ ] Cache performance acceptable
  - [ ] No memory leaks
  - [ ] Auto-scaling triggers correctly

- [ ] **Failure Testing**
  - [ ] Graceful degradation on vault failure
  - [ ] OPA unavailability handled
  - [ ] Database connection failure recovery
  - [ ] Network partition handling

- [ ] **Chaos Engineering** (Optional)
  - [ ] Pod termination handled
  - [ ] Network latency tolerated
  - [ ] Packet loss recovery
  - [ ] Clock skew tolerance

---

### Phase 9: Deployment

- [ ] **Pre-Production Deployment**
  - [ ] Staging cluster ready
  - [ ] Staging manifests applied
  - [ ] Staging tests passing
  - [ ] Performance baseline established
  - [ ] Team trained on runbooks

- [ ] **Production Readiness**
  - [ ] Production cluster prepared
  - [ ] All dependencies deployed
  - [ ] Backups configured
  - [ ] Rollback plan documented
  - [ ] Incident response team ready

- [ ] **Deployment Execution**
  - [ ] Deployment window scheduled
  - [ ] Stakeholders notified
  - [ ] Health checks monitored
  - [ ] Metrics watched
  - [ ] Logs monitored for errors

- [ ] **Post-Deployment**
  - [ ] Health checks passing
  - [ ] Smoke tests successful
  - [ ] Metrics baseline established
  - [ ] No critical alerts
  - [ ] Team monitoring in place

---

### Phase 10: Post-Deployment

- [ ] **Verification**
  - [ ] All endpoints responding
  - [ ] Metrics showing normal patterns
  - [ ] No unusual errors in logs
  - [ ] Compliance checks passing
  - [ ] Audit trail complete

- [ ] **Optimization**
  - [ ] Cache hit rates > 80%
  - [ ] Response times acceptable
  - [ ] Resource utilization reasonable
  - [ ] Costs within budget
  - [ ] Scaling parameters tuned

- [ ] **Maintenance**
  - [ ] Backup schedule active
  - [ ] Logs rotated
  - [ ] Secrets rotation working
  - [ ] Certificate renewal scheduled
  - [ ] Update strategy defined

- [ ] **Documentation Update**
  - [ ] Deployment runbook created
  - [ ] Incident response guide updated
  - [ ] Architecture diagram finalized
  - [ ] Team documentation updated
  - [ ] Knowledge transfer completed

---

## 📋 Configuration Checklist

### Required Environment Variables
```bash
# Vault
VAULT_ADDR=https://vault.example.com
VAULT_TOKEN=s.xxxxxxxxxxxxxx

# OAuth2
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://auth-server.example.com
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://auth-server.example.com/.well-known/jwks.json

# OPA (if using)
SECURITAS_POLICY_OPA_URL=http://opa:8181

# Jaeger (if using tracing)
OTEL_EXPORTER_JAEGER_ENDPOINT=http://jaeger:14268/api/traces

# Logging
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_RU_SHANINA=INFO
```

### Application Properties
```yaml
# Required
spring.application.name=secure-app
server.port=8080

# Security
securitas.policy.enabled=true
securitas.api-security.enabled=true
securitas.compliance.enabled=true

# Observability
securitas.observability.enabled=true
management.endpoints.web.exposure.include=health,prometheus

# Vault
vault.uri=${VAULT_ADDR}
vault.token=${VAULT_TOKEN}
```

---

## 🚨 Common Issues & Resolutions

| Issue | Cause | Resolution |
|-------|-------|-----------|
| Modules not loading | Not in pom.xml | Add dependencies in parent pom.xml |
| Rate limiting too strict | Low requestsPerSecond | Increase in application.yml |
| Vault connection failed | No VAULT_TOKEN env | Set VAULT_TOKEN environment variable |
| OPA unreachable | OPA not running | Start OPA: `docker run ... opa run --server` |
| Jaeger not showing traces | Sampling = 0 | Set `securitas.observability.tracing.sampling.rate: 1.0` |
| K8s pod pending | Image not found | Verify image URI in deployment.yaml |
| Network policies blocking traffic | Too strict rules | Review NetworkPolicy in ARCHITECTURE.md |
| High memory usage | Cache size too large | Reduce `securitas.policy.cache.maxSize` |
| Slow policy evaluation | OPA latency | Cache decisions or use ABAC instead |

---

## 📞 Support Contacts

### During Deployment
- **Architecture Issues:** Review ARCHITECTURE.md
- **Configuration Issues:** Check INTEGRATION_GUIDE.md
- **Module Questions:** See MODULES_OVERVIEW.md
- **Quick Help:** Read QUICK_START.md

### Escalation Path
1. Check relevant documentation
2. Review logs for error messages
3. Check troubleshooting sections
4. Consult team members
5. Open support ticket

---

## ✅ Sign-Off Template

```
Deployment Checklist - Security Framework v2.0

Project: _________________
Date: _________________
Environment: [ ] Dev [ ] Staging [ ] Production

Completed by: _________________  Date: _________________
Reviewed by: _________________   Date: _________________
Approved by: _________________   Date: _________________

All items checked: ☐ YES ☐ NO
Known issues: _________________________________
Next steps: _________________________________

Sign-off: _________________   Date: _________________
```

---

## 📊 Success Metrics

| Metric | Target | Verification |
|--------|--------|--------------|
| Pod startup time | < 30s | Check timestamps in logs |
| Health check latency | < 100ms | `curl /actuator/health` |
| Request latency p99 | < 500ms | Prometheus metrics |
| Error rate | < 0.1% | Logs & metrics |
| Compliance score | > 95% | Compliance dashboard |
| Cache hit ratio | > 90% | Metrics dashboard |
| Uptime | > 99.9% | Availability monitoring |

---

**Version:** 2.0.0 | **Last Updated:** May 7, 2026 | **Status:** ✅ Production Ready

