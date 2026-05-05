package ru.shanina.securityframework.core_audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.shanina.securityframework.core_audit.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventPublisher {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publish(AuditEvent event) {
        try {
            log.info("AUDIT: {}", objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            log.error("Failed to serialize audit event", e);
        }
    }
}
