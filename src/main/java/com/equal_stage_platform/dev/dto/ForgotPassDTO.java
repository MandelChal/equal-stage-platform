package com.equal_stage_platform.dev.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPassDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ForgotPassDTO() {
        // Default constructor
    }
    public ForgotPassDTO(String email) {
        this.email = email;
    }
}
