package com.equal_stage_platform.dev.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLectureDTO {
    private Integer lectureId;
    private String title;
    private String description;
    private String location;
    private Boolean isAvailable;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String imageUrl;
    private Boolean isOnline;
    private LocalDateTime createdAt;
    private Integer price;
    private Set<SimpleLecturerDTO> lecturers;
}
