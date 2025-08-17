package com.equal_stage_platform.dev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.equal_stage_platform.dev.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find all reviews for a specific lecture
    List<Review> findByLectureId(Long lectureId);
    
    // Find all reviews by a specific user
    List<Review> findByUserUserId(java.util.UUID userId);
    
    // Find reviews for a lecture ordered by date (newest first)
    List<Review> findByLectureLectureIdOrderByCreatedAtDesc(Long lectureId);
    
    // Find reviews for a lecture ordered by rating (highest first)
    List<Review> findByLectureLectureIdOrderByRatingDesc(Long lectureId);
    
    // Check if a user already reviewed a specific lecture
    boolean existsByLectureLectureIdAndUserUserId(Long lectureId, java.util.UUID userId);
    
    // Find a specific user's review for a lecture
    Optional<Review> findByLectureLectureIdAndUserUserId(Long lectureId, java.util.UUID userId);
    
    // Find reviews with a specific rating
    List<Review> findByRating(Integer rating);
    
    // Find reviews with rating greater than or equal to minimum value
    List<Review> findByRatingGreaterThanEqual(Integer minRating);
    
    // Find reviews that have a comment (not null)
    List<Review> findByCommentIsNotNull();
    
    // Find reviews without comments
    List<Review> findByCommentIsNull();
    
    // Find the latest 5 reviews for a lecture
    List<Review> findTop5ByLectureLectureIdOrderByCreatedAtDesc(Long lectureId);
    
    // Custom query - calculate average rating for a lecture
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lecture.lectureId = :lectureId")
    Double findAverageRatingByLectureId(@Param("lectureId") Long lectureId);
    
    // Custom query - count reviews for a lecture
    @Query("SELECT COUNT(r) FROM Review r WHERE r.lecture.lectureId = :lectureId")
    Long countByLectureId(@Param("lectureId") Long lectureId);
    
    // Custom query - count reviews by rating for a specific lecture
    @Query("SELECT COUNT(r) FROM Review r WHERE r.lecture.lectureId = :lectureId AND r.rating = :rating")
    Long countByLectureIdAndRating(@Param("lectureId") Long lectureId, @Param("rating") Integer rating);
    
    // Custom query - find the best reviews (4-5 stars)
    @Query("SELECT r FROM Review r WHERE r.lecture.lectureId = :lectureId AND r.rating >= 4 ORDER BY r.rating DESC, r.createdAt DESC")
    List<Review> findTopReviewsByLectureId(@Param("lectureId") Long lectureId);
    
    // Custom query - find reviews with keywords in comments
    @Query("SELECT r FROM Review r WHERE LOWER(r.comment) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Review> findByCommentContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Custom query - rating statistics for a lecture
    @Query("""
        SELECT r.rating as rating, COUNT(r) as count 
        FROM Review r 
        WHERE r.lecture.lectureId = :lectureId 
        GROUP BY r.rating 
        ORDER BY r.rating DESC
        """)
    List<Object[]> findRatingStatisticsByLectureId(@Param("lectureId") Long lectureId);
    
    // Delete all reviews by a user
    void deleteByUserUserId(java.util.UUID userId);
    
    // Delete all reviews for a lecture
    void deleteByLectureId(Long lectureId);
}