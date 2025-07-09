package com.equal_stage_platform.dev.repository;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.enums.LectureStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByTitle(String title);

    // get lectures by status
    List<Lecture> findByStatus(LectureStatus status);

    // get lectures by online
    List<Lecture> findByOnline(boolean online);

    List<Lecture> findByStatusAndOnline(LectureStatus status, boolean online);
}