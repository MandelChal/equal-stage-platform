package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.UserStatus;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserId(UUID userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(Role role);
    List<User> findByStatus(UserStatus status); // ACTIVE, BLOCKED, DELETED
    @Query("SELECT u.role FROM User u WHERE u.userId = :userId")
    Optional<Role> getRoleByUserId(@Param("userId") UUID userId);
}