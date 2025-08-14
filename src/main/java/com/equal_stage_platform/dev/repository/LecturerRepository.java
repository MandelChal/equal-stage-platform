package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.Lecturer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    // Find lecturers by working areas and status
    @Query("SELECT DISTINCT l FROM Lecturer l JOIN l.workingAreas wa WHERE wa IN :workingAreas AND l.status = :status")
    List<Lecturer> findByWorkingAreasContainingAndStatus(@Param("workingAreas") Set<Area> workingAreas, @Param("status") LecturerStatus status);    // Find lecturers by working areas
    
    @Query("SELECT DISTINCT l FROM Lecturer l JOIN l.workingAreas wa WHERE wa IN :workingAreas")
    List<Lecturer> findByWorkingAreasContaining(@Param("workingAreas") Set<Area> workingAreas);

    // Filter lecturers by multiple criteria
    @Query("SELECT DISTINCT l FROM Lecturer l " +
           "WHERE l.status = :status " +
           "AND (:targetAudiences IS NULL OR EXISTS (SELECT 1 FROM l.targetAudiences ta WHERE ta.targetAudienceId IN :targetAudiences)) " +
           "AND (:topics IS NULL OR EXISTS (SELECT 1 FROM l.topics t WHERE t.topicId IN :topics)) " +
           "AND (:workingAreas IS NULL OR EXISTS (SELECT 1 FROM l.workingAreas wa WHERE wa IN :workingAreas))" +
           "AND (:minRank IS NULL OR l.rank >= :minRank) " +
           "AND (:maxRank IS NULL OR l.rank <= :maxRank)")
    List<Lecturer> filterLecturers(@Param("status") LecturerStatus status,
                                   @Param("targetAudiences") List<Long> targetAudiences,
                                   @Param("topics") List<Long> topics,
                                   @Param("workingAreas") List<Area> workingAreas,
                                   @Param("minRank") Double minRank,
                                   @Param("maxRank") Double maxRank);

       // pageable filter lecturers by multiple criteria
    @Query("SELECT DISTINCT l FROM Lecturer l " +
           "WHERE l.status = :status " +
           "AND (:targetAudiences IS NULL OR EXISTS (SELECT 1 FROM l.targetAudiences ta WHERE ta.targetAudienceId IN :targetAudiences)) " +
           "AND (:topics IS NULL OR EXISTS (SELECT 1 FROM l.topics t WHERE t.topicId IN :topics)) " +
           "AND (:workingAreas IS NULL OR EXISTS (SELECT 1 FROM l.workingAreas wa WHERE wa IN :workingAreas))" +
           "AND (:minRank IS NULL OR l.rank >= :minRank) " +
           "AND (:maxRank IS NULL OR l.rank <= :maxRank)")
       Page<Lecturer> filterLecturersPageable(@Param("status") LecturerStatus status,
                                   @Param("targetAudiences") List<Long> targetAudiences,
                                   @Param("topics") List<Long> topics,
                                   @Param("workingAreas") List<Area> workingAreas,
                                   @Param("minRank") Double minRank,
                                   @Param("maxRank") Double maxRank,
                                   Pageable pageable);

}