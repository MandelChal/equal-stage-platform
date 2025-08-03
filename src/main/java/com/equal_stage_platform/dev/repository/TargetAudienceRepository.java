package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.TargetAudience;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetAudienceRepository extends JpaRepository<TargetAudience, Long> {
    // Search target audiences by type starting with a prefix
    List<TargetAudience> findByTypeStartingWithIgnoreCase(String prefix);
}