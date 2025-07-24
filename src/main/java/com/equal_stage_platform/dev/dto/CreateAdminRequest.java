package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for creating a new admin user")
public class CreateAdminRequest {
    @Schema(description = "Email address of the admin", example = "admin@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CreateAdminRequest() {
        // Default constructor
    }
    public CreateAdminRequest(String email) {
        this.email = email;
    }
}
