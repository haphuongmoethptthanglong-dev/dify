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
 * <p><b>Slice 2</b> adds: Login, Mail, Enterprise, Billing, Plugin, Hosted config
 * groups and expands Features with dataset/education/marketplace/trial/banner flags.
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
    private final Login login = new Login();
    private final Mail mail = new Mail();
    private final Enterprise enterprise = new Enterprise();
    private final Billing billing = new Billing();
    private final Plugin plugin = new Plugin();
    private final Hosted hosted = new Hosted();

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

    public Login getLogin() {
        return login;
    }

    public Mail getMail() {
        return mail;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public Billing getBilling() {
        return billing;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Hosted getHosted() {
        return hosted;
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

    /**
     * Feature flags exposed via {@code /console/api/feature} and {@code /api/feature}.
     * Maps to Python's {@code FeatureConfig}.
     */
    public static class Features {
        /** Whether custom logo replacement is supported. */
        private boolean canReplaceLogo = false;

        /** Whether model load balancing is enabled. */
        private boolean modelLoadBalancingEnabled = false;

        /** Whether the dataset-operator role is enabled. Maps to Python DATASET_OPERATOR_ENABLED. */
        private boolean datasetOperatorEnabled = false;

        /** Whether education features are enabled. Maps to Python EDUCATION_ENABLED. */
        private boolean educationEnabled = false;

        /** Whether the plugin marketplace is enabled. Maps to Python MARKETPLACE_ENABLED. */
        private boolean enableMarketplace = false;

        /** Whether the trial-app banner is enabled. Maps to Python ENABLE_TRIAL_APP. */
        private boolean enableTrialApp = false;

        /** Whether the explore-page banner is enabled. Maps to Python ENABLE_EXPLORE_BANNER. */
        private boolean enableExploreBanner = false;

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

        public boolean isDatasetOperatorEnabled() {
            return datasetOperatorEnabled;
        }

        public void setDatasetOperatorEnabled(boolean datasetOperatorEnabled) {
            this.datasetOperatorEnabled = datasetOperatorEnabled;
        }

        public boolean isEducationEnabled() {
            return educationEnabled;
        }

        public void setEducationEnabled(boolean educationEnabled) {
            this.educationEnabled = educationEnabled;
        }

        public boolean isEnableMarketplace() {
            return enableMarketplace;
        }

        public void setEnableMarketplace(boolean enableMarketplace) {
            this.enableMarketplace = enableMarketplace;
        }

        public boolean isEnableTrialApp() {
            return enableTrialApp;
        }

        public void setEnableTrialApp(boolean enableTrialApp) {
            this.enableTrialApp = enableTrialApp;
        }

        public boolean isEnableExploreBanner() {
            return enableExploreBanner;
        }

        public void setEnableExploreBanner(boolean enableExploreBanner) {
            this.enableExploreBanner = enableExploreBanner;
        }
    }

    /**
     * Login / registration configuration. Maps to Python's {@code AuthConfig}.
     * Bound from {@code dify.login.*}.
     */
    public static class Login {
        /** Maps to Python ENABLE_EMAIL_CODE_LOGIN. */
        private boolean enableEmailCodeLogin = false;

        /** Maps to Python ENABLE_EMAIL_PASSWORD_LOGIN. */
        private boolean enableEmailPasswordLogin = true;

        /** Maps to Python ENABLE_SOCIAL_OAUTH_LOGIN. */
        private boolean enableSocialOauthLogin = false;

        /** Maps to Python ALLOW_REGISTER. */
        private boolean allowRegister = false;

        /** Maps to Python ALLOW_CREATE_WORKSPACE. */
        private boolean allowCreateWorkspace = false;

        public boolean isEnableEmailCodeLogin() {
            return enableEmailCodeLogin;
        }

        public void setEnableEmailCodeLogin(boolean enableEmailCodeLogin) {
            this.enableEmailCodeLogin = enableEmailCodeLogin;
        }

        public boolean isEnableEmailPasswordLogin() {
            return enableEmailPasswordLogin;
        }

        public void setEnableEmailPasswordLogin(boolean enableEmailPasswordLogin) {
            this.enableEmailPasswordLogin = enableEmailPasswordLogin;
        }

        public boolean isEnableSocialOauthLogin() {
            return enableSocialOauthLogin;
        }

        public void setEnableSocialOauthLogin(boolean enableSocialOauthLogin) {
            this.enableSocialOauthLogin = enableSocialOauthLogin;
        }

        public boolean isAllowRegister() {
            return allowRegister;
        }

        public void setAllowRegister(boolean allowRegister) {
            this.allowRegister = allowRegister;
        }

        public boolean isAllowCreateWorkspace() {
            return allowCreateWorkspace;
        }

        public void setAllowCreateWorkspace(boolean allowCreateWorkspace) {
            this.allowCreateWorkspace = allowCreateWorkspace;
        }
    }

    /**
     * Mail configuration. Maps to Python's {@code MailConfig}.
     * Bound from {@code dify.mail.*}.
     */
    public static class Mail {
        /** Maps to Python MAIL_TYPE. */
        private String type = "";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * Enterprise feature gate. Bound from {@code dify.enterprise.*}.
     */
    public static class Enterprise {
        /** Maps to Python ENTERPRISE_ENABLED. */
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Billing feature gate. Bound from {@code dify.billing.*}.
     */
    public static class Billing {
        /** Maps to Python BILLING_ENABLED. */
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Plugin configuration. Bound from {@code dify.plugin.*}.
     */
    public static class Plugin {
        /** Maximum plugin package size in bytes (default 10 MB). Maps to Python PLUGIN_MAX_PACKAGE_SIZE. */
        private int maxPackageSize = 10485760;

        public int getMaxPackageSize() {
            return maxPackageSize;
        }

        public void setMaxPackageSize(int maxPackageSize) {
            this.maxPackageSize = maxPackageSize;
        }
    }

    /**
     * Hosted model-provider configuration used for trial-model derivation.
     * Bound from {@code dify.hosted.*}.
     *
     * <p>Python checks {@code HOSTED_{config_key}_PAID_ENABLED} and
     * {@code HOSTED_{config_key}_TRIAL_ENABLED} for each {@code HostedTrialProvider}.
     */
    public static class Hosted {
        private final HostedProvider openai = new HostedProvider();
        private final HostedProvider anthropic = new HostedProvider();
        private final HostedProvider gemini = new HostedProvider();
        private final HostedProvider xai = new HostedProvider();
        private final HostedProvider deepseek = new HostedProvider();
        private final HostedProvider tongyi = new HostedProvider();

        public HostedProvider getOpenai() {
            return openai;
        }

        public HostedProvider getAnthropic() {
            return anthropic;
        }

        public HostedProvider getGemini() {
            return gemini;
        }

        public HostedProvider getXai() {
            return xai;
        }

        public HostedProvider getDeepseek() {
            return deepseek;
        }

        public HostedProvider getTongyi() {
            return tongyi;
        }
    }

    /**
     * Per-provider hosted trial/paid flags.
     * Maps to Python's {@code HOSTED_{KEY}_PAID_ENABLED} / {@code HOSTED_{KEY}_TRIAL_ENABLED}.
     */
    public static class HostedProvider {
        private boolean paidEnabled = false;
        private boolean trialEnabled = false;

        public boolean isPaidEnabled() {
            return paidEnabled;
        }

        public void setPaidEnabled(boolean paidEnabled) {
            this.paidEnabled = paidEnabled;
        }

        public boolean isTrialEnabled() {
            return trialEnabled;
        }

        public void setTrialEnabled(boolean trialEnabled) {
            this.trialEnabled = trialEnabled;
        }
    }
}
