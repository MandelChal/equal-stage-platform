package com.equal_stage_platform.dev.dto;
// CreateLectureDTO.java


import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLectureDTO {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String location;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    private String imageUrl;
    private Boolean isOnline = false;
    private Boolean isAvailable = true;
    
    @NotNull(message = "Price is required")
    private Integer price;
    
    private Set<Long> lecturerIds;
}
