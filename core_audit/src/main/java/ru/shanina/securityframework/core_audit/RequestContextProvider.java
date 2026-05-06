package ru.shanina.securityframework.core_audit;

import java.util.Optional;

public interface RequestContextProvider {
    Optional<RequestContext> getContext();

    class RequestContext {
        public final String sourceIp;
        public final String method;
        public final String uri;

        public RequestContext(String sourceIp, String method, String uri) {
            this.sourceIp = sourceIp;
            this.method = method;
            this.uri = uri;
        }
    }
}
