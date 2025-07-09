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

@Data
@AllArgsConstructor
public class CreateLecturerDTO {
    private UUID userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Bio is required")
    private String bio;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^05\\d{8}$", message = "Invalid phone number format")
    private String phone;

    @URL(message = "Invalid URL format")
    private String imageUrl;

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
