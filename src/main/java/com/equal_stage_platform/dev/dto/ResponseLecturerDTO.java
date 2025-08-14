package com.equal_stage_platform.dev.dto;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import java.util.Set;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.TargetAudience;
import com.equal_stage_platform.dev.model.LecturerTopic;
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
    @Schema(description = "Rank of the lecturer", example = "3.5")
    private Double rank;
    @Schema(description = "Creation timestamp (ISO 8601)", example = "2024-06-01T12:00:00Z")
    private String createdAt;
    @Schema(description = "Last update timestamp (ISO 8601)", example = "2024-06-02T12:00:00Z")
    private String lastUpdatedAt;
    @Schema(description = "Status of the lecturer", example = "ACTIVE")
    private LecturerStatus status;
    @Schema(description = "Set of working areas of the lecturer", example = "[CENTER, NORTH, SOUTH] OR [ONLINE_ONLY]")
    private Set<String> workingAreas; // Assuming working areas are represented as strings for simplicity
    @Schema(description = "Set of lectures given by the lecturer")
    private Set<LectureInfo> lectures;
    @Schema(description = "List of external links associated with the lecturer", example = "[{\"url\": \"https://example.com\", \"description\": \"Personal website\"}]")
    private Set<ExternalLinkDTO> externalLinks;
    @Schema(description = "Set of video links associated with the lecture", example = "[{\"url\": \"https://example.com/video\", \"description\": \"Lecture Video\"}]")
    private Set<ExternalLinkDTO> videoLinks;
    @Schema(description = "Set of Topics related to the lecturer", exampleClasses = LecturerTopic.class, implementation = LecturerTopic.class)
    private Set<LecturerTopic> topics;
    @Schema(description = "Set of Target Audiences related to the lecturer", exampleClasses = TargetAudience.class, implementation = TargetAudience.class)
    private Set<TargetAudience> targetAudiences;
    
    //this constructor is commented out because i want to control lectures that will be send to user  
    // public ResponseLecturerDTO(Lecturer lecturer) {
    //     this.userId = lecturer.getUserId();
    //     this.fullName = lecturer.getFullName();
    //     this.firstName = lecturer.getFirstName();
    //     this.lastName = lecturer.getLastName();
    //     this.bio = lecturer.getBio();
    //     this.city = lecturer.getCity();
    //     this.email = lecturer.getEmail();
    //     this.phone = lecturer.getPhone();
    //     this.imageUrl = lecturer.getImageUrl();
    //     this.createdAt = lecturer.getCreatedAt().toString();
    //     this.lastUpdatedAt = lecturer.getLastUpdatedAt().toString();
    //     this.status = lecturer.getStatus();
    //     this.lectures = new Set<ResponseLectureDTO>();
    // }
    
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
        this.rank = lecturer.getRank();
        this.createdAt = lecturer.getCreatedAt().toString();
        this.lastUpdatedAt = lecturer.getLastUpdatedAt().toString();
        this.status = lecturer.getStatus();
        this.workingAreas = lecturer.getWorkingAreas().stream()
            .map(Area::name)
            .collect(Collectors.toSet());
        this.lectures = lectures.stream()
            .map(lecture -> new LectureInfo(lecture))
            .collect(Collectors.toSet());
        this.externalLinks = lecturer.getExternalLinks().stream()
            .map(link -> new ExternalLinkDTO(link.getUrl(), link.getDescription()))
            .collect(Collectors.toSet());
        this.videoLinks = lecturer.getVideoLinks().stream()
            .map(link -> new ExternalLinkDTO(link.getUrl(), link.getDescription()))
            .collect(Collectors.toSet());
        this.topics = lecturer.getTopics();
        this.targetAudiences = lecturer.getTargetAudiences();
    }

}

