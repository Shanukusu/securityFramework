package ru.shanina.securityframework.core_audit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityContextProvider implements SecurityContextProvider {
    @Override
    public Optional<SecurityContext> getContext() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String[] roles = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .toArray(String[]::new);
                return Optional.of(new SecurityContext(auth.getName(), roles));
            }
        } catch (Exception e) {
            // ignore
        }
        return Optional.empty();
    }
}
