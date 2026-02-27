package com.dify.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Persistable;

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
public class TenantAccountJoin implements Persistable<UUID> {

    @Transient
    private boolean isNew = true;

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name = "current", nullable = false)
    private boolean current = false;

    @Column(nullable = false, length = 16)
    private String role = "normal";

    @Column(name = "invited_by", columnDefinition = "uuid")
    private UUID invitedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        isNew = false;
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PostLoad
    void onLoad() {
        isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    protected TenantAccountJoin() {
    }

    public TenantAccountJoin(UUID id, UUID tenantId, UUID accountId, String role) {
        this.id = id;
        this.tenantId = tenantId;
        this.accountId = accountId;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
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

    public UUID getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(UUID invitedBy) {
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
