package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.LecturerTopic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerTopicRepository extends JpaRepository<LecturerTopic, Long> {
     // Search topics by name or description starting with a prefix
    List<LecturerTopic> findByNameStartingWithIgnoreCase(String prefix);
}

