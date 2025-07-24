package com.equal_stage_platform.dev.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import com.equal_stage_platform.dev.model.enums.bannerObjectType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Home Page Banner Object representing a banner with a URL, title, and type")
@Data
@AllArgsConstructor
public class AddHomePageBanner {
    @Schema(description = "URL", example = "\"https://example.com/banner.jpg\" OR \"https://example.com/video.mp4\"")
    @URL(message = "Invalid URL format")
    private String url;

    @Schema(description = "Title of the banner", example = "\"Welcome to Our Platform\" OR \"Check Out Our New Video\"")
    private String title;

    @Schema(description = "Type of the banner object", example = "\"PHOTO\" or \"VIDEO\"")
    @NotBlank(message = "Object type is required - PHOTO or VIDEO")
    private bannerObjectType objectType;

    @Schema(description = "Order index for the banner", example = "1")
    @NotBlank(message = "Order index is required")
    private Integer orderIndex;
}
