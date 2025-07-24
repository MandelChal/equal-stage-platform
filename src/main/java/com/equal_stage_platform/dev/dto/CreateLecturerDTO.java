package com.equal_stage_platform.dev.dto;
import java.util.UUID;

import org.hibernate.validator.constraints.URL;

import com.equal_stage_platform.dev.model.enums.Area;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating a new lecturer")
@Data
@AllArgsConstructor
public class CreateLecturerDTO {
    @Schema(description = "Unique user ID of the lecturer", example = "b3b6a8e2-8c3d-4e2a-9c3d-8e2a8c3d4e2a")
    private UUID userId;

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

    @Schema(description = "Working area of the lecturer", example = "CENTER, NORTH, SOUTH, ONLINE_ONLY")
    @NotNull(message = "Working Area is required")
    private Area workingArea;

    public CreateLecturerDTO() {
        // Default constructor
    }
    public CreateLecturerDTO(String firstName, String lastName, String bio, String city, String email, String phone, String imageUrl, Area workingArea) {
        this.userId = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.city = city;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.workingArea = workingArea;
    }
}
