package com.equal_stage_platform.dev.model;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.util.TimeUtils;

import java.time.LocalDateTime;
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
@Entity
@Table(name = "0!58$_lecturers")

public class Lecturer {
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

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
    private Set<Topic> topics;
    
    public Lecturer(UUID userId, CreateLecturerDTO lecturerData) {
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
        // Set created and updated timestamps
        LocalDateTime now = TimeUtils.nowInIsrael();
        this.createdAt = now;
        this.lastUpdatedAt = now;
        this.workingAreas = lecturerData.getWorkingAreas() != null ? new HashSet<>(lecturerData.getWorkingAreas()) : new HashSet<>();
        this.lectures = new HashSet<>();
        this.targetAudiences = new HashSet<>();
        this.topics = new HashSet<>();
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
        this.lastUpdatedAt = TimeUtils.nowInIsrael();
        if(lecture!=null){
            this.lectures.add(lecture);
            lecture.enrollLecturer(this);
            this.enrollTopics(lecture.getTopics());
            this.enrollTargetAudiences(lecture.getTargetAudiences());
        }
    }
    
    public Set<Lecture> getLecturesByStatus(LectureStatus status) {
        return this.lectures.stream()
            .filter(lecture -> lecture.getStatus() == status)
            .collect(Collectors.toSet());
    }
        
    public void removeLecture(Lecture lecture) {
        this.lastUpdatedAt = TimeUtils.nowInIsrael();
        if (lecture != null) {
            this.lectures.remove(lecture);
            initTopicsAndTargetAudiences();
        }
    }

    // Full name helper method
    public String getFullName() {
        return firstName + " " + lastName;
    }

    private void enrollTopics(Set<Topic> topics) {
        if (topics != null) {
            if (this.topics == null) {
                this.topics = new HashSet<>();
            }
            this.topics.addAll(topics);
        }
    }
    
    private void enrollTargetAudiences(Set<TargetAudience> targetAudiences) {
        if (targetAudiences != null) {
            if (this.targetAudiences == null) {
                this.targetAudiences = new HashSet<>();
            }
            this.targetAudiences.addAll(targetAudiences);
        }
    }


    public void initTopicsAndTargetAudiences(){
        for (Lecture lecture : this.lectures) {
            if (lecture.getTopics() != null) {
                if (this.topics == null) {
                    this.topics = new HashSet<>();
                }
                this.topics.addAll(lecture.getTopics());
            }
            if (lecture.getTargetAudiences() != null) {
                if (this.targetAudiences == null) {
                    this.targetAudiences = new HashSet<>();
                }
                this.targetAudiences.addAll(lecture.getTargetAudiences());
            }
        }
    }

    public void initTopics(){
        for(Lecture lecture : this.lectures) {
            if(lecture.getTopics() != null) {
                if (this.topics == null) {
                    this.topics = new HashSet<>();
                }
                this.topics.addAll(lecture.getTopics());
            }
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
}