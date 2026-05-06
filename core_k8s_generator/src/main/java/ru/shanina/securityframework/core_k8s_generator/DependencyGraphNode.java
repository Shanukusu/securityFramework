package ru.shanina.securityframework.core_k8s_generator;

import java.util.HashSet;
import java.util.Set;

public class DependencyGraphNode {
    public final String serviceName;
    public final String className;
    public final Set<ExternalCall> externalCalls = new HashSet<>();
    public final Set<String> internalDependencies = new HashSet<>();

    public DependencyGraphNode(String serviceName, String className) {
        this.serviceName = serviceName;
        this.className = className;
    }

    public static class ExternalCall {
        public final String protocol; // HTTP, gRPC, etc
        public final String host;
        public final int port;
        public final String description;

        public ExternalCall(String protocol, String host, int port, String description) {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            this.description = description;
        }
    }
}
