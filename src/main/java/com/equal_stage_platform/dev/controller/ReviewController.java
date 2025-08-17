package com.equal_stage_platform.dev.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateReviewDTO;
import com.equal_stage_platform.dev.dto.ReviewResponseDTO;
import com.equal_stage_platform.dev.service.ReviewFakerService;
import com.equal_stage_platform.dev.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ReviewFakerService reviewFakerService;
    
    // ======================== BASIC CRUD OPERATIONS ========================
    
    // Create a new review
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody CreateReviewDTO createReviewDTO,
                                         @RequestParam UUID userId) {
        try {
            ReviewResponseDTO review = reviewService.createReview(createReviewDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get review by ID
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long reviewId) {
        try {
            ReviewResponseDTO review = reviewService.getReviewById(reviewId);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Update a review
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                         @Valid @RequestBody CreateReviewDTO updateReviewDTO,
                                         @RequestParam UUID userId) {
        try {
            ReviewResponseDTO review = reviewService.updateReview(reviewId, updateReviewDTO, userId);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Delete a review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
                                         @RequestParam UUID userId) {
        try {
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ======================== LECTURE-BASED QUERIES ========================
    
    // Get all reviews for a lecture
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByLecture(@PathVariable Long lectureId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByLecture(lectureId);
        return ResponseEntity.ok(reviews);
    }
    
    // Get top reviews for a lecture (4-5 stars)
    @GetMapping("/lecture/{lectureId}/top")
    public ResponseEntity<List<ReviewResponseDTO>> getTopReviewsByLecture(@PathVariable Long lectureId) {
        List<ReviewResponseDTO> reviews = reviewService.getTopReviewsByLecture(lectureId);
        return ResponseEntity.ok(reviews);
    }
    
    // Get latest reviews for a lecture
    @GetMapping("/lecture/{lectureId}/latest")
    public ResponseEntity<List<ReviewResponseDTO>> getLatestReviewsByLecture(
            @PathVariable Long lectureId,
            @RequestParam(defaultValue = "5") int limit) {
        List<ReviewResponseDTO> reviews = reviewService.getLatestReviewsByLecture(lectureId, limit);
        return ResponseEntity.ok(reviews);
    }
    
    // Get average rating for a lecture
    @GetMapping("/lecture/{lectureId}/average")
    public ResponseEntity<Map<String, Object>> getAverageRatingByLecture(@PathVariable Long lectureId) {
        Double averageRating = reviewService.getAverageRatingByLecture(lectureId);
        Long reviewCount = reviewService.getReviewCountByLecture(lectureId);
        
        return ResponseEntity.ok(Map.of(
            "lectureId", lectureId,
            "averageRating", averageRating,
            "totalReviews", reviewCount
        ));
    }
    
    // Get detailed statistics for a lecture
    @GetMapping("/lecture/{lectureId}/statistics")
    public ResponseEntity<ReviewService.ReviewStatistics> getReviewStatistics(@PathVariable Long lectureId) {
        ReviewService.ReviewStatistics stats = reviewService.getReviewStatistics(lectureId);
        return ResponseEntity.ok(stats);
    }
    
    // ======================== USER-BASED QUERIES ========================
    
    // Get all reviews by a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUser(@PathVariable UUID userId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }
    
    // Check if user can review a lecture
    @GetMapping("/lecture/{lectureId}/user/{userId}/can-review")
    public ResponseEntity<Map<String, Boolean>> canUserReviewLecture(
            @PathVariable Long lectureId,
            @PathVariable UUID userId) {
        boolean canReview = reviewService.canUserReviewLecture(lectureId, userId);
        return ResponseEntity.ok(Map.of("canReview", canReview));
    }
    
    // Get user's review for a specific lecture
    @GetMapping("/lecture/{lectureId}/user/{userId}")
    public ResponseEntity<?> getUserReviewForLecture(
            @PathVariable Long lectureId,
            @PathVariable UUID userId) {
        Optional<ReviewResponseDTO> review = reviewService.getUserReviewForLecture(lectureId, userId);
        
        if (review.isPresent()) {
            return ResponseEntity.ok(review.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ======================== FILTERING AND SEARCH ========================
    
    // Get reviews by rating
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByRating(@PathVariable Integer rating) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByRating(rating);
        return ResponseEntity.ok(reviews);
    }
    
    // Search reviews by keyword in comments
    @GetMapping("/search")
    public ResponseEntity<List<ReviewResponseDTO>> searchReviewsByKeyword(@RequestParam String keyword) {
        if (keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ReviewResponseDTO> reviews = reviewService.searchReviewsByKeyword(keyword.trim());
        return ResponseEntity.ok(reviews);
    }
    
    // ======================== FAKER SERVICE ENDPOINTS ========================
    
    // Generate fake reviews for all lectures
    @PostMapping("/fake/generate-all")
    public ResponseEntity<Map<String, Object>> generateFakeReviewsForAllLectures(
            @RequestParam(defaultValue = "3") int minReviewsPerLecture,
            @RequestParam(defaultValue = "10") int maxReviewsPerLecture) {
        try {
            reviewFakerService.createFakeReviewsForAllLectures(minReviewsPerLecture, maxReviewsPerLecture);
            ReviewFakerService.ReviewGenerationStats stats = reviewFakerService.getReviewStats();
            
            return ResponseEntity.ok(Map.of(
                "message", "Fake reviews generated successfully for all lectures",
                "statistics", stats
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Generate fake reviews for a specific lecture
    @PostMapping("/fake/lecture/{lectureId}")
    public ResponseEntity<Map<String, Object>> generateFakeReviewsForLecture(
            @PathVariable Long lectureId,
            @RequestParam(defaultValue = "5") int numberOfReviews) {
        try {
            reviewFakerService.createFakeReviewsForLecture(lectureId, numberOfReviews);
            ReviewService.ReviewStatistics stats = reviewService.getReviewStatistics(lectureId);
            
            return ResponseEntity.ok(Map.of(
                "message", String.format("Generated %d fake reviews for lecture %d", numberOfReviews, lectureId),
                "lectureStatistics", stats
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Generate fake reviews with custom distribution
    @PostMapping("/fake/lecture/{lectureId}/custom")
    public ResponseEntity<Map<String, Object>> generateCustomFakeReviews(
            @PathVariable Long lectureId,
            @RequestParam(defaultValue = "3") int fiveStars,
            @RequestParam(defaultValue = "3") int fourStars,
            @RequestParam(defaultValue = "2") int threeStars,
            @RequestParam(defaultValue = "1") int twoStars,
            @RequestParam(defaultValue = "0") int oneStars) {
        try {
            reviewFakerService.createReviewsWithDistribution(lectureId, fiveStars, fourStars, threeStars, twoStars, oneStars);
            ReviewService.ReviewStatistics stats = reviewService.getReviewStatistics(lectureId);
            
            int totalGenerated = fiveStars + fourStars + threeStars + twoStars + oneStars;
            return ResponseEntity.ok(Map.of(
                "message", String.format("Generated %d custom distributed reviews for lecture %d", totalGenerated, lectureId),
                "distribution", Map.of(
                    "5stars", fiveStars,
                    "4stars", fourStars,
                    "3stars", threeStars,
                    "2stars", twoStars,
                    "1stars", oneStars
                ),
                "lectureStatistics", stats
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get overall review generation statistics
    @GetMapping("/fake/statistics")
    public ResponseEntity<ReviewFakerService.ReviewGenerationStats> getFakeReviewStatistics() {
        ReviewFakerService.ReviewGenerationStats stats = reviewFakerService.getReviewStats();
        return ResponseEntity.ok(stats);
    }
    
    // ======================== CLEANUP OPERATIONS ========================
    
    // Delete all reviews (use with caution!)
    @DeleteMapping("/fake/delete-all")
    public ResponseEntity<Map<String, String>> deleteAllReviews() {
        reviewFakerService.deleteAllReviews();
        return ResponseEntity.ok(Map.of("message", "All reviews deleted successfully"));
    }
    
    // Delete reviews for a specific lecture
    @DeleteMapping("/fake/lecture/{lectureId}")
    public ResponseEntity<Map<String, String>> deleteReviewsForLecture(@PathVariable Long lectureId) {
        reviewFakerService.deleteReviewsForLecture(lectureId);
        return ResponseEntity.ok(Map.of("message", String.format("All reviews for lecture %d deleted successfully", lectureId)));
    }
    
    // ======================== UTILITY ENDPOINTS ========================
    
    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        ReviewFakerService.ReviewGenerationStats stats = reviewFakerService.getReviewStats();
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "totalReviews", stats.getTotalReviews(),
            "averageRating", stats.getAverageRating(),
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
    
    // Get all reviews in the system
    @GetMapping("/all")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
}