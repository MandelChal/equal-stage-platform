package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.Topic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
     // Search topics by name or description starting with a prefix
    List<Topic> findByNameStartingWithIgnoreCase(String prefix);
}

