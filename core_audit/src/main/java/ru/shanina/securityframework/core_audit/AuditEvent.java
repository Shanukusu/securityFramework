package ru.shanina.securityframework.core_audit;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class AuditEvent {
    private Instant timestamp;
    private String event;
    private String level;
    private String principal;
    private String sourceIp;
    private String resource;
    private String outcome;
    private Map<String, Object> details;
}