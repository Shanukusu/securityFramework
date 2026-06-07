package ru.shanina.securityframework.core_scanner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Security Scanner Configuration Properties
 */
@Component
@ConfigurationProperties(prefix = "securitas.scanner")
public class ScannerProperties {
    private boolean enabled = false;
    private TrivyConfig trivy = new TrivyConfig();
    private DependencyCheckConfig dependencyCheck = new DependencyCheckConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TrivyConfig getTrivy() {
        return trivy;
    }

    public void setTrivy(TrivyConfig trivy) {
        this.trivy = trivy;
    }

    public DependencyCheckConfig getDependencyCheck() {
        return dependencyCheck;
    }

    public void setDependencyCheck(DependencyCheckConfig dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public static class TrivyConfig {
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class DependencyCheckConfig {
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}

