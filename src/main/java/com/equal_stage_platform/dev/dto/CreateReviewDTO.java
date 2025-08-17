package com.equal_stage_platform.dev.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReviewDTO {
    
    @NotNull(message = "Lecture ID is required")
    private Long lectureId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
    
    // Constructors
    public CreateReviewDTO() {}
    
    public CreateReviewDTO(Long lectureId, Integer rating, String comment) {
        this.lectureId = lectureId;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public Long getLectureId() {
        return lectureId;
    }
    
    public void setLectureId(Long lectureId) {
        this.lectureId = lectureId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public String toString() {
        return "CreateReviewDTO{" +
                "lectureId=" + lectureId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}