package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.Lecturer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.equal_stage_platform.dev.model.enums.LecturerStatus;

import io.lettuce.core.dynamic.annotation.Param;

public interface LecturerRepository extends JpaRepository<Lecturer, UUID> {
    // Search lecturer by Status
    List<Lecturer> findByStatus(LecturerStatus status);

    // Search lecturer by email
    Optional<Lecturer> findByEmail(String email);

    // Search lecturer by phone
    Optional<Lecturer> findByPhone(String phone);
    
    // Search lecturers by first name or last name
    @Query("SELECT l FROM Lecturer l WHERE LOWER(l.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(l.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Lecturer> findByNameContaining(@Param("name") String name);
    
    // Count lecturers
    @Query("SELECT COUNT(l) FROM Lecturer l")
    Long countLecturers();
    
    // Lecturers who have lectures
    @Query("SELECT DISTINCT l FROM Lecturer l WHERE SIZE(l.lectures) > 0")
    List<Lecturer> findLecturersWithLectures();
}