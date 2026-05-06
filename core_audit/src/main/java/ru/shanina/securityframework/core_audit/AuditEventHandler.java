package ru.shanina.securityframework.core_audit;

public interface AuditEventHandler {
    void handle(AuditEvent event);
}
