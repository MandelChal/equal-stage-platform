package com.equal_stage_platform.dev.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private List<ResponseLectureDTO> lectures;
    private List<ResponseLecturerDTO> lecturers;
} 