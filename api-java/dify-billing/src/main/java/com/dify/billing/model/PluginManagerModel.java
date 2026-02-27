package com.dify.billing.model;

public class PluginManagerModel {

    private boolean enabled;

    public PluginManagerModel() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
