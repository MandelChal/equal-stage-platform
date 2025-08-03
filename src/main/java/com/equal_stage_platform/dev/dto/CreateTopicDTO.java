package com.equal_stage_platform.dev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Topic DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTopicDTO {   
    @Schema(description = "Topic name", example = "Life Story")
    @NotBlank(message = "Name is required")
    @NotNull(message = "Name cannot be null")
    private String name;
    
    @Schema(description = "Topic description", example = "Learn Java programming fundamentals")
    @NotBlank(message = "Description is required")
    @NotNull(message = "Description cannot be null")
    private String description;
}