package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.Lecturer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecturer, Long> {
}
