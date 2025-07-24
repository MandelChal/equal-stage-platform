package com.equal_stage_platform.dev.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Search result DTO containing lists of lectures and lecturers")
public class SearchResultDTO {
    @Schema(description = "List of lectures matching the search")
    private List<ResponseLectureDTO> lectures;
    @Schema(description = "List of lecturers matching the search")
    private List<ResponseLecturerDTO> lecturers;
} 