package com.dify.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * JPA entity mapping to the existing {@code tenant_account_joins} table.
 *
 * Matches Python's {@code TenantAccountJoin(TypeBase)} in models/account.py.
 * Links accounts to tenants with role assignment.
 */
@Entity
@Table(name = "tenant_account_joins",
        indexes = {
                @Index(name = "tenant_account_join_account_id_idx", columnList = "account_id"),
                @Index(name = "tenant_account_join_tenant_id_idx", columnList = "tenant_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_tenant_account_join", columnNames = {"tenant_id", "account_id"})
        })
public class TenantAccountJoin {

    @Id
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private String tenantId;

    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private String accountId;

    @Column(name = "current", nullable = false)
    private boolean current = false;

    @Column(nullable = false, length = 16)
    private String role = "normal";

    @Column(name = "invited_by", columnDefinition = "uuid")
    private String invitedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected TenantAccountJoin() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public TenantAccountRole getTenantAccountRole() {
        return TenantAccountRole.fromValue(role);
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "TenantAccountJoin{tenantId='" + tenantId + "', accountId='" + accountId + "', role='" + role + "'}";
    }
}
