package com.dify.billing.model;

public enum LicenseStatus {

    NONE("none"),
    INACTIVE("inactive"),
    ACTIVE("active"),
    EXPIRING("expiring"),
    EXPIRED("expired"),
    LOST("lost");

    private final String value;

    LicenseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LicenseStatus fromValue(String value) {
        for (LicenseStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown license status: " + value);
    }
}
