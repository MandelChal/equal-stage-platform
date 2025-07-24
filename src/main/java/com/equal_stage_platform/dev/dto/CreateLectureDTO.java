package com.equal_stage_platform.dev.dto;
import java.util.UUID;
import org.hibernate.validator.constraints.URL;

import com.equal_stage_platform.dev.model.enums.LectureStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating a new lecture")
@Data
public class CreateLectureDTO {
    @Schema(description = "User ID of the lecturer creating the lecture", example = "b3b6a8e2-8c3d-4e2a-9c3d-8e2a8c3d4e2a")
    private UUID userId;

    @Schema(description = "Title of the lecture", example = "Introduction to AI")
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Description of the lecture", example = "A beginner's guide to Artificial Intelligence.")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Duration in minutes", example = "90")
    @NotNull(message = "Duration is required")
    private Integer duration; // Duration in minutes

    @Schema(description = "Price of the lecture", example = "100")
    @NotNull(message = "Price is required")
    private Integer price;

    @Schema(description = "Status of the lecture", example = "ACTIVE")
    @NotNull(message = "Lecture status is required")
    private LectureStatus lectureStatus;

    @Schema(description = "Is the lecture online?", example = "true")
    private boolean online;

    @Schema(description = "URL of the lecture image", example = "https://example.com/image.png")
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
