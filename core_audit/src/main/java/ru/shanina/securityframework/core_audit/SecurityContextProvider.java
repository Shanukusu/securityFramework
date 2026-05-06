package ru.shanina.securityframework.core_audit;

import java.util.Optional;

public interface SecurityContextProvider {
    Optional<SecurityContext> getContext();

    class SecurityContext {
        public final String principal;
        public final String[] roles;

        public SecurityContext(String principal, String[] roles) {
            this.principal = principal;
            this.roles = roles;
        }
    }
}
