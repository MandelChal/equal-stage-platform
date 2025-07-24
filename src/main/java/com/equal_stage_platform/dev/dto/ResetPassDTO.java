package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "DTO for resetting a user's password")
@Data
public class ResetPassDTO {

    @Schema(description = "Old password of the user", example = "OldPassword123!")
    @NotBlank(message = "Old Password is required")
    private String oldPassword;

    @Schema(description = "New password for the user", example = "NewPassword123!")
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
