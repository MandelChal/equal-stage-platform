package com.equal_stage_platform.dev.dto;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

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
    @Schema(description = "Title of the lecture, **if not need to update - send null**", example = "Introduction to AI")
    private String title;

    @Schema(description = "Description of the lecture, **if not need to update - send null**", example = "A beginner's guide to Artificial Intelligence.")
    private String description;

    @Schema(description = "Duration in minutes, **if not need to update - send null**", example = "90")
    private Integer duration; // Duration in minutes

    @Schema(description = "Price of the lecture, **if not need to update - send null**", example = "100")
    private Integer price;

    @Schema(description = "Status of the lecture, **if not need to update - send null**", example = "ON_AIR, IN_PROGRESS, FREEZE")
    @Pattern(regexp = "^(ON_AIR|IN_PROGRESS|FREEZE)$", message = "Invalid lecture status") //TODO: Check if this is correct
    private String lectureStatus;

    @Schema(description = "Is the lecture online?, **if not need to update - send null**", example = "true")
    private Boolean online;

    @Schema(description = "URL of the lecture image, **if not need to update - send null**", example = "https://example.com/image.png")
    @URL(message = "Invalid URL format")
    private String imageUrl;

    @Schema(description = "List of **MAX 5** External links associated with the lecture, **if not need to update - send null**", example = "[{\"url\": \"https://example.com\", \"description\": \"Lecture Article at N12\"}]")
    @Size(max = 5, message = "Maximum of 5 external links allowed")
    @Valid
    private Set<ExternalLinkDTO> externalLinks;

    @Schema(description = "List of **MAX 2** Video links associated with the lecture, **if not need to update - send null**", example = "[{\"url\": \"https://example.com/video\", \"description\": \"Lecture Video\"}]")
    @Size(max = 2, message = "Maximum of 2 video links allowed")
    @Valid
    private Set<ExternalLinkDTO> videoLinks;

    @Schema(description = "Set of Topics ids associated with the lecture, **if not need to update - send null**", example = "[1,5,10]")
    private Set<Long> topicsIds;

    @Schema(description = "Set of Target Audience ids associated with the lecture, **if not need to update - send null**", example = "[1,3,7]")
    private Set<Long> targetAudiencesIds;
}
