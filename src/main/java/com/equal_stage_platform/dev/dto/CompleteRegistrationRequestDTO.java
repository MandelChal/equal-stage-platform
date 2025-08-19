package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO for completing the registration of a user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteRegistrationRequestDTO {
    @NotBlank(message = "First name is required") 
    @Schema(description = "First name of the user", example = "John") 
    String firstName;

    @NotBlank(message = "Last name is required") 
    @Schema(description = "Last name of the user", example = "Doe") 
    String lastName;

    @NotBlank(message = "Phone is required") 
    @Pattern(regexp = "^05\\d{8}$", message = "Invalid phone number format")
    String phone;
}
