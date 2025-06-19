package com.equal_stage_platform.dev.dto;
import java.util.UUID;

import lombok.Data;
import com.equal_stage_platform.dev.model.Lecture;

@Data
public class ResponseLectureDTO {
    private UUID userId;
    private Long lectureId;
    private String title;
    private String description;
    private Integer duration; // Duration in minutes
    private Integer price;
    private String createdAt; // ISO 8601 format
    private String updatedAt; // ISO 8601 format
    private String status; // LectureStatus as a string
    public ResponseLectureDTO() {
        // Default constructor
    }
    public ResponseLectureDTO(UUID userId, Lecture lecture) {
        this.userId = userId;
        this.lectureId = lecture.getLectureId();
        this.title = lecture.getTitle();
        this.description = lecture.getDescription();
        this.duration = lecture.getDuration();
        this.price = lecture.getPrice();
        this.createdAt = lecture.getCreatedAt().toString();
        this.updatedAt = lecture.getUpdatedAt().toString();
        this.status = lecture.getStatus().name();
    }
}
