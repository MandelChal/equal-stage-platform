package com.equal_stage_platform.dev.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.equal_stage_platform.dev.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String>{
    Optional<PasswordResetToken> findByToken(String token);
}
