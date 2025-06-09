package com.equal_stage_platform.dev.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateLecturerDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String city;
    private String email;
    private String phone;
    private String imageUrl;
    public CreateLecturerDTO() {
        // Default constructor
    }
}
