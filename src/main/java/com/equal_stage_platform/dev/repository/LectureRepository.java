
// LectureRepository.java
package com.equal_stage_platform.dev.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.equal_stage_platform.dev.model.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    
    // הרצאות זמינות
    List<Lecture> findByIsAvailableTrue();
    
    // הרצאות אונליין
    List<Lecture> findByIsOnlineTrue();
    
    // הרצאות פיזיות
    List<Lecture> findByIsOnlineFalse();
    
    // הרצאות עתידיות
    List<Lecture> findByStartTimeAfter(LocalDateTime dateTime);
    
    // חיפוש הרצאות לפי כותרת
    List<Lecture> findByTitleContainingIgnoreCase(String title);
    
    // הרצאות לפי מיקום
    List<Lecture> findByLocationContainingIgnoreCase(String location);
    
    // רשימת כל המיקומים (ללא כפילויות)
    @Query("SELECT DISTINCT l.location FROM Lecture l WHERE l.location IS NOT NULL ORDER BY l.location")
    List<String> findAllLocations();
    
    // הרצאות לפי מרצה ספציפי
    @Query("SELECT l FROM Lecture l JOIN l.lecturers lec WHERE lec.userId = :lecturerId")
    List<Lecture> findByLecturerId(@Param("lecturerId") Long lecturerId);
    
    // ספירת הרצאות
    @Query("SELECT COUNT(l) FROM Lecture l")
    Long countLectures();
    
    // הרצאות זמינות ועתידיות
    @Query("SELECT l FROM Lecture l WHERE l.isAvailable = true AND l.startTime > :now")
    List<Lecture> findAvailableAndFuture(@Param("now") LocalDateTime now);
    
    // הרצאות שמתחילות בטווח זמן מסוים
    List<Lecture> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}