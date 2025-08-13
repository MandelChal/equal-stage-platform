package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;

/*
 * Single file for all About-Us DTOs
 */
public final class ContactUsDTOs {
    private ContactUsDTOs() {}

    public static record ContactUsDTO(
        @Schema(description = "The name of the person")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "The organization of the person **Can be null**")
        //validation is at bottom of the record object
        String organization,

        @Schema(description = "The position of the person **Can be null only if organization is null**")
        //validation is at bottom of the record object
        String position,

        @Schema(description = "The email of the person")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "The phone of the person")
        @Pattern(regexp = "^05\\d{8}$", message = "Invalid phone number format")
        String phone,

        @Schema(description = "The message of the person")
        @NotBlank(message = "Message is required")
        String message
    ) {

        @AssertTrue(message = "Position must not be blank when organization is provided")
        public boolean isPositionRequiredWhenOrganizationProvided() {
            return organization == null || organization.isBlank() || (position != null && !position.isBlank());
        }
    }
}
