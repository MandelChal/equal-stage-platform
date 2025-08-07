package com.equal_stage_platform.dev.model;

import com.equal_stage_platform.dev.util.TimeUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = TimeUtils.nowInIsrael();
        createdAt = now;
        lastUpdatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = TimeUtils.nowInIsrael();
    }
}