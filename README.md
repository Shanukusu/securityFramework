# Security Framework

A **production-grade, compile-time safe security framework** for Java applications.

## 🎯 Key Features

### 1. Type-Safe RBAC
- Enum-based roles instead of strings
- `@Authorize(Role.ADMIN)` compile-time checked
- Policy engine with `and()`/`or()` combinators

### 2. Production-Grade Vault Integration
- Caffeine-based caching with automatic refresh
- Cache invalidation support
- Async-ready architecture

### 3. Decoupled Audit System
- Works with async, messaging, batch
- `SecurityContextProvider` & `RequestContextProvider` abstractions
- Plugin-based custom handlers

### 4. K8s Security Analyzer (Killer Feature 🚀)
- **Automatic dependency graph analysis**
- Detects HTTP/Kafka/DB/gRPC calls
- Generates NetworkPolicy + RBAC automatically
- Security compiler for safe deployments

### 5. OAuth2 Support
- JWT authentication ready
- Resource server configuration
- Role extraction from tokens

## 📦 Modules

- **core_iam**: OAuth2, JWT, rate limiting, RBAC
- **core_audit**: Persistent audit with context providers
- **core_vault**: Production-grade secret management
- **core_k8s_generator**: Security-aware K8s manifest generation
- **sample_app**: Complete demo

## 🚀 Quick Start

### Add Dependency
```xml
<dependency>
    <groupId>ru.shanina.securityFramework</groupId>
    <artifactId>core_iam</artifactId>
</dependency>
```

### Use Typed Authorization
```java
@Authorize(Role.ADMIN)
@GetMapping("/admin")
public String adminOnly() { ... }
```

### Enable Audit
```java
@Audit(event = "USER_LOGIN", level = "INFO")
@PostMapping("/login")
public Token login(@RequestBody LoginRequest req) { ... }
```

## 🔒 Security Analysis for K8s

```bash
mvn compile ru.shanina.securityFramework:core_k8s_generator:k8s-generate
```

Generates:
- `deployment.yaml` - with security contexts
- `network-policy.yaml` - based on code analysis
- `service-account.yaml` - with minimal RBAC
- `role.yaml` + `role-binding.yaml` - auto-configured

## 📋 Configuration

```yaml
securitas:
  iam:
    enabled: true
  policies:
    rules:
      - path: /admin/**
        require:
          role: ADMIN
```

## 🧪 Testing

```bash
mvn clean test
```

## 📚 Architecture

- **Compile-time safe**: Enums, not strings
- **Async-ready**: Context providers, not ThreadLocal
- **Plugin-based**: Custom handlers for audit
- **Production**: Caffeine caching, proper error handling

## 🎓 Advanced: Custom Audit Handler

```java
@Component
public class KafkaAuditHandler implements AuditEventHandler {
    @Override
    public void handle(AuditEvent event) {
        kafkaTemplate.send("audit-events", event);
    }
}
```

## 💡 Why This Framework

✅ Type-safe security code  
✅ Kubernetes-native  
✅ OAuth2 ready  
✅ Production-grade caching  
✅ Automatic security analysis  
✅ Extensible architecture
