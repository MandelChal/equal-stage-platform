package com.equal_stage_platform.dev.repository;
import com.equal_stage_platform.dev.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByTitle(String title);
}