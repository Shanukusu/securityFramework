package ru.shanina.securityframework.core_audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.shanina.securityframework.core_audit.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventPublisher {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuditEventRepository repository;
    private final List<AuditEventHandler> handlers;

    public void publish(AuditEvent event) {
        repository.save(event);
        for (AuditEventHandler handler : handlers) {
            try {
                handler.handle(event);
            } catch (Exception e) {
                log.error("Error in audit handler", e);
            }
        }
        try {
            log.info("AUDIT: {}", objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            log.error("Failed to serialize audit event", e);
        }
    }
}
