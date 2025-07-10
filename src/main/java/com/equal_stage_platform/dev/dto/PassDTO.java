package com.equal_stage_platform.dev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PassDTO {
    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-z]).*$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one special character, and be in English"
    )
    private String pass;

    public PassDTO() {
        // Default constructor
    }
    public PassDTO(String pass) {
        this.pass = pass;
    }

    // getter and setter
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
}