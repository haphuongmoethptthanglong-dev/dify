package com.dify.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Typed configuration properties binding all {@code dify.*} entries in application.yml.
 *
 * Maps to Python's {@code DifyConfig(BaseSettings)} composed from DeploymentConfig,
 * FeatureConfig, etc.  Only the properties needed for the current migration slice
 * are exposed here; new slices add fields as they are implemented.
 *
 * Lives in dify-api-gateway so controllers can inject it directly.
 * The actual property values are defined in dify-api-boot's application.yml.
 */
@Component
@ConfigurationProperties(prefix = "dify")
public class DifyProperties {

    /** Deployment edition: SELF_HOSTED, CLOUD, or ENTERPRISE. */
    private String edition = "SELF_HOSTED";

    /** URL to check for version updates. Null/empty disables the check. */
    private String checkUpdateUrl;

    /** Current application version (matches Python's project.version). */
    private String version = "0.0.0";

    /**
     * Init password for first-time setup.
     * Maps to Python's {@code os.environ.get("INIT_PASSWORD")}.
     */
    private String initPassword;

    private final Security security = new Security();
    private final Features features = new Features();

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getCheckUpdateUrl() {
        return checkUpdateUrl;
    }

    public void setCheckUpdateUrl(String checkUpdateUrl) {
        this.checkUpdateUrl = checkUpdateUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInitPassword() {
        return initPassword;
    }

    public void setInitPassword(String initPassword) {
        this.initPassword = initPassword;
    }

    public Security getSecurity() {
        return security;
    }

    public Features getFeatures() {
        return features;
    }

    public static class Security {
        private String secretKey = "your-secret-key";

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static class Features {
        /** Whether custom logo replacement is supported. */
        private boolean canReplaceLogo = false;

        /** Whether model load balancing is enabled. */
        private boolean modelLoadBalancingEnabled = false;

        public boolean isCanReplaceLogo() {
            return canReplaceLogo;
        }

        public void setCanReplaceLogo(boolean canReplaceLogo) {
            this.canReplaceLogo = canReplaceLogo;
        }

        public boolean isModelLoadBalancingEnabled() {
            return modelLoadBalancingEnabled;
        }

        public void setModelLoadBalancingEnabled(boolean modelLoadBalancingEnabled) {
            this.modelLoadBalancingEnabled = modelLoadBalancingEnabled;
        }
    }
}
