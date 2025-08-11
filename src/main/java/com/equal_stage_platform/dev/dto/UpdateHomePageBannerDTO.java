package com.equal_stage_platform.dev.dto;

import com.equal_stage_platform.dev.model.enums.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Schema(description = "DTO for updating existing Home Page Banner")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHomePageBannerDTO {
    
    @Schema(description = "Banner ID", example = "1")
    @NotNull(message = "Banner ID is required")
    private Integer id;

    @Schema(description = "URL", example = "https://example.com/banner.jpg")
    @URL(message = "Invalid URL format")
    private String url;

    @Schema(description = "Title of the banner", example = "Welcome to Our Platform")
    private String title;

    @Schema(description = "Type of the banner object", example = "PHOTO")
    private MediaType mediaType;
}