package com.equal_stage_platform.dev.repository;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.Area;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByTitle(String title);
    Optional<Lecture> findByTitleAndApproved(String title, boolean approved);
    Optional<Lecture> findByLectureIdAndApproved(Long lectureId, boolean approved);

    // get lectures by status
    List<Lecture> findByStatusAndApproved(LectureStatus status, boolean approved);
    Page<Lecture> findByStatusAndApproved(LectureStatus status, boolean approved, Pageable pageable);

    // get lectures by online
    List<Lecture> findByOnlineAndApproved(boolean online, boolean approved);

    List<Lecture> findByStatusAndOnlineAndApproved(LectureStatus status, boolean online, boolean approved);

    // Search lectures by title starting with a specific string
    @Query("SELECT l FROM Lecture l WHERE LOWER(l.title) LIKE LOWER(CONCAT(:title, '%'))")
    List<Lecture> findByTitleStartingWith(@Param("title") String title);

    // get lectures by approved status
    List<Lecture> findByApproved(boolean approved);

    // Filter lectures by multiple criteria
    @Query("SELECT DISTINCT l FROM Lecture l " +
           "WHERE l.status = :status " +
           "AND l.approved = :approved " +
           "AND (:targetAudiences IS NULL OR EXISTS (SELECT 1 FROM l.targetAudiences ta WHERE ta.targetAudienceId IN :targetAudiences)) " +
           "AND (:topics IS NULL OR EXISTS (SELECT 1 FROM l.topics t WHERE t.topicId IN :topics)) " +
           "AND (:workingAreas IS NULL OR EXISTS (SELECT 1 FROM l.lecturers lec JOIN lec.workingAreas wa WHERE wa IN :workingAreas))" +
           "AND (:minRank IS NULL OR l.rank >= :minRank) " +
           "AND (:maxRank IS NULL OR l.rank <= :maxRank)")
    List<Lecture> filterLectures(@Param("status") LectureStatus status,
                                 @Param("approved") boolean approved,
                                 @Param("targetAudiences") List<Long> targetAudiences,
                                 @Param("topics") List<Long> topics,
                                 @Param("workingAreas") List<Area> workingAreas,
                                 @Param("minRank") Double minRank,
                                 @Param("maxRank") Double maxRank);

       // pageable lectures by multiple criteria
           @Query("SELECT DISTINCT l FROM Lecture l " +
           "WHERE l.status = :status " +
           "AND l.approved = :approved " +
           "AND (:targetAudiences IS NULL OR EXISTS (SELECT 1 FROM l.targetAudiences ta WHERE ta.targetAudienceId IN :targetAudiences)) " +
           "AND (:topics IS NULL OR EXISTS (SELECT 1 FROM l.topics t WHERE t.topicId IN :topics)) " +
           "AND (:workingAreas IS NULL OR EXISTS (SELECT 1 FROM l.lecturers lec JOIN lec.workingAreas wa WHERE wa IN :workingAreas)) " +
           "AND (:minRank IS NULL OR l.rank >= :minRank) " +
           "AND (:maxRank IS NULL OR l.rank <= :maxRank)")
       Page<Lecture> filterLecturesPageable(@Param("status") LectureStatus status,
                                   @Param("approved") boolean approved,
                                   @Param("targetAudiences") List<Long> targetAudiences,
                                   @Param("topics") List<Long> topics,
                                   @Param("workingAreas") List<Area> workingAreas,
                                   @Param("minRank") Double minRank,
                                   @Param("maxRank") Double maxRank,
                                   Pageable pageable);
}