package com.equal_stage_platform.dev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Target Audience DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTargetAudienceDTO {
    @Schema(description = "Target audience type", example = "Beginners")
    @NotBlank(message = "Type is required")
    private String type;
    
    @Schema(description = "Target audience description", example = "Suitable for beginners with no prior experience")
    @NotBlank(message = "Description is required")
    private String description;
}