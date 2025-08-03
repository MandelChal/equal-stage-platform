package com.equal_stage_platform.dev.dto;

import com.equal_stage_platform.dev.model.enums.Area;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLecturerDTO {

    @Schema(description = "First name of the lecturer **if not need to update - send null** ", example = "John")
    private String firstName;

    @Schema(description = "Last name of the lecturer **if not need to update - send null** ", example = "Doe")
    private String lastName;

    @Schema(description = "Biography of the lecturer **if not need to update - send null** ", example = "Expert in AI and Data Science.")
    private String bio;

    @Schema(description = "City where the lecturer is based **if not need to update - send null** ", example = "Tel Aviv")
    private String city;

    @Schema(description = "Email address of the lecturer **if not need to update - send null**", example = "john.doe@example.com")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Phone number of the lecturer **if not need to update - send null** ", example = "0501234567")
    @Pattern(regexp = "^05\\d{8}$", message = "Invalid phone number format")
    private String phone;

    @Schema(description = "Profile image URL **if not need to update - send null** ", example = "https://example.com/image.png")
    @URL(message = "Invalid URL format")
    private String imageUrl;

    // status cannot be PENDING
    @Schema(description = "Status of the lecturer **if not need to update - send null**", example = "APPROVED, FREEZE")
    @Pattern(regexp = "^(APPROVED|FREEZE)$", message = "Status must be APPROVED or FREEZE")
    private String status;

    @Schema(description = "List of Working areas of the lecturer **if not need to update - send null**", example = "[CENTER, NORTH, SOUTH] OR [ONLINE_ONLY]")
    @ValidWorkingAreas
    private Set<Area> workingAreas;

    @Schema(description = "List of **MAX 5** external links associated with the lecturer **if not need to update - send null**", example = "[{\"url\": \"https://example.com\", \"description\": \"Personal website\"}]")
    @Size(max = 5, message = "Maximum of 5 external links allowed")
    @Valid
    private Set<ExternalLinkDTO> externalLinks;

    @Schema(description = "List of **MAX 2** Video links associated with the lecturer **if not need to update - send null**", example = "[{\"url\": \"https://example.com/video\", \"description\": \"Lecture Video\"}]")
    @Size(max = 2, message = "Maximum of 2 video links allowed")
    @Valid
    private Set<ExternalLinkDTO> videoLinks;
}