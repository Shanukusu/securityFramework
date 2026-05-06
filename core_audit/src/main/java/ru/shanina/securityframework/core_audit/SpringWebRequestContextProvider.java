package ru.shanina.securityframework.core_audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class SpringWebRequestContextProvider implements RequestContextProvider {
    @Override
    public Optional<RequestContext> getContext() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String sourceIp = request.getRemoteAddr();
                String method = request.getMethod();
                String uri = request.getRequestURI();
                return Optional.of(new RequestContext(sourceIp, method, uri));
            }
        } catch (Exception e) {
            // ignore - not in HTTP context
        }
        return Optional.empty();
    }
}
