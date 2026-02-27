package com.dify.billing.model;

public class LicenseModel {

    private String status;
    private String expiredAt;
    private LicenseLimitationModel workspaces;

    public LicenseModel() {
        this.status = "none";
        this.expiredAt = "";
        this.workspaces = new LicenseLimitationModel();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
    }

    public LicenseLimitationModel getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(LicenseLimitationModel workspaces) {
        this.workspaces = workspaces;
    }
}
