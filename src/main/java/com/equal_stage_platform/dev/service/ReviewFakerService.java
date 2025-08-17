package com.equal_stage_platform.dev.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Review;
import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.ReviewRepository;
import com.equal_stage_platform.dev.repository.UserRepository;

@Service
@Transactional
public class ReviewFakerService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private LectureRepository lectureRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final Random random = new Random();
    
    // Dynamic comment generation components
    private final Map<Integer, List<String>> positiveAdjectives = Map.of(
        5, Arrays.asList("מעולה", "מדהים", "פנטסטי", "יוצא דופן", "מושלם", "brilliant", "outstanding", "exceptional", "amazing", "perfect"),
        4, Arrays.asList("טוב", "מעניין", "איכותי", "מוצלח", "good", "solid", "informative", "well-done", "valuable"),
        3, Arrays.asList("בסדר", "סביר", "אוקיי", "decent", "okay", "fair", "reasonable", "average"),
        2, Arrays.asList("חלש", "לא מרשים", "בינוני", "poor", "weak", "disappointing", "mediocre"),
        1, Arrays.asList("גרוע", "נורא", "איום", "terrible", "awful", "horrible", "dreadful")
    );
    
    private final List<String> learningPhrases = Arrays.asList(
        "למדתי הרבה", "קיבלתי כלים חשובים", "הבנתי נושאים חדשים", "התרחבה לי הידע",
        "learned a lot", "gained valuable insights", "understood new concepts", "expanded my knowledge"
    );
    
    private final List<String> presentationPhrases = Arrays.asList(
        "המרצה הסביר בבהירות", "הצגה מובנת", "סגנון הרצאה מעולה", "המרצה מקצועי",
        "clear explanation", "well-presented", "excellent teaching style", "professional presenter"
    );
    
    private final List<String> contentPhrases = Arrays.asList(
        "תוכן רלוונטי", "חומר מעודכן", "נושאים חשובים", "מידע איכותי",
        "relevant content", "up-to-date material", "important topics", "quality information"
    );
    
    private final List<String> recommendationPhrases = Arrays.asList(
        "ממליץ בחום", "שווה להשתתף", "מומלץ לכולם", "חובה לראות",
        "highly recommend", "worth attending", "recommend to everyone", "must see"
    );
    
    private final List<String> negativeReasons = Arrays.asList(
        "קשה להתרכז", "לא מעניין", "חסר עומק", "משעמם", "לא ברור",
        "hard to focus", "not interesting", "lacks depth", "boring", "unclear"
    );
    
    private final List<String> timeReferences = Arrays.asList(
        "בהרצאה הזו", "במהלך השיעור", "לאורך ההרצאה", "בפגישה",
        "in this lecture", "during the session", "throughout the presentation", "in this talk"
    );
    
    // Generate dynamic comment based on rating
    private String generateDynamicComment(int rating, String lectureTitle) {
        // 30% chance for no comment
        if (random.nextInt(100) < 30) {
            return null;
        }
        
        StringBuilder comment = new StringBuilder();
        
        // Get rating-specific adjectives
        List<String> adjectives = positiveAdjectives.get(rating);
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        
        if (rating >= 4) {
            // Positive comments (4-5 stars)
            switch (random.nextInt(4)) {
                case 0:
                    comment.append("הרצאה ").append(adjective).append("! ")
                           .append(learningPhrases.get(random.nextInt(learningPhrases.size()))).append(". ")
                           .append(recommendationPhrases.get(random.nextInt(recommendationPhrases.size())));
                    break;
                case 1:
                    comment.append(presentationPhrases.get(random.nextInt(presentationPhrases.size()))).append(", ")
                           .append(contentPhrases.get(random.nextInt(contentPhrases.size()))).append(". ")
                           .append("הרצאה ").append(adjective);
                    break;
                case 2:
                    comment.append(adjective.substring(0, 1).toUpperCase()).append(adjective.substring(1))
                           .append(" lecture! ").append(learningPhrases.get(random.nextInt(learningPhrases.size())))
                           .append(". ").append(recommendationPhrases.get(random.nextInt(recommendationPhrases.size())));
                    break;
                case 3:
                    comment.append(timeReferences.get(random.nextInt(timeReferences.size()))).append(" ")
                           .append(learningPhrases.get(random.nextInt(learningPhrases.size()))).append(". ")
                           .append("הרצאה ").append(adjective).append(" ומועילה");
                    break;
            }
        } else if (rating == 3) {
            // Neutral comments (3 stars)
            switch (random.nextInt(3)) {
                case 0:
                    comment.append("הרצאה ").append(adjective).append(", ")
                           .append("יש נקודות מעניינות אבל יכול היה להיות יותר טוב");
                    break;
                case 1:
                    comment.append(adjective.substring(0, 1).toUpperCase()).append(adjective.substring(1))
                           .append(" content, but could be improved in some areas");
                    break;
                case 2:
                    comment.append(contentPhrases.get(random.nextInt(contentPhrases.size())))
                           .append(" אבל ההצגה יכולה להיות יותר מעניינת");
                    break;
            }
        } else {
            // Negative comments (1-2 stars)
            switch (random.nextInt(3)) {
                case 0:
                    comment.append("הרצאה ").append(adjective).append(", ")
                           .append(negativeReasons.get(random.nextInt(negativeReasons.size())))
                           .append(". לא ממליץ");
                    break;
                case 1:
                    comment.append(adjective.substring(0, 1).toUpperCase()).append(adjective.substring(1))
                           .append(" lecture, ").append(negativeReasons.get(random.nextInt(negativeReasons.size())))
                           .append(". Disappointed");
                    break;
                case 2:
                    comment.append("לא התרשמתי, ")
                           .append(negativeReasons.get(random.nextInt(negativeReasons.size())))
                           .append(". בזבוז זמן");
                    break;
            }
        }
        
        // Occasionally add lecture-specific reference
        if (random.nextInt(100) < 20 && lectureTitle != null) {
            comment.append(" (").append(lectureTitle).append(")");
        }
        
        return comment.toString();
    }
    
    // Create fake reviews for all lectures
    public void createFakeReviewsForAllLectures(int minReviewsPerLecture, int maxReviewsPerLecture) {
        List<Lecture> lectures = lectureRepository.findAll();
        List<User> users = userRepository.findAll();
        
        if (users.isEmpty()) {
            throw new RuntimeException("No users found. Please create users first.");
        }
        
        for (Lecture lecture : lectures) {
            int reviewCount = random.nextInt(maxReviewsPerLecture - minReviewsPerLecture + 1) + minReviewsPerLecture;
            createFakeReviewsForLecture(lecture.getLectureId(), reviewCount);
        }
    }
    
    // Create fake reviews for a specific lecture
    public List<Review> createFakeReviewsForLecture(Long lectureId, int numberOfReviews) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found with id: " + lectureId));
        
        List<User> availableUsers = getAvailableUsersForLecture(lectureId);
        
        if (availableUsers.isEmpty()) {
            throw new RuntimeException("No available users to create reviews for lecture: " + lectureId);
        }
        
        List<Review> createdReviews = new ArrayList<>();
        
        // Shuffle users to get random selection
        Collections.shuffle(availableUsers);
        
        int actualReviewsToCreate = Math.min(numberOfReviews, availableUsers.size());
        
        for (int i = 0; i < actualReviewsToCreate; i++) {
            User user = availableUsers.get(i);
            Review review = createSingleFakeReview(lecture, user);
            createdReviews.add(review);
        }
        
        return createdReviews;
    }
    
    // Create a single fake review
    private Review createSingleFakeReview(Lecture lecture, User user) {
        int rating = generateRealisticRating();
        String comment = generateDynamicComment(rating, lecture.getTitle());
        LocalDateTime createdAt = generateRandomPastDate();
        
        Review review = new Review(lecture, user, rating, comment);
        review.setCreatedAt(createdAt);
        
        return reviewRepository.save(review);
    }
    
    // Generate realistic rating distribution (more 4-5 stars than 1-2)
    private int generateRealisticRating() {
        int randomValue = random.nextInt(100);
        
        if (randomValue < 35) return 5;      // 35% chance for 5 stars
        else if (randomValue < 60) return 4; // 25% chance for 4 stars  
        else if (randomValue < 80) return 3; // 20% chance for 3 stars
        else if (randomValue < 95) return 2; // 15% chance for 2 stars
        else return 1;                       // 5% chance for 1 star
    }
    
    // Generate comment based on rating (wrapper for backward compatibility)
    private String generateCommentForRating(int rating) {
        return generateDynamicComment(rating, null);
    }
    
    // Generate random past date (within last 6 months)
    private LocalDateTime generateRandomPastDate() {
        LocalDateTime now = LocalDateTime.now();
        int daysAgo = random.nextInt(180); // 0-180 days ago
        int hoursAgo = random.nextInt(24);
        int minutesAgo = random.nextInt(60);
        
        return now.minusDays(daysAgo).minusHours(hoursAgo).minusMinutes(minutesAgo);
    }
    
    // Get users who haven't reviewed this lecture yet
    private List<User> getAvailableUsersForLecture(Long lectureId) {
        List<User> allUsers = userRepository.findAll();
List<Review> existingReviews = reviewRepository.findByLectureId(lectureId);
        
        Set<UUID> usersWithReviews = existingReviews.stream()
                .map(review -> review.getUser().getUserId())
                .collect(Collectors.toSet());
        
        return allUsers.stream()
                .filter(user -> !usersWithReviews.contains(user.getUserId()))
                .collect(Collectors.toList());
    }
    
    // Create reviews with specific rating distribution
    public List<Review> createReviewsWithDistribution(Long lectureId, 
                                                    int fiveStars, int fourStars, int threeStars, 
                                                    int twoStars, int oneStars) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found with id: " + lectureId));
        
        List<User> availableUsers = getAvailableUsersForLecture(lectureId);
        Collections.shuffle(availableUsers);
        
        List<Review> createdReviews = new ArrayList<>();
        int userIndex = 0;
        
        // Create reviews for each rating level
        userIndex = createReviewsWithSpecificRating(lecture, availableUsers, createdReviews, 5, fiveStars, userIndex);
        userIndex = createReviewsWithSpecificRating(lecture, availableUsers, createdReviews, 4, fourStars, userIndex);
        userIndex = createReviewsWithSpecificRating(lecture, availableUsers, createdReviews, 3, threeStars, userIndex);
        userIndex = createReviewsWithSpecificRating(lecture, availableUsers, createdReviews, 2, twoStars, userIndex);
        createReviewsWithSpecificRating(lecture, availableUsers, createdReviews, 1, oneStars, userIndex);
        
        return createdReviews;
    }
    
    // Helper method to create reviews with specific rating
    private int createReviewsWithSpecificRating(Lecture lecture, List<User> availableUsers, 
                                              List<Review> createdReviews, int rating, 
                                              int count, int startUserIndex) {
        for (int i = 0; i < count && startUserIndex < availableUsers.size(); i++, startUserIndex++) {
            User user = availableUsers.get(startUserIndex);
            String comment = generateDynamicComment(rating, lecture.getTitle());
            LocalDateTime createdAt = generateRandomPastDate();
            
            Review review = new Review(lecture, user, rating, comment);
            review.setCreatedAt(createdAt);
            
            createdReviews.add(reviewRepository.save(review));
        }
        
        return startUserIndex;
    }
    
    // Delete all fake reviews (cleanup method)
    public void deleteAllReviews() {
        reviewRepository.deleteAll();
    }
    
    // Delete reviews for specific lecture
    public void deleteReviewsForLecture(Long lectureId) {
        reviewRepository.deleteByLectureId(lectureId);
    }
    
    // Get statistics about current reviews
    public ReviewGenerationStats getReviewStats() {
        List<Review> allReviews = reviewRepository.findAll();
        Map<Integer, Long> ratingDistribution = allReviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        
        return new ReviewGenerationStats(
                allReviews.size(),
                ratingDistribution,
                (double) allReviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0)
        );
    }
    
    // Statistics class
    public static class ReviewGenerationStats {
        private final int totalReviews;
        private final Map<Integer, Long> ratingDistribution;
        private final double averageRating;
        
        public ReviewGenerationStats(int totalReviews, Map<Integer, Long> ratingDistribution, double averageRating) {
            this.totalReviews = totalReviews;
            this.ratingDistribution = ratingDistribution;
            this.averageRating = averageRating;
        }
        
        // Getters
        public int getTotalReviews() { return totalReviews; }
        public Map<Integer, Long> getRatingDistribution() { return ratingDistribution; }
        public double getAverageRating() { return Math.round(averageRating * 100.0) / 100.0; }
        
        @Override
        public String toString() {
            return String.format("ReviewStats{total=%d, avg=%.2f, dist=%s}", 
                               totalReviews, averageRating, ratingDistribution);
        }
    }
    
}