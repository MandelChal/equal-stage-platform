package com.equal_stage_platform.dev.dto;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import java.util.Set;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lecturer response DTO containing lecturer details and their lectures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLecturerDTO {
    @Schema(description = "Unique user ID of the lecturer", example = "b3b6a8e2-8c3d-4e2a-9c3d-8e2a8c3d4e2a")
    private UUID userId;
    @Schema(description = "Full name of the lecturer", example = "John Doe")
    private String fullName;
    @Schema(description = "First name of the lecturer", example = "John")
    private String firstName;
    @Schema(description = "Last name of the lecturer", example = "Doe")
    private String lastName;
    @Schema(description = "Biography of the lecturer", example = "Expert in AI and Data Science.")
    private String bio;
    @Schema(description = "City where the lecturer is based", example = "Tel Aviv")
    private String city;
    @Schema(description = "Email address of the lecturer", example = "john.doe@example.com")
    private String email;
    @Schema(description = "Phone number of the lecturer", example = "0501234567")
    private String phone;
    @Schema(description = "Profile image URL", example = "https://example.com/image.png")
    private String imageUrl;
    @Schema(description = "Creation timestamp (ISO 8601)", example = "2024-06-01T12:00:00Z")
    private String createdAt;
    @Schema(description = "Last update timestamp (ISO 8601)", example = "2024-06-02T12:00:00Z")
    private String lastUpdatedAt;
    @Schema(description = "Status of the lecturer", example = "ACTIVE")
    private LecturerStatus status;
    @Schema(description = "Set of lectures given by the lecturer")
    private Set<ResponseLectureDTO> lectures;
    public ResponseLecturerDTO(Lecturer lecturer) {
        this.userId = lecturer.getUserId();
        this.fullName = lecturer.getFullName();
        this.firstName = lecturer.getFirstName();
        this.lastName = lecturer.getLastName();
        this.bio = lecturer.getBio();
        this.city = lecturer.getCity();
        this.email = lecturer.getEmail();
        this.phone = lecturer.getPhone();
        this.imageUrl = lecturer.getImageUrl();
        this.createdAt = lecturer.getCreatedAt().toString();
        this.lastUpdatedAt = lecturer.getLastUpdatedAt().toString();
        this.status = lecturer.getStatus();
        this.lectures = lecturer.getLectures().stream()
            .map(lecture -> new ResponseLectureDTO(lecture))
            .collect(Collectors.toSet());
    }
    
    public ResponseLecturerDTO(Lecturer lecturer, Set<Lecture> lectures) {
        this.userId = lecturer.getUserId();
        this.fullName = lecturer.getFullName();
        this.firstName = lecturer.getFirstName();
        this.lastName = lecturer.getLastName();
        this.bio = lecturer.getBio();
        this.city = lecturer.getCity();
        this.email = lecturer.getEmail();
        this.phone = lecturer.getPhone();
        this.imageUrl = lecturer.getImageUrl();
        this.createdAt = lecturer.getCreatedAt().toString();
        this.lastUpdatedAt = lecturer.getLastUpdatedAt().toString();
        this.status = lecturer.getStatus();
        this.lectures = lectures.stream()
            .map(lecture -> new ResponseLectureDTO(lecture))
            .collect(Collectors.toSet());
    }

}
