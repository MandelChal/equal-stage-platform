package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;

import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.SecurityEventType;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "45$^87(}_security_events")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "security_event_id", nullable = false, unique = true)
    private Long securityEventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private SecurityEventType eventType;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_role", nullable = false)
    private Role userRole;

    @Column(name = "event_data", nullable = false, columnDefinition = "TEXT")
    private String eventData;
}