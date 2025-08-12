package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

/*
 * Single file for all About-Us DTOs
 */
public final class AboutUsDTOs {
    private AboutUsDTOs() {}

    public static record AboutUsDTO(
        @Schema(description = "The text of the about us")
        @NotBlank
        String text,

        @Schema(description = "The image urls of the about us")
        Set<String> imageUrls,

        @Schema(description = "The video urls of the about us")
        Set<String> videoUrls
    ) {}

    public static record UpdateAboutUsDTO(
        @Schema(description = "The text of the about us")
        String text,

        @Schema(description = "The image urls of the about us")
        Set<String> imageUrls,

        @Schema(description = "The video urls of the about us")
        Set<String> videoUrls
    ) {} 
}
