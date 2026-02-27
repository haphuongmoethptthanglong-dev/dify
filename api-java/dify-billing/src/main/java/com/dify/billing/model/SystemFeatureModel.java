package com.dify.billing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * System-wide feature flags returned by {@code GET /console/api/system-features}.
 *
 * <p>Fields prefixed with {@code is_} in the Python model require explicit
 * {@link JsonProperty} annotations because Jackson's SNAKE_CASE strategy strips
 * the {@code is} prefix from boolean getters (e.g. {@code isAllowRegister()} →
 * {@code allow_register}), which would break contract parity.
 */
public class SystemFeatureModel {

    private boolean ssoEnforcedForSignin;
    private String ssoEnforcedForSigninProtocol;
    private boolean enableMarketplace;
    private int maxPluginPackageSize;
    private boolean enableEmailCodeLogin;
    private boolean enableEmailPasswordLogin;
    private boolean enableSocialOauthLogin;

    @JsonProperty("is_allow_register")
    private boolean isAllowRegister;

    @JsonProperty("is_allow_create_workspace")
    private boolean isAllowCreateWorkspace;

    @JsonProperty("is_email_setup")
    private boolean isEmailSetup;
    private LicenseModel license;
    private BrandingModel branding;
    private WebAppAuthModel webappAuth;
    private PluginInstallationPermissionModel pluginInstallationPermission;
    private boolean enableChangeEmail;
    private PluginManagerModel pluginManager;
    private List<String> trialModels;
    private boolean enableTrialApp;
    private boolean enableExploreBanner;

    public SystemFeatureModel() {
        this.ssoEnforcedForSignin = false;
        this.ssoEnforcedForSigninProtocol = "";
        this.enableMarketplace = false;
        this.maxPluginPackageSize = 10485760;
        this.enableEmailCodeLogin = false;
        this.enableEmailPasswordLogin = true;
        this.enableSocialOauthLogin = false;
        this.isAllowRegister = false;
        this.isAllowCreateWorkspace = false;
        this.isEmailSetup = false;
        this.license = new LicenseModel();
        this.branding = new BrandingModel();
        this.webappAuth = new WebAppAuthModel();
        this.pluginInstallationPermission = new PluginInstallationPermissionModel();
        this.enableChangeEmail = true;
        this.pluginManager = new PluginManagerModel();
        this.trialModels = new ArrayList<>();
        this.enableTrialApp = false;
        this.enableExploreBanner = false;
    }

    public boolean isSsoEnforcedForSignin() {
        return ssoEnforcedForSignin;
    }

    public void setSsoEnforcedForSignin(boolean ssoEnforcedForSignin) {
        this.ssoEnforcedForSignin = ssoEnforcedForSignin;
    }

    public String getSsoEnforcedForSigninProtocol() {
        return ssoEnforcedForSigninProtocol;
    }

    public void setSsoEnforcedForSigninProtocol(String ssoEnforcedForSigninProtocol) {
        this.ssoEnforcedForSigninProtocol = ssoEnforcedForSigninProtocol;
    }

    public boolean isEnableMarketplace() {
        return enableMarketplace;
    }

    public void setEnableMarketplace(boolean enableMarketplace) {
        this.enableMarketplace = enableMarketplace;
    }

    public int getMaxPluginPackageSize() {
        return maxPluginPackageSize;
    }

    public void setMaxPluginPackageSize(int maxPluginPackageSize) {
        this.maxPluginPackageSize = maxPluginPackageSize;
    }

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
        return isAllowRegister;
    }

    public void setAllowRegister(boolean allowRegister) {
        isAllowRegister = allowRegister;
    }

    public boolean isAllowCreateWorkspace() {
        return isAllowCreateWorkspace;
    }

    public void setAllowCreateWorkspace(boolean allowCreateWorkspace) {
        isAllowCreateWorkspace = allowCreateWorkspace;
    }

    public boolean isEmailSetup() {
        return isEmailSetup;
    }

    public void setEmailSetup(boolean emailSetup) {
        isEmailSetup = emailSetup;
    }

    public LicenseModel getLicense() {
        return license;
    }

    public void setLicense(LicenseModel license) {
        this.license = license;
    }

    public BrandingModel getBranding() {
        return branding;
    }

    public void setBranding(BrandingModel branding) {
        this.branding = branding;
    }

    public WebAppAuthModel getWebappAuth() {
        return webappAuth;
    }

    public void setWebappAuth(WebAppAuthModel webappAuth) {
        this.webappAuth = webappAuth;
    }

    public PluginInstallationPermissionModel getPluginInstallationPermission() {
        return pluginInstallationPermission;
    }

    public void setPluginInstallationPermission(PluginInstallationPermissionModel pluginInstallationPermission) {
        this.pluginInstallationPermission = pluginInstallationPermission;
    }

    public boolean isEnableChangeEmail() {
        return enableChangeEmail;
    }

    public void setEnableChangeEmail(boolean enableChangeEmail) {
        this.enableChangeEmail = enableChangeEmail;
    }

    public PluginManagerModel getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManagerModel pluginManager) {
        this.pluginManager = pluginManager;
    }

    public List<String> getTrialModels() {
        return trialModels;
    }

    public void setTrialModels(List<String> trialModels) {
        this.trialModels = trialModels;
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
