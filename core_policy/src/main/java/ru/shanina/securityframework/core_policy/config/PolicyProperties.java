package ru.shanina.securityframework.core_policy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Policy Engine Configuration Properties
 *
 * Example application.yml:
 * securitas:
 *   policy:
 *     enabled: true
 *     engine: abac  # or opa
 *     opa:
 *       url: http://localhost:8181
 *       timeout: 5000
 */
@Component
@ConfigurationProperties(prefix = "securitas.policy")
public class PolicyProperties {
    private boolean enabled = true;
    private String engine = "abac"; // "abac" or "opa"
    private OpaConfig opa = new OpaConfig();
    private CacheConfig cache = new CacheConfig();

    // Getters and Setters
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

    public static class OpaConfig {
        private String url = "http://localhost:8181";
        private long timeout = 5000;

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
    }

    public static class CacheConfig {
        private long ttlMinutes = 5;
        private int maxSize = 1000;

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
    }
}

