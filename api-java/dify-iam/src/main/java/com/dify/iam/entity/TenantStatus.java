package com.dify.iam.entity;

public enum TenantStatus {

    NORMAL("normal"),
    ARCHIVE("archive");

    private final String value;

    TenantStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TenantStatus fromValue(String value) {
        for (TenantStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown tenant status: " + value);
    }
}
