package com.dify.billing.model;

public class PluginInstallationPermissionModel {

    private String pluginInstallationScope;
    private boolean restrictToMarketplaceOnly;

    public PluginInstallationPermissionModel() {
        this.pluginInstallationScope = "all";
        this.restrictToMarketplaceOnly = false;
    }

    public String getPluginInstallationScope() {
        return pluginInstallationScope;
    }

    public void setPluginInstallationScope(String pluginInstallationScope) {
        this.pluginInstallationScope = pluginInstallationScope;
    }

    public boolean isRestrictToMarketplaceOnly() {
        return restrictToMarketplaceOnly;
    }

    public void setRestrictToMarketplaceOnly(boolean restrictToMarketplaceOnly) {
        this.restrictToMarketplaceOnly = restrictToMarketplaceOnly;
    }
}
