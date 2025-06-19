package com.equal_stage_platform.dev.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleLectureDTO {
    private Integer lectureId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Boolean isOnline;
    private Integer price;
    private Boolean isAvailable;
}