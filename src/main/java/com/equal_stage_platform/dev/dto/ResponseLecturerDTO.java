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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLecturerDTO {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String city;
    private String email;
    private String phone;
    private String imageUrl;
    private String createdAt;
    private String lastUpdatedAt;
    private LecturerStatus status;
    private Set<ResponseLectureDTO> lectures;
    public ResponseLecturerDTO(Lecturer lecturer) {
        this.userId = lecturer.getUserId();
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
