package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
// import jakarta.validation.constraints.Size;

@Schema(description = "DTO for user registration")
public class RegisterRequest {
    
    @Schema(description = "Email address of the user", example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password for the user", example = "Password123!")
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?:{}|<>])[A-Za-z\\d!@#$%^&*(),.?:{}|<>]{12,}$",
        message = "Password must be at least 12 characters long, contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    private String password;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RegisterRequest() {
        // Default constructor
    }
    public RegisterRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
