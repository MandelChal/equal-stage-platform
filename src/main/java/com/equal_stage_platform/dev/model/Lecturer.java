package com.equal_stage_platform.dev.model;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "0!58$_lecturers")

public class Lecturer extends BaseAuditableEntity {
    @Id
    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LecturerStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "a23v%_lecturer_working_areas", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Area> workingAreas;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "54{4fd%_lecturers_external_links")
    private Set<ExternalLink> externalLinks;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "x'7a#_lecturers_video_links")
    private Set<ExternalLink> videoLinks;

    @ManyToMany
    @JoinTable(
        name = "0!58$_lectures_lecturers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "lecture_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<Lecture> lectures;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "0!58$_lecturer_target_audiences",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "target_audience_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<TargetAudience> targetAudiences;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "0!58$_lecturer_topics",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<LecturerTopic> topics;
    
    public Lecturer(UUID userId, CreateLecturerDTO lecturerData, Set<LecturerTopic> topics) {
        this.userId = userId;
        this.firstName = lecturerData.getFirstName();
        this.lastName = lecturerData.getLastName();
        this.fullName = lecturerData.getFirstName() + " " + lecturerData.getLastName();
        this.bio = lecturerData.getBio();
        this.city = lecturerData.getCity();
        this.email = lecturerData.getEmail();
        this.phone = lecturerData.getPhone();
        this.imageUrl = lecturerData.getImageUrl();
        this.status = LecturerStatus.PENDING; // Default status when created
        // Timestamps are now handled automatically by JPA auditing
        this.workingAreas = lecturerData.getWorkingAreas() != null ? new HashSet<>(lecturerData.getWorkingAreas()) : new HashSet<>();
        this.lectures = new HashSet<>(); // will be filled when lecture is enrolled
        this.targetAudiences = new HashSet<>(); // will be filled when lecture is enrolled
        this.enrollLecturerTopics(topics); // helper method to enroll topics
        this.externalLinks = lecturerData.getExternalLinks() == null ? new HashSet<>() :
            lecturerData.getExternalLinks().stream()
                .map(link -> new ExternalLink(link.getUrl(), link.getDescription()))
                .collect(Collectors.toSet());
        this.videoLinks = lecturerData.getVideoLinks() == null ? new HashSet<>() :
            lecturerData.getVideoLinks().stream()
                .map(link -> new ExternalLink(link.getUrl(), link.getDescription()))
                .collect(Collectors.toSet());
    }
    
    public void enrollLecture(Lecture lecture) {
        // lastUpdatedAt is now handled automatically by JPA auditing
        if(lecture!=null){
            this.lectures.add(lecture);
            lecture.enrollLecturer(this);
            this.enrollTargetAudiences(lecture.getTargetAudiences());
        }
    }
    
    public Set<Lecture> getLecturesByStatus(LectureStatus status) {
        return this.lectures.stream()
            .filter(lecture -> lecture.getStatus() == status)
            .collect(Collectors.toSet());
    }
        
    public void removeLecture(Lecture lecture) {
        // lastUpdatedAt is now handled automatically by JPA auditing
        if (lecture != null) {
            this.lectures.remove(lecture);
            initTargetAudiences();
        }
    }

    // Full name helper method
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    private void enrollTargetAudiences(Set<TargetAudience> targetAudiences) {
        if (targetAudiences != null) {
            if (this.targetAudiences == null) {
                this.targetAudiences = new HashSet<>();
            }
            this.targetAudiences.addAll(targetAudiences);
        }
    }

    public void initTargetAudiences(){
        for(Lecture lecture : this.lectures) {
            if(lecture.getTargetAudiences() != null) {
                if (this.targetAudiences == null) {
                    this.targetAudiences = new HashSet<>();
                }
                this.targetAudiences.addAll(lecture.getTargetAudiences());
            }
        }
    }

    public void enrollLecturerTopics(Set<LecturerTopic> topics) {
        if(topics!=null){
            this.topics = new HashSet<>(); // initialize the topics set
            this.topics.addAll(topics);
        }
    }
}