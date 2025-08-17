package com.equal_stage_platform.dev.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.dto.CreateReviewDTO;
import com.equal_stage_platform.dev.dto.ReviewResponseDTO;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Review;
import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.ReviewRepository;
import com.equal_stage_platform.dev.repository.UserRepository;

@Service
@Transactional
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private LectureRepository lectureRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Create a new review
    public ReviewResponseDTO createReview(CreateReviewDTO createReviewDTO, UUID userId) {
        // Check if user already reviewed this lecture
        if (reviewRepository.existsByLectureLectureIdAndUserUserId(createReviewDTO.getLectureId(), userId)) {
            throw new RuntimeException("User has already reviewed this lecture");
        }
        
        // Get lecture and user entities
        Lecture lecture = lectureRepository.findById(createReviewDTO.getLectureId())
                .orElseThrow(() -> new RuntimeException("Lecture not found with id: " + createReviewDTO.getLectureId()));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Create review entity
        Review review = new Review(lecture, user, createReviewDTO.getRating(), createReviewDTO.getComment());
        
        // Save review
        Review savedReview = reviewRepository.save(review);
        
        // Convert to DTO and return
        return convertToResponseDTO(savedReview);
    }
    
    // Get all reviews for a lecture
    public List<ReviewResponseDTO> getReviewsByLecture(Long lectureId) {
        List<Review> reviews = reviewRepository.findByLectureLectureIdOrderByCreatedAtDesc(lectureId);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Get all reviews by a user
    public List<ReviewResponseDTO> getReviewsByUser(UUID userId) {
        List<Review> reviews = reviewRepository.findByUserUserId(userId);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Get a specific review by ID
    public ReviewResponseDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        return convertToResponseDTO(review);
    }
    
    // Update a review (only by the owner)
    public ReviewResponseDTO updateReview(Long reviewId, CreateReviewDTO updateReviewDTO, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        
        // Check if user is the owner of the review
        if (!review.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to update this review");
        }
        
        // Update review fields
        review.setRating(updateReviewDTO.getRating());
        review.setComment(updateReviewDTO.getComment());
        
        // Save updated review
        Review updatedReview = reviewRepository.save(review);
        
        return convertToResponseDTO(updatedReview);
    }
    
    // Delete a review (only by the owner)
    public void deleteReview(Long reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        
        // Check if user is the owner of the review
        if (!review.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to delete this review");
        }
        
        reviewRepository.delete(review);
    }
    
    // Get reviews by rating
    public List<ReviewResponseDTO> getReviewsByRating(Integer rating) {
        List<Review> reviews = reviewRepository.findByRating(rating);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Get top reviews for a lecture (4-5 stars)
    public List<ReviewResponseDTO> getTopReviewsByLecture(Long lectureId) {
        List<Review> reviews = reviewRepository.findTopReviewsByLectureId(lectureId);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Get latest reviews for a lecture
    public List<ReviewResponseDTO> getLatestReviewsByLecture(Long lectureId, int limit) {
        List<Review> reviews;
        if (limit == 5) {
            reviews = reviewRepository.findTop5ByLectureLectureIdOrderByCreatedAtDesc(lectureId);
        } else {
            reviews = reviewRepository.findByLectureLectureIdOrderByCreatedAtDesc(lectureId);
            if (reviews.size() > limit) {
                reviews = reviews.subList(0, limit);
            }
        }
        
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Calculate average rating for a lecture
    public Double getAverageRatingByLecture(Long lectureId) {
        Double average = reviewRepository.findAverageRatingByLectureId(lectureId);
        return average != null ? Math.round(average * 100.0) / 100.0 : 0.0;
    }
    
    // Get review count for a lecture
    public Long getReviewCountByLecture(Long lectureId) {
        return reviewRepository.countByLectureId(lectureId);
    }
    
    // Get rating statistics for a lecture
    public ReviewStatistics getReviewStatistics(Long lectureId) {
        List<Object[]> statistics = reviewRepository.findRatingStatisticsByLectureId(lectureId);
        
        ReviewStatistics stats = new ReviewStatistics();
        stats.setLectureId(lectureId);
        stats.setTotalReviews(getReviewCountByLecture(lectureId));
        stats.setAverageRating(getAverageRatingByLecture(lectureId));
        
        // Initialize rating counts
        for (int i = 1; i <= 5; i++) {
            stats.getRatingCounts().put(i, 0L);
        }
        
        // Fill actual counts
        for (Object[] stat : statistics) {
            Integer rating = (Integer) stat[0];
            Long count = (Long) stat[1];
            stats.getRatingCounts().put(rating, count);
        }
        
        return stats;
    }
    
    // Search reviews by comment keywords
    public List<ReviewResponseDTO> searchReviewsByKeyword(String keyword) {
        List<Review> reviews = reviewRepository.findByCommentContainingIgnoreCase(keyword);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Check if user can review a lecture (hasn't reviewed it yet)
    public boolean canUserReviewLecture(Long lectureId, UUID userId) {
        return !reviewRepository.existsByLectureLectureIdAndUserUserId(lectureId, userId);
    }
    
    // Get user's review for a specific lecture
    public Optional<ReviewResponseDTO> getUserReviewForLecture(Long lectureId, UUID userId) {
        Optional<Review> review = reviewRepository.findByLectureLectureIdAndUserUserId(lectureId, userId);
        return review.map(this::convertToResponseDTO);
    }
    
    // Convert Review entity to ResponseDTO
    private ReviewResponseDTO convertToResponseDTO(Review review) {
        // Get lecturer name (take first lecturer if multiple exist)
        String lecturerName = review.getLecture().getLecturers().isEmpty() 
            ? "No lecturer assigned" 
            : review.getLecture().getLecturers().iterator().next().getFullName();
            
        ReviewResponseDTO.LectureInfo lectureInfo = new ReviewResponseDTO.LectureInfo(
                review.getLecture().getLectureId(),
                review.getLecture().getTitle(),
                lecturerName
        );
        
        ReviewResponseDTO.UserInfo userInfo = new ReviewResponseDTO.UserInfo(
                review.getUser().getUserId(),
                review.getUser().getEmail(),
                review.getUser().getEmail() // Using email as display name
        );
        
        return new ReviewResponseDTO(
                review.getId(),
                lectureInfo,
                userInfo,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    
    }

    // Get all reviews in the system
    public List<ReviewResponseDTO> getAllReviews() {
    List<Review> reviews = reviewRepository.findAll();
    return reviews.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
}

    
    // Inner class for review statistics
    public static class ReviewStatistics {
        private Long lectureId;
        private Long totalReviews;
        private Double averageRating;
        private java.util.Map<Integer, Long> ratingCounts = new java.util.HashMap<>();
        
        // Getters and setters
        public Long getLectureId() { return lectureId; }
        public void setLectureId(Long lectureId) { this.lectureId = lectureId; }
        
        public Long getTotalReviews() { return totalReviews; }
        public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }
        
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        
        public java.util.Map<Integer, Long> getRatingCounts() { return ratingCounts; }
        public void setRatingCounts(java.util.Map<Integer, Long> ratingCounts) { this.ratingCounts = ratingCounts; }
        
    }

    
}