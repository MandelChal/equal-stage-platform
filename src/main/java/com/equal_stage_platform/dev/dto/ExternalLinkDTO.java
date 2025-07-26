package com.equal_stage_platform.dev.dto;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO for external links associated with a lecturer/lecture")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalLinkDTO {
    @Schema(description = "URL of the external link", example = "https://example.com")
    @URL(message = "Invalid URL format")
    private String url;
    @Schema(description = "Description of the external link", example = "Personal website")
    @NotBlank(message = "Description is required")
    private String description;
}
