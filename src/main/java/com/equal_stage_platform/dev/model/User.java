package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.equal_stage_platform.dev.dto.RegisterRequest;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.equal_stage_platform.dev.util.TimeUtils;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "7$&*588$_users")
public class User extends BaseAuditableEntity {
    @Id
    @GeneratedValue
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    // @Enumerated(EnumType.STRING)
    // @Column(name = "role", nullable = false)
    // private Role role; // CLIENT, SUPER_ADMIN, ADMIN, LECTURER

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "9(8%5&_user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status; // ACTIVE, BLOCKED, DELETED

    @ElementCollection
    @CollectionTable(name = "9(8%5&_user_past_passwords", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "past_password")
    private Set<String> pastPasswords;

    @Column(name = "next_password_change", nullable = true)
    private LocalDateTime nextPasswordChange;

    @Column(name = "google_id", nullable = true)
    private String googleId;

    public User(RegisterRequest registerRequest) {
        this.firstName = registerRequest.getFirstName();
        this.lastName = registerRequest.getLastName();
        this.fullName = registerRequest.getFirstName() + " " + registerRequest.getLastName();
        this.phone = registerRequest.getPhone();
        this.email = registerRequest.getEmail();
        this.password = registerRequest.getPassword();
        // Timestamps are now handled automatically by JPA auditing
        this.roles = new HashSet<>();
        this.roles.add(Role.CLIENT);
        this.status = UserStatus.ACTIVE;
        this.nextPasswordChange = TimeUtils.nowInIsrael().plusMonths(4);
        this.pastPasswords = new HashSet<>();
        this.googleId = null;
    }

    public User(String email, String googleId) {
        this.email = email;
        this.googleId = googleId;
        this.roles = new HashSet<>();
        this.roles.add(Role.CLIENT);
        this.status = UserStatus.ACTIVE;
        // default values for non-nullable fields
        this.firstName = "";
        this.lastName = "";
        this.fullName = "";
        this.phone = "";
        this.password = "";
        this.nextPasswordChange = null;
        this.pastPasswords = new HashSet<>();
    }

    public boolean isRegistrationCompleted() {
        return this.firstName != "" && this.firstName != null && this.lastName != "" && this.lastName != null && this.phone != "" && this.phone != null;
    }

    public boolean isAdmin() {
        return this.roles.contains(Role.ADMIN)||this.roles.contains(Role.SUPER_ADMIN);
    }

    public boolean isLecturer() {
        return this.roles.contains(Role.LECTURER);
    }

    public boolean isClient() {
        return this.roles.contains(Role.CLIENT);
    }

    public boolean isSuperAdmin() {
        return this.roles.contains(Role.SUPER_ADMIN);
    }

    public boolean canDeleteClient() {
        //logic -> client can be deleted only if he is only a client
        return this.roles.size() == 1 && this.roles.contains(Role.CLIENT);
    }

    public boolean canDeleteLecturer() {
        //logic -> lecturer can be deleted only if he is not an admin or super admin
        return this.roles.contains(Role.LECTURER);
    }
    
    public boolean canDeleteAdmin() {
        //logic -> admin can be deleted only if he is not a super admin
        return this.roles.contains(Role.ADMIN) && !this.roles.contains(Role.SUPER_ADMIN);
    }
    

    public boolean updatePass(String newPass, PasswordEncoder passwordEncoder) {
        if (!canUsePass(newPass, passwordEncoder)) {
            return false; // New password is invalid
        }
        this.pastPasswords.add(this.password); // Store the old password
        this.password = passwordEncoder.encode(newPass); // Encode the new password
        LocalDateTime now = TimeUtils.nowInIsrael();
        // lastUpdatedAt is now handled automatically by JPA auditing
        this.nextPasswordChange = now.plusMonths(4); // Set next password change to 3 months from now
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.fullName = firstName + " " + this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.fullName = this.firstName + " " + lastName;
    }

    public void completeRegistration(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.fullName = firstName + " " + lastName;
    }
    public void addRole(Role role) {
        this.roles.add(role);
    }
    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}
