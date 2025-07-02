package com.equal_stage_platform.dev.dto;
import java.util.UUID;
import org.hibernate.validator.constraints.URL;

import com.equal_stage_platform.dev.model.enums.LectureStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class CreateLectureDTO {
    private UUID userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private Integer duration; // Duration in minutes

    @NotBlank(message = "Price is required")
    private Integer price;

    @NotBlank(message = "Lecture status is required")
    private LectureStatus lectureStatus;

    @NotBlank(message = "Lecture online status is required")
    private boolean isOnline;

    @URL(message = "Invalid URL format")
    private String imageUrl;
    
    public CreateLectureDTO() {
        // Default constructor
    }
    public CreateLectureDTO(String title, String description, Integer duration, Integer price, LectureStatus lectureStatus, boolean isOnline, String imageUrl) {
        this.userId = null;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.lectureStatus = lectureStatus;
        this.isOnline = isOnline;
        this.imageUrl = imageUrl;
    }
}
