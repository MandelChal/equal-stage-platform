package com.equal_stage_platform.dev.dto;
import lombok.Data;
import com.equal_stage_platform.dev.model.LectureStatus;
@Data
public class CreateLectureDTO {
    private Long userId;
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
