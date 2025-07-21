package com.equal_stage_platform.dev.repository;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.enums.LectureStatus;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByTitle(String title);

    // get lectures by status
    List<Lecture> findByStatus(LectureStatus status);
    Page<Lecture> findByStatus(LectureStatus status, Pageable pageable);

    // get lectures by online
    List<Lecture> findByOnline(boolean online);

    List<Lecture> findByStatusAndOnline(LectureStatus status, boolean online);

    // Search lectures by title starting with a specific string
    @Query("SELECT l FROM Lecture l WHERE LOWER(l.title) LIKE LOWER(CONCAT(:title, '%'))")
    List<Lecture> findByTitleStartingWith(@Param("title") String title);
}