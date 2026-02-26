package com.dify.iam.entity;

/**
 * Account status values.
 *
 * Maps to Python's {@code AccountStatus(enum.StrEnum)} in models/account.py.
 * Stored as string in the {@code accounts.status} column (default: 'active').
 */
public enum AccountStatus {

    PENDING("pending"),
    UNINITIALIZED("uninitialized"),
    ACTIVE("active"),
    BANNED("banned"),
    CLOSED("closed");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AccountStatus fromValue(String value) {
        for (AccountStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown account status: " + value);
    }
}
