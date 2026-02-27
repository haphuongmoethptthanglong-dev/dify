package com.dify.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LicenseLimitationModel {

    private boolean enabled;
    private int size;
    private int limit;

    public LicenseLimitationModel() {
        this(false, 0, 0);
    }

    public LicenseLimitationModel(boolean enabled, int size, int limit) {
        this.enabled = enabled;
        this.size = size;
        this.limit = limit;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @JsonIgnore
    public boolean isAvailable(int required) {
        if (!enabled || limit == 0) {
            return true;
        }
        return (limit - size) >= required;
    }

    @JsonIgnore
    public boolean isAvailable() {
        return isAvailable(1);
    }
}
