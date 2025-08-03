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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating a new lecturer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLecturerDTO {
    @Schema(description = "First name of the lecturer", example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Last name of the lecturer", example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Biography of the lecturer", example = "Expert in AI and Data Science.")
    @NotBlank(message = "Bio is required")
    private String bio;

    @Schema(description = "City where the lecturer is based", example = "Tel Aviv")
    @NotBlank(message = "City is required")
    private String city;

    @Schema(description = "Email address of the lecturer", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Phone number of the lecturer", example = "0501234567")
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^05\\d{8}$", message = "Invalid phone number format")
    private String phone;

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
}
