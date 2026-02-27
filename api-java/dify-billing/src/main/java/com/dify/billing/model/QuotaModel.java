package com.dify.billing.model;

public class QuotaModel {

    private int usage;
    private int limit;
    private int resetDate;

    public QuotaModel() {
        this(0, 0, -1);
    }

    public QuotaModel(int usage, int limit, int resetDate) {
        this.usage = usage;
        this.limit = limit;
        this.resetDate = resetDate;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getResetDate() {
        return resetDate;
    }

    public void setResetDate(int resetDate) {
        this.resetDate = resetDate;
    }
}
