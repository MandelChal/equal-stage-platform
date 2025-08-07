package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;

import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.SecurityEventType;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@Data
@Entity
@Table(name = "45$^87(}_security_events")
@EntityListeners(AuditingEntityListener.class)
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "security_event_id", nullable = false, unique = true)
    private Long securityEventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private SecurityEventType eventType;

    @CreatedDate
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_role", nullable = false)
    private Role userRole;

    @Column(name = "event_data", nullable = false, columnDefinition = "TEXT")
    private String eventData;

    public SecurityEvent(SecurityEventType eventType, Long userId, Role userRole, String eventData) {
        this.eventType = eventType;
        this.userId = userId;
        this.userRole = userRole;
        this.eventData = eventData;
    }
}