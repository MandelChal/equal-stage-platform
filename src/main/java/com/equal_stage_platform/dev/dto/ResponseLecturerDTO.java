// ResponseLecturerDTO.java
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
public class ResponseLecturerDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String city;
    private String email;
    private String phone;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Set<SimpleLectureDTO> lectures;
}
