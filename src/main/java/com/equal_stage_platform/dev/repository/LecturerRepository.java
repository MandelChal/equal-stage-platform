package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.Lecturer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;

import io.lettuce.core.dynamic.annotation.Param;

public interface LecturerRepository extends JpaRepository<Lecturer, UUID> {
    // Find lecturers by their status
    List<Lecturer> findByStatus(LecturerStatus status);
    Page<Lecturer> findByStatus(LecturerStatus status, Pageable pageable);

    // Search lecturer by email
    Optional<Lecturer> findByEmail(String email);

    // Search lecturer by phone
    Optional<Lecturer> findByPhone(String phone);
    
    // Search lecturers by first name or last name or full name containing a specific string
    @Query("SELECT l FROM Lecturer l WHERE LOWER(l.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(l.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(l.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Lecturer> findByNameContaining(@Param("name") String name);
    
    // Search lecturers by full name or last name starting with a specific string
    @Query("SELECT l FROM Lecturer l WHERE LOWER(l.fullName) LIKE LOWER(CONCAT(:name, '%')) OR LOWER(l.lastName) LIKE LOWER(CONCAT(:name, '%'))")
    List<Lecturer> findByNameStartingWith(@Param("name") String name);

    // Count lecturers
    @Query("SELECT COUNT(l) FROM Lecturer l")
    Long countLecturers();
    
    // Lecturers who have lectures
    @Query("SELECT DISTINCT l FROM Lecturer l WHERE SIZE(l.lectures) > 0")
    List<Lecturer> findLecturersWithLectures();

    List<Lecturer> findByWorkingAreaAndStatus(Area workingArea, LecturerStatus status);
}