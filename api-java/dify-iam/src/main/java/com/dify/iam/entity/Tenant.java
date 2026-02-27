package com.dify.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Persistable;

/**
 * JPA entity mapping to the existing {@code tenants} table.
 *
 * Matches Python's {@code Tenant(TypeBase)} in models/account.py.
 * Read-only during initial migration phase.
 */
@Entity
@Table(name = "tenants")
public class Tenant implements Persistable<UUID> {

    @Transient
    private boolean isNew = true;

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "encrypt_public_key", columnDefinition = "text")
    private String encryptPublicKey;

    @Column(nullable = false, length = 255)
    private String plan = "basic";

    @Column(nullable = false, length = 255)
    private String status = "normal";

    @Column(name = "custom_config", columnDefinition = "text")
    private String customConfig;

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

    protected Tenant() {
    }

    public Tenant(UUID id, String name) {
        this.id = id;
        this.name = name;
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

    public String getEncryptPublicKey() {
        return encryptPublicKey;
    }

    public void setEncryptPublicKey(String encryptPublicKey) {
        this.encryptPublicKey = encryptPublicKey;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TenantStatus getTenantStatus() {
        return TenantStatus.fromValue(status);
    }

    public String getCustomConfig() {
        return customConfig;
    }

    public void setCustomConfig(String customConfig) {
        this.customConfig = customConfig;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Tenant{id='" + id + "', name='" + name + "'}";
    }
}
