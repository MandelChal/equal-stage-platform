package com.equal_stage_platform.dev.dto;

import java.time.LocalDateTime;

public class ReviewResponseDTO {
    
    private Long id;
    private LectureInfo lecture;
    private UserInfo user;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Nested classes for basic info
    public static class LectureInfo {
        private Long id;
        private String title;
        private String lecturerName;
        
        public LectureInfo() {}
        
        public LectureInfo(Long id, String title, String lecturerName) {
            this.id = id;
            this.title = title;
            this.lecturerName = lecturerName;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getLecturerName() { return lecturerName; }
        public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }
    }
    
    public static class UserInfo {
        private java.util.UUID id;
        private String email;
        private String displayName;
        
        public UserInfo() {}
        
        public UserInfo(java.util.UUID id, String email, String displayName) {
            this.id = id;
            this.email = email;
            this.displayName = displayName.isEmpty() ? email : displayName;
        }
        
        // Getters and Setters
        public java.util.UUID getId() { return id; }
        public void setId(java.util.UUID id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getFullName() {
            return displayName != null ? displayName : email;
        }
    }
    
    // Constructors
    public ReviewResponseDTO() {}
    
    public ReviewResponseDTO(Long id, LectureInfo lecture, UserInfo user, 
                           Integer rating, String comment, 
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.lecture = lecture;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LectureInfo getLecture() {
        return lecture;
    }
    
    public void setLecture(LectureInfo lecture) {
        this.lecture = lecture;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "ReviewResponseDTO{" +
                "id=" + id +
                ", lecture=" + (lecture != null ? lecture.getTitle() : null) +
                ", user=" + (user != null ? user.getFullName() : null) +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}