package com.dify.billing.model;

public class WebAppAuthModel {

    private boolean enabled;
    private boolean allowSso;
    private WebAppAuthSSOModel ssoConfig;
    private boolean allowEmailCodeLogin;
    private boolean allowEmailPasswordLogin;

    public WebAppAuthModel() {
        this.enabled = false;
        this.allowSso = false;
        this.ssoConfig = new WebAppAuthSSOModel();
        this.allowEmailCodeLogin = false;
        this.allowEmailPasswordLogin = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAllowSso() {
        return allowSso;
    }

    public void setAllowSso(boolean allowSso) {
        this.allowSso = allowSso;
    }

    public WebAppAuthSSOModel getSsoConfig() {
        return ssoConfig;
    }

    public void setSsoConfig(WebAppAuthSSOModel ssoConfig) {
        this.ssoConfig = ssoConfig;
    }

    public boolean isAllowEmailCodeLogin() {
        return allowEmailCodeLogin;
    }

    public void setAllowEmailCodeLogin(boolean allowEmailCodeLogin) {
        this.allowEmailCodeLogin = allowEmailCodeLogin;
    }

    public boolean isAllowEmailPasswordLogin() {
        return allowEmailPasswordLogin;
    }

    public void setAllowEmailPasswordLogin(boolean allowEmailPasswordLogin) {
        this.allowEmailPasswordLogin = allowEmailPasswordLogin;
    }
}
