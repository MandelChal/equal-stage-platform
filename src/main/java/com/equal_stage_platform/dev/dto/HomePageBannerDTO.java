// package com.equal_stage_platform.dev.dto;
// import io.swagger.v3.oas.annotations.media.Schema;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.util.List;

// import com.equal_stage_platform.dev.model.HomePageBannerObject;

// @Data
// @AllArgsConstructor
// @NoArgsConstructor
// @Schema(description = "DTO for Home Page Banner")
// public class HomePageBannerDTO {
//     @Schema(type = "array", description = "List of image URLs for the banner", example = "[ {\"url\": \"https://example.com/image1.jpg\", \"title\": \"Image 1\", \"objectType\": \"PHOTO\" }, { \"url\": \"https://example.com/image2.jpg\", \"title\": \"Image 2\", \"objectType\": \"PHOTO\" } ]")
//     private List<HomePageBannerObject> imageUrls;
//     @Schema(type = "array", description = "List of video URLs for the banner", example = "[ {\"url\": \"https://example.com/video1.mp4\", \"title\": \"Video 1\", \"objectType\": \"VIDEO\" }, { \"url\": \"https://example.com/video2.mp4\", \"title\": \"Video 2\", \"objectType\": \"VIDEO\" } ]")
//     private List<HomePageBannerObject> videosUrls;
// }