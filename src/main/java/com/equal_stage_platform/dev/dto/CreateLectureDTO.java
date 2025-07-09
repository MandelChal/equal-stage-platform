package com.equal_stage_platform.dev.dto;
import java.util.UUID;
import org.hibernate.validator.constraints.URL;

import com.equal_stage_platform.dev.model.enums.LectureStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateLectureDTO {
    private UUID userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Duration is required")
    private Integer duration; // Duration in minutes

    @NotNull(message = "Price is required")
    private Integer price;

    @NotNull(message = "Lecture status is required")
    private LectureStatus lectureStatus;

    private boolean online;

    @URL(message = "Invalid URL format")
    private String imageUrl;
    
    public CreateLectureDTO() {
        // Default constructor
    }
    public CreateLectureDTO(String title, String description, Integer duration, Integer price, LectureStatus lectureStatus, boolean online, String imageUrl) {
        this.userId = null;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.lectureStatus = lectureStatus;
        this.online = online;
        this.imageUrl = imageUrl;
    }
}
