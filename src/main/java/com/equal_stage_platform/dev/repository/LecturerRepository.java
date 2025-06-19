package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.Lecturer;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;

public interface LecturerRepository extends JpaRepository<Lecturer, UUID> {
    List<Lecturer> findByStatus(LecturerStatus status);
}