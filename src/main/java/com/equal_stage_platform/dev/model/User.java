package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @CollectionTable(name = "9(8%5&_user_past_passwords", joinColumns = @JoinColumn(name = "user_id"))
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

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isLecturer() {
        return this.role == Role.LECTURER;
    }

    public boolean updatePass(String newPass, PasswordEncoder passwordEncoder) {
        if (!canUsePass(newPass, passwordEncoder)) {
            return false; // New password is invalid
        }
        this.pastPasswords.add(this.password); // Store the old password
        this.password = passwordEncoder.encode(newPass); // Encode the new password
        LocalDateTime now = LocalDateTime.now();
        this.lastUpdatedAt = now;
        this.nextPasswordChange = now.plusMonths(3); // Set next password change to 3 months from now
        return true; // Password updated successfully
    }

    private boolean canUsePass(String newPass, PasswordEncoder passwordEncoder) {
        if (passwordEncoder.matches(newPass, this.password)) {
            return false; // New password must not be the same as the current password
        }
        for(String pastPassword : this.pastPasswords) {
            if (passwordEncoder.matches(newPass, pastPassword)) {
                return false; // New password must not be one of the past passwords
            }
        }
        return true; // New password is valid
    }
}
