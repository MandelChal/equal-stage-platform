package com.equal_stage_platform.dev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPassDTO {

    @NotBlank(message = "Old Password is required")
    private String oldPassword;

    @NotBlank(message = "New Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-z]).*$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one special character, and be in English"
    )
    private String newPassword;

    public ResetPassDTO() {
        // Default constructor
    }
    public ResetPassDTO(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

}
