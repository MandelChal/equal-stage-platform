package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.LectureTopic; // Lecture*Topic

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureTopicRepository extends JpaRepository<LectureTopic, Long> { // Lecture*Topic
     // Search topics by name or description starting with a prefix
    List<LectureTopic> findByNameStartingWithIgnoreCase(String prefix); // Lecture*Topic
}

