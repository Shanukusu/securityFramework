package ru.shanina.securityframework.core_audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditEventPublisher publisher;
    private final SecurityContextProvider securityContextProvider;
    private final RequestContextProvider requestContextProvider;

    @AfterReturning(pointcut = "@annotation(audit)", returning = "result")
    public void auditSuccess(JoinPoint jp, Audit audit, Object result) {
        publish(audit, "SUCCESS", null, result);
    }

    @AfterThrowing(pointcut = "@annotation(audit)", throwing = "ex")
    public void auditFailure(JoinPoint jp, Audit audit, Exception ex) {
        publish(audit, "FAILURE", ex.getMessage(), null);
    }

    private void publish(Audit audit, String outcome, String error, Object result) {
        var secCtx = securityContextProvider.getContext();
        var reqCtx = requestContextProvider.getContext();

        String principal = secCtx.map(c -> c.principal).orElse("anonymous");
        String sourceIp = reqCtx.map(c -> c.sourceIp).orElse("unknown");
        String resource = reqCtx.map(c -> c.uri).orElse("unknown");

        AuditEvent event = AuditEvent.builder()
                .timestamp(Instant.now())
                .event(audit.event())
                .level(audit.level())
                .principal(principal)
                .sourceIp(sourceIp)
                .resource(resource)
                .outcome(outcome)
                .details(Map.of("error", error, "resultType", result != null ? result.getClass().getSimpleName() : "null"))
                .build();
        publisher.publish(event);
    }
}
