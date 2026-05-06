package ru.shanina.securityframework.core_audit;

import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant timestamp;
    private String event;
    private String level;
    private String principal;
    private String sourceIp;
    private String resource;
    private String outcome;
    @ElementCollection
    private Map<String, Object> details;
}