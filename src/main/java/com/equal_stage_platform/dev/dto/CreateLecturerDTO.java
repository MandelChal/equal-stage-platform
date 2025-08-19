package com.equal_stage_platform.dev.dto;
import java.util.Set;

import org.hibernate.validator.constraints.URL;

import com.equal_stage_platform.dev.model.enums.Area;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating a new lecturer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLecturerDTO {
    @Schema(description = "Biography of the lecturer", example = "Expert in AI and Data Science.")
    @NotBlank(message = "Bio is required")
    private String bio;

    @Schema(description = "Email address of the lecturer, *if null, then the user email will be used*", example = "john.doe@example.com")
    // @NotBlank(message = "Email is required") -> changed to optional
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Profile image URL", example = "https://example.com/image.png")
    @URL(message = "Invalid URL format")
    private String imageUrl;

    @Schema(description = "List of Working areas of a lecturer", example = "[CENTER, NORTH, SOUTH] OR [ONLINE_ONLY]")
    @NotNull(message = "Working Areas are required")
    @ValidWorkingAreas
    private Set<Area> workingAreas;

    @Schema(description = "List of **MAX 5** external links associated with the lecturer", example = "[{\"url\": \"https://example.com\", \"description\": \"Personal website\"}]")
    @Size(max = 5, message = "Maximum of 5 external links allowed")
    @Valid
    private Set<ExternalLinkDTO> externalLinks;

    @Schema(description = "List of **MAX 2** Video links associated with the lecturer", example = "[{\"url\": \"https://example.com/video\", \"description\": \"Lecture Video\"}]")
    @Size(max = 2, message = "Maximum of 2 video links allowed")
    @Valid
    private Set<ExternalLinkDTO> videoLinks;

    @Schema(description = "List of **MAX 5** lecturer topics", example = "[1, 2, 3, 4, 5]")
    @Size(max = 5, message = "Maximum of 5 lecturer topics allowed")
    @NotNull(message = "Lecturer topics are required")
    private Set<Long> lecturerTopicsIds;
}
