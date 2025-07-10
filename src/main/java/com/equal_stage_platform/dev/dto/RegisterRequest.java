package com.equal_stage_platform.dev.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-z]).*$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one special character, and be in English"
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
