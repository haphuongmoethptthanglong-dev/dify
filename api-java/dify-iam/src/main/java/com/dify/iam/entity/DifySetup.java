package com.dify.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA entity mapping to the existing {@code dify_setups} table.
 *
 * Matches Python's {@code DifySetup(TypeBase)} in models/model.py.
 * Uses {@code version} (String) as the primary key — not a UUID.
 * The {@code setup_at} column defaults to the current timestamp on insert.
 */
@Entity
@Table(name = "dify_setups")
public class DifySetup {

    @Id
    @Column(length = 255, nullable = false)
    private String version;

    @Column(name = "setup_at", nullable = false)
    private LocalDateTime setupAt;

    protected DifySetup() {
    }

    public DifySetup(String version) {
        this.version = version;
        this.setupAt = LocalDateTime.now();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getSetupAt() {
        return setupAt;
    }

    public void setSetupAt(LocalDateTime setupAt) {
        this.setupAt = setupAt;
    }

    @Override
    public String toString() {
        return "DifySetup{version='" + version + "', setupAt=" + setupAt + "}";
    }
}
