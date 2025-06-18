package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.Lecturer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    List<Lecturer> findByStatus(LecturerStatus status);
}