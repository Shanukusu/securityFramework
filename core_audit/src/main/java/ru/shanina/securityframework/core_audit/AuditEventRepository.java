package ru.shanina.securityframework.core_audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
}
