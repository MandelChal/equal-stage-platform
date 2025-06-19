// LecturerRepository.java
package com.equal_stage_platform.dev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.equal_stage_platform.dev.model.Lecturer;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    
    // חיפוש מרצה לפי אימייל
    Optional<Lecturer> findByEmail(String email);
    
    // חיפוש מרצה לפי טלפון
    Optional<Lecturer> findByPhone(String phone);
    
    // חיפוש מרצים לפי שם פרטי או משפחה
    @Query("SELECT l FROM Lecturer l WHERE LOWER(l.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(l.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Lecturer> findByNameContaining(@Param("name") String name);
    
    // מרצים לפי עיר
    List<Lecturer> findByCityContainingIgnoreCase(String city);
    
    // רשימת כל הערים (ללא כפילויות)
    @Query("SELECT DISTINCT l.city FROM Lecturer l WHERE l.city IS NOT NULL ORDER BY l.city")
    List<String> findAllCities();
    
    // ספירת מרצים
    @Query("SELECT COUNT(l) FROM Lecturer l")
    Long countLecturers();
    
    // מרצים שיש להם הרצאות
    @Query("SELECT DISTINCT l FROM Lecturer l WHERE SIZE(l.lectures) > 0")
    List<Lecturer> findLecturersWithLectures();
    
    // מרצים לפי עיר ספציפית
    List<Lecturer> findByCity(String city);
}