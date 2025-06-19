package com.equal_stage_platform.dev.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleLecturerDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String city;
    private String imageUrl;
}
