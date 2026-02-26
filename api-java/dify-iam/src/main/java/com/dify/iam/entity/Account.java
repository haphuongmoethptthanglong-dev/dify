package com.dify.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity mapping to the existing {@code accounts} table.
 *
 * Matches Python's {@code Account(UserMixin, TypeBase)} in models/account.py.
 * Read-only during initial migration phase — no schema modifications.
 */
@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "account_email_idx", columnList = "email")
})
public class Account {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(name = "password_salt", length = 255)
    private String passwordSalt;

    @Column(length = 255)
    private String avatar;

    @Column(name = "interface_language", length = 255)
    private String interfaceLanguage;

    @Column(name = "interface_theme", length = 255)
    private String interfaceTheme;

    @Column(length = 255)
    private String timezone;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 255)
    private String lastLoginIp;

    @Column(name = "last_active_at", nullable = false)
    private LocalDateTime lastActiveAt;

    @Column(nullable = false, length = 16)
    private String status = "active";

    @Column(name = "initialized_at")
    private LocalDateTime initializedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Transient role field — populated at runtime from TenantAccountJoin,
     * matching Python's {@code Account.role} dataclass field.
     */
    @Transient
    private TenantAccountRole role;

    /**
     * Transient current tenant — populated at runtime,
     * matching Python's {@code Account._current_tenant} field.
     */
    @Transient
    private Tenant currentTenant;

    protected Account() {
    }

    /**
     * Create a new Account for programmatic construction (e.g., first-time setup).
     * JPA still uses the no-arg constructor for hydration.
     */
    public Account(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
    }

    public String getInterfaceTheme() {
        return interfaceTheme;
    }

    public void setInterfaceTheme(String interfaceTheme) {
        this.interfaceTheme = interfaceTheme;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AccountStatus getAccountStatus() {
        return AccountStatus.fromValue(status);
    }

    public LocalDateTime getInitializedAt() {
        return initializedAt;
    }

    public void setInitializedAt(LocalDateTime initializedAt) {
        this.initializedAt = initializedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPasswordSet() {
        return password != null;
    }

    public TenantAccountRole getRole() {
        return role;
    }

    public void setRole(TenantAccountRole role) {
        this.role = role;
    }

    public Tenant getCurrentTenant() {
        return currentTenant;
    }

    public void setCurrentTenant(Tenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    public UUID getCurrentTenantId() {
        return currentTenant != null ? currentTenant.getId() : null;
    }

    public boolean isAdminOrOwner() {
        return role != null && role.isPrivileged();
    }

    public boolean hasEditPermission() {
        return role != null && role.isEditingRole();
    }

    public boolean isDatasetEditor() {
        return role != null && role.isDatasetEditRole();
    }

    public boolean isDatasetOperator() {
        return role == TenantAccountRole.DATASET_OPERATOR;
    }

    @Override
    public String toString() {
        return "Account{id='" + id + "', email='" + email + "'}";
    }
}
