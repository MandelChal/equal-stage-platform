package com.equal_stage_platform.dev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Update Topic DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTopicDTO {   
    @Schema(description = "Topic name **CAN BE NULL, NOT EMPTY**", example = "Life Story")
    @NotBlank(message = "Name is required")
    private String name;
    
    @Schema(description = "Topic description **CAN BE NULL, NOT EMPTY**", example = "Learn Java programming fundamentals")
    @NotBlank(message = "Description is required")
    private String description;
}