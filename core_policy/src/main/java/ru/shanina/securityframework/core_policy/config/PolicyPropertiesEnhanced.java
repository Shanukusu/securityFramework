package ru.shanina.securityframework.core_policy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Enhanced Policy Engine Configuration
 */
@Component
@ConfigurationProperties(prefix = "securitas.policy")
public class PolicyPropertiesEnhanced {
    private boolean enabled = true;
    private String engine = "abac";
    private OpaConfig opa = new OpaConfig();
    private CacheConfig cache = new CacheConfig();
    private FailureMode failureMode = FailureMode.DENY;  // DENY или ALLOW

    // Getters & Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public OpaConfig getOpa() {
        return opa;
    }

    public void setOpa(OpaConfig opa) {
        this.opa = opa;
    }

    public CacheConfig getCache() {
        return cache;
    }

    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }

    public FailureMode getFailureMode() {
        return failureMode;
    }

    public void setFailureMode(FailureMode failureMode) {
        this.failureMode = failureMode;
    }

    /**
     * Режим на случай отказа OPA
     */
    public enum FailureMode {
        DENY,   // Запретить по умолчанию (fail-close)
        ALLOW   // Разрешить по умолчанию (fail-open)
    }

    public static class OpaConfig {
        private String url = "http://localhost:8181";
        private long timeout = 5000;
        private boolean circuitBreakerEnabled = true;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public boolean isCircuitBreakerEnabled() {
            return circuitBreakerEnabled;
        }

        public void setCircuitBreakerEnabled(boolean circuitBreakerEnabled) {
            this.circuitBreakerEnabled = circuitBreakerEnabled;
        }
    }

    public static class CacheConfig {
        private long ttlMinutes = 5;
        private int maxSize = 1000;
        private boolean enabled = true;

        public long getTtlMinutes() {
            return ttlMinutes;
        }

        public void setTtlMinutes(long ttlMinutes) {
            this.ttlMinutes = ttlMinutes;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}

