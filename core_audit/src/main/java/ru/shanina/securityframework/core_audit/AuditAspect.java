package ru.shanina.securityframework.core_audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditEventPublisher publisher;

    @AfterReturning(pointcut = "@annotation(audit)", returning = "result")
    public void auditSuccess(JoinPoint jp, Audit audit, Object result) {
        publish(audit, "SUCCESS", null, result);
    }

    @AfterThrowing(pointcut = "@annotation(audit)", throwing = "ex")
    public void auditFailure(JoinPoint jp, Audit audit, Exception ex) {
        publish(audit, "FAILURE", ex.getMessage(), null);
    }

    private void publish(Audit audit, String outcome, String error, Object result) {
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception ignored) {}
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth != null ? auth.getName() : "anonymous";

        AuditEvent event = AuditEvent.builder()
                .timestamp(Instant.now())
                .event(audit.event())
                .level(audit.level())
                .principal(principal)
                .sourceIp(request != null ? request.getRemoteAddr() : "unknown")
                .resource(request != null ? request.getRequestURI() : "unknown")
                .outcome(outcome)
                .details(Map.of("error", error, "resultType", result != null ? result.getClass().getSimpleName() : "null"))
                .build();
        publisher.publish(event);
    }
}
