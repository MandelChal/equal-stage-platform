package com.equal_stage_platform.dev.dto;

import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;

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

    @Schema(description = "First name of the lecturer", example = "John")
    private String firstName;

    @Schema(description = "Last name of the lecturer", example = "Doe")
    private String lastName;

    @Schema(description = "Biography of the lecturer", example = "Expert in AI and Data Science.")
    private String bio;

    @Schema(description = "City where the lecturer is based", example = "Tel Aviv")
    private String city;

    @Schema(description = "Email address of the lecturer", example = "john.doe@example.com")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Phone number of the lecturer", example = "0501234567")
    @Pattern(regexp = "^05\\d{8}$", message = "Invalid phone number format")
    private String phone;

    @Schema(description = "Profile image URL", example = "https://example.com/image.png")
    @URL(message = "Invalid URL format")
    private String imageUrl;

    // status cannot be PENDING
    @Schema(description = "Status of the lecturer", example = "APPROVED, FREEZE, REJECTED")
    @Pattern(regexp = "^(APPROVED|FREEZE|REJECTED)$", message = "Status must be APPROVED, FREEZE, or REJECTED") //TODO: check if this is correct
    private LecturerStatus status;

    @Schema(description = "Working area of the lecturer", example = "CENTER, NORTH, SOUTH, ONLINE_ONLY")
    private Area workingArea;

    @Schema(description = "List of **MAX 5** external links associated with the lecturer - CAN BE EMPTY BUT NOT NULL", example = "[{\"url\": \"https://example.com\", \"description\": \"Personal website\"}]")
    @Size(max = 5, message = "Maximum of 5 external links allowed")
    @Valid
    private Set<ExternalLinkDTO> externalLinks;

    @Schema(description = "List of **MAX 2** Video links associated with the lecturer - CAN BE EMPTY BUT NOT NULL", example = "[{\"url\": \"https://example.com/video\", \"description\": \"Lecture Video\"}]")
    @Size(max = 2, message = "Maximum of 2 video links allowed")
    @Valid
    private Set<ExternalLinkDTO> videoLinks;
}