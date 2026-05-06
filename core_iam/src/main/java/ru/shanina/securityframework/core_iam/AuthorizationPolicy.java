package ru.shanina.securityframework.core_iam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface AuthorizationPolicy {
    boolean authorize(Authentication authentication);

    static AuthorizationPolicy role(Role... roles) {
        return auth -> {
            if (auth == null) return false;
            return auth.getAuthorities().stream()
                    .anyMatch(a -> {
                        for (Role role : roles) {
                            if (a.getAuthority().equals("ROLE_" + role.getName())) {
                                return true;
                            }
                        }
                        return false;
                    });
        };
    }

    static AuthorizationPolicy and(AuthorizationPolicy... policies) {
        return auth -> {
            for (AuthorizationPolicy policy : policies) {
                if (!policy.authorize(auth)) {
                    return false;
                }
            }
            return true;
        };
    }

    static AuthorizationPolicy or(AuthorizationPolicy... policies) {
        return auth -> {
            for (AuthorizationPolicy policy : policies) {
                if (policy.authorize(auth)) {
                    return true;
                }
            }
            return false;
        };
    }
}
