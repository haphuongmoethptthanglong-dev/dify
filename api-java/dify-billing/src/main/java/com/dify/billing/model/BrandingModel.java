package com.dify.billing.model;

public class BrandingModel {

    private boolean enabled;
    private String applicationTitle;
    private String loginPageLogo;
    private String workspaceLogo;
    private String favicon;

    public BrandingModel() {
        this.enabled = false;
        this.applicationTitle = "";
        this.loginPageLogo = "";
        this.workspaceLogo = "";
        this.favicon = "";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public void setApplicationTitle(String applicationTitle) {
        this.applicationTitle = applicationTitle;
    }

    public String getLoginPageLogo() {
        return loginPageLogo;
    }

    public void setLoginPageLogo(String loginPageLogo) {
        this.loginPageLogo = loginPageLogo;
    }

    public String getWorkspaceLogo() {
        return workspaceLogo;
    }

    public void setWorkspaceLogo(String workspaceLogo) {
        this.workspaceLogo = workspaceLogo;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }
}
