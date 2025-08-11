package com.equal_stage_platform.dev.dto;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Data;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.LectureTopic;
import com.equal_stage_platform.dev.model.TargetAudience;
import com.equal_stage_platform.dev.model.enums.Area;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lecture response DTO containing lecture details")
@Data
public class ResponseLectureDTO {
    @Schema(description = "List of user UUIDs associated with the lecture", example = "['b3b6a8e2-8c3d-4e2a-9c3d-8e2a8c3d4e2a', 'c4d5e6f7-8g9h-0i1j-2k3l-4m5n6o7p8q9r']")
    private List<UUID> userIds;

    @Schema(description = "Unique ID of the lecture", example = "123")
    private Long lectureId;

    @Schema(description = "Is the lecture approved?", example = "\"true\" OR \"false\"")
    private boolean approved;

    @Schema(description = "Names of the lecturers", example = "['John Doe', 'Jane Smith']")
    private List<String> lecturerName;

    @Schema(description = "Title of the lecture", example = "Introduction to AI")
    private String title;

    @Schema(description = "Description of the lecture", example = "A beginner's guide to Artificial Intelligence.")
    private String description;

    @Schema(description = "Duration in minutes", example = "90")
    private Integer duration;

    @Schema(description = "Price of the lecture", example = "100")
    private Integer price;

    @Schema(description = "Rank of the lecture", example = "3.5")
    private Double rank;

    @Schema(description = "Creation timestamp (ISO 8601)", example = "2024-06-01T12:00:00Z")
    private String createdAt;

    @Schema(description = "Last update timestamp (ISO 8601)", example = "2024-06-02T12:00:00Z")
    private String updatedAt;

    @Schema(description = "Status of the lecture", example = "ACTIVE")
    private String status;

    @Schema(description = "Is the lecture online?", example = "true")
    private boolean online;

    @Schema(description = "URL of the lecture image", example = "https://example.com/image.png")
    private String imageUrl;

    @Schema(description = "Set of external links associated with the lecture", example = "[{\"url\": \"https://example.com\", \"description\": \"Lecture Article at N12\"}]")
    private Set<ExternalLinkDTO> externalLinks;

    @Schema(description = "Set of video links associated with the lecture", example = "[{\"url\": \"https://example.com/video\", \"description\": \"Lecture Video\"}]")
    private Set<ExternalLinkDTO> videoLinks;

    @Schema(description = "Set of Areas where the lecture is available", example = "[CENTER, NORTH, SOUTH] OR [ONLINE_ONLY]")
    private Set<Area> areas;
    
    @Schema(description = "Set of Topic objects associated with the lecture")
    private Set<LectureTopic> topics;

    @Schema(description = "Set of Target Audience objects associated with the lecture")
    private Set<TargetAudience> targetAudiences;
    public ResponseLectureDTO() {
        // Default constructor
    }
    public ResponseLectureDTO(Lecture lecture) {
        this.userIds = lecture.getLecturersIds();
        this.lectureId = lecture.getLectureId();
        this.approved = lecture.isApproved();
        this.lecturerName = lecture.getLecturersNames();
        this.title = lecture.getTitle();
        this.description = lecture.getDescription();
        this.duration = lecture.getDuration();
        this.price = lecture.getPrice();
        this.rank = lecture.getRank();
        this.createdAt = lecture.getCreatedAt().toString();
        this.updatedAt = lecture.getLastUpdatedAt().toString();
        this.status = lecture.getStatus().name();
        this.online = lecture.isOnline();
        this.imageUrl = lecture.getImageUrl();
        this.externalLinks = lecture.getExternalLinks().stream()
            .map(link -> new ExternalLinkDTO(link.getUrl(), link.getDescription()))
            .collect(Collectors.toSet());
        this.videoLinks = lecture.getVideoLinks().stream()
            .map(link -> new ExternalLinkDTO(link.getUrl(), link.getDescription()))
            .collect(Collectors.toSet());
        this.areas = lecture.getWorkingAreas();
        this.topics = lecture.getTopics();
        this.targetAudiences = lecture.getTargetAudiences();
    }
    @Override
    public String toString(){
        return "ResponseLectureDTO\n\t"+
                "lectureId: " + lectureId + "\n\t" +
                "userIds: " + userIds + "\n\t" +
                "approved: " + approved + "\n\t" +
                "lecturerName: " + lecturerName + "\n\t" +
                "title: " + title + "\n\t" +
                "description: " + description + "\n\t" +
                "duration: " + duration + "\n\t" +
                "price: " + price + "\n\t" +
                "createdAt: " + createdAt + "\n\t" +
                "updatedAt: " + updatedAt + "\n\t" +
                "status: " + status + "\n\t" +
                "online: " + online + "\n\t" +
                "imageUrl: " + imageUrl + "\n\t" +
                "externalLinks: " + externalLinks + "\n\t" +
                "videoLinks: " + videoLinks + "\n\t" +
                "areas: " + areas + "\n\t" +
                "topics: " + topics + "\n\t" +
                "targetAudiences: " + targetAudiences;
    }
}
