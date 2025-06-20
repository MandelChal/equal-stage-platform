package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.UserStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "7$&*588$_users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; // USER, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status; // ACTIVE, BLOCKED, DELETED

    @ElementCollection
    @CollectionTable(name = "user_past_passwords", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "past_password")
    private Set<String> pastPasswords;

    @Column(name = "next_password_change", nullable = false)
    private LocalDateTime nextPasswordChange;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = this.createdAt;
        this.role = Role.USER;
        this.status = UserStatus.ACTIVE;
        this.nextPasswordChange = LocalDateTime.now().plusDays(30*4);
        this.pastPasswords = new HashSet<>();
    }

}
