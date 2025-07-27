package com.equal_stage_platform.dev.dto;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

import com.equal_stage_platform.dev.model.enums.LectureStatus;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating an existing lecture")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLectureDTO {
    @Schema(description = "Title of the lecture", example = "Introduction to AI")
    private String title;

    @Schema(description = "Description of the lecture", example = "A beginner's guide to Artificial Intelligence.")
    private String description;

    @Schema(description = "Duration in minutes", example = "90")
    private Integer duration; // Duration in minutes

    @Schema(description = "Price of the lecture", example = "100")
    private Integer price;

    @Schema(description = "Status of the lecture", example = "ACTIVE")
    @Pattern(regexp = "^(ON_AIR|IN_PROGRESS|FREEZE)$", message = "Invalid lecture status") //TODO: Check if this is correct
    private LectureStatus lectureStatus;

    @Schema(description = "Is the lecture online?", example = "true")
    private Boolean online;

    @Schema(description = "URL of the lecture image", example = "https://example.com/image.png")
    @URL(message = "Invalid URL format")
    private String imageUrl;

    @Schema(description = "List of **MAX 5** External links associated with the lecture - CAN BE EMPTY BUT NOT NULL", example = "[{\"url\": \"https://example.com\", \"description\": \"Lecture Article at N12\"}]")
    @Size(max = 5, message = "Maximum of 5 external links allowed")
    @Valid
    private Set<ExternalLinkDTO> externalLinks;

    @Schema(description = "List of **MAX 2** Video links associated with the lecture - CAN BE EMPTY BUT NOT NULL", example = "[{\"url\": \"https://example.com/video\", \"description\": \"Lecture Video\"}]")
    @Size(max = 2, message = "Maximum of 2 video links allowed")
    @Valid
    private Set<ExternalLinkDTO> videoLinks;
}
