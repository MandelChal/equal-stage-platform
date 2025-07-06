package com.equal_stage_platform.dev.dto;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import com.equal_stage_platform.dev.model.Lecture;

@Data
public class ResponseLectureDTO {
    private List<UUID> userIds;
    private Long lectureId;
    private String title;
    private String description;
    private Integer duration; // Duration in minutes
    private Integer price;
    private String createdAt; // ISO 8601 format
    private String updatedAt; // ISO 8601 format
    private String status; // LectureStatus as a string
    private boolean isOnline;
    private String imageUrl;
    public ResponseLectureDTO() {
        // Default constructor
    }
    public ResponseLectureDTO(Lecture lecture) {
        this.userIds = lecture.getLecturersIds();
        this.lectureId = lecture.getLectureId();
        this.title = lecture.getTitle();
        this.description = lecture.getDescription();
        this.duration = lecture.getDuration();
        this.price = lecture.getPrice();
        this.createdAt = lecture.getCreatedAt().toString();
        this.updatedAt = lecture.getUpdatedAt().toString();
        this.status = lecture.getStatus().name();
        this.isOnline = lecture.isOnline();
        this.imageUrl = lecture.getImageUrl();
    }
}
