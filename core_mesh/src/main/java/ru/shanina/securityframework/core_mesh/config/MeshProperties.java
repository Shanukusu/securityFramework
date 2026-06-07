package ru.shanina.securityframework.core_mesh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Service Mesh Configuration Properties
 */
@Component
@ConfigurationProperties(prefix = "securitas.mesh")
public class MeshProperties {
    private boolean enabled = false;
    private String provider = "istio"; // "istio" or "linkerd"
    private MtlsProperties mtls = new MtlsProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public MtlsProperties getMtls() {
        return mtls;
    }

    public void setMtls(MtlsProperties mtls) {
        this.mtls = mtls;
    }

    public static class MtlsProperties {
        private String enforcement = "STRICT"; // STRICT, PERMISSIVE, DISABLED
        private long rotationIntervalDays = 90;

        public String getEnforcement() {
            return enforcement;
        }

        public void setEnforcement(String enforcement) {
            this.enforcement = enforcement;
        }

        public long getRotationIntervalDays() {
            return rotationIntervalDays;
        }

        public void setRotationIntervalDays(long rotationIntervalDays) {
            this.rotationIntervalDays = rotationIntervalDays;
        }
    }
}

