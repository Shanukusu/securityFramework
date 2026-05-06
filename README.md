# Security Framework

A modular Spring Boot security framework for Java applications.

## Modules

- **core_iam**: Identity and Access Management with JWT, OAuth2, RBAC, rate limiting.
- **core_audit**: Auditing with JPA persistence and custom handlers.
- **core_vault**: Integration with HashiCorp Vault.
- **core_k8s_generator**: Maven plugin for generating secure Kubernetes manifests.
- **sample_app**: Demo application using all modules.

## Usage

1. Add modules as dependencies in your pom.xml.
2. Configure properties in application.properties.
3. Use annotations: @PublicEndpoint, @Audit, @PreAuthorize.

## Configuration

- `securitas.iam.enabled=true`
- `securitas.vault.enabled=false`
- JWT secret, etc.

## Building

```bash
mvn clean install
```

## Running Sample App

```bash
cd sample_app
mvn spring-boot:run
```

Login with user:user or admin:admin, then access /user/1 or /admin.

## CI/CD

GitHub Actions workflow for build and test.
