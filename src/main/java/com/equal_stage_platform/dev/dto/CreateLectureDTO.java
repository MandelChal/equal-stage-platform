package com.equal_stage_platform.dev.dto;
import java.util.UUID;

import com.equal_stage_platform.dev.model.enums.LectureStatus;

import lombok.Data;
@Data
public class CreateLectureDTO {
    private UUID userId;
    private Long lectureId;
    private String title;
    private String description;
    private Integer duration; // Duration in minutes
    private Integer price;
    private LectureStatus lectureStatus;
    public CreateLectureDTO() {
        // Default constructor
    }
}
