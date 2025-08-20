package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.UserStatus;
import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserId(UUID userId);
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status); // ACTIVE, BLOCKED, DELETED
    boolean existsByEmail(String email);
    boolean existsByUserId(UUID userId);
    Optional<User> findByGoogleId(String googleId);
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.roles r WHERE r = :role")
    boolean existsByRole(@Param("role") Role role);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") Role role);
    @Query("SELECT u.roles FROM User u WHERE u.userId = :userId")
    Optional<Set<Role>> getRolesByUserId(@Param("userId") UUID userId);
}