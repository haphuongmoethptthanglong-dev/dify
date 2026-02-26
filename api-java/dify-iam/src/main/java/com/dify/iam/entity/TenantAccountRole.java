package com.dify.iam.entity;

/**
 * RBAC roles for tenant-account relationships.
 *
 * Maps to Python's {@code TenantAccountRole(enum.StrEnum)} in models/account.py.
 * Values are stored as lowercase strings in the {@code tenant_account_joins.role} column.
 */
public enum TenantAccountRole {

    OWNER("owner"),
    ADMIN("admin"),
    EDITOR("editor"),
    NORMAL("normal"),
    DATASET_OPERATOR("dataset_operator");

    private final String value;

    TenantAccountRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TenantAccountRole fromValue(String value) {
        for (TenantAccountRole role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }

    public boolean isPrivileged() {
        return this == OWNER || this == ADMIN;
    }

    public boolean isEditingRole() {
        return this == OWNER || this == ADMIN || this == EDITOR;
    }

    public boolean isDatasetEditRole() {
        return this == OWNER || this == ADMIN || this == EDITOR || this == DATASET_OPERATOR;
    }
}
