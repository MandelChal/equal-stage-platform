package com.equal_stage_platform.dev.dto;

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// import org.hibernate.validator.constraints.URL;
// import com.equal_stage_platform.dev.model.enums.BannerObjectType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Home Page Banner Object representing a banner with a URL, title, and type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddHomePageBanner {
    @Schema(description = "URL", example = "\"https://example.com/banner.jpg\" OR \"https://example.com/video.mp4\"")
    // @URL(message = "Invalid URL format")
    private String url;

    @Schema(description = "Title of the banner", example = "\"Welcome to Our Platform\" OR \"Check Out Our New Video\"")
    // @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Type of the banner object", example = "\"PHOTO\" or \"VIDEO\"")
    // @NotNull(message = "Object type is required - PHOTO or VIDEO")
    private String objectType;

    @Schema(description = "Display order for the banner **If null, the banner will be added at the end of the list, else it will be added at the specified display order, the other banners from that display order to the right will be shifted**", example = "1")
    private Integer displayOrder;
}
