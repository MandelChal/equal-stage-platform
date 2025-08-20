package com.equal_stage_platform.dev.model;

import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "0!58$_lectures*")
public class Lecture extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id", nullable = false, unique = true)
    private Long lectureId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "duration", nullable = false)
    private Integer duration; // Duration in minutes

    @Column(name = "rank", nullable = false)
    private Double rank;

    @ManyToMany(mappedBy = "lectures", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<Lecturer> lecturers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LectureStatus status;

    @Column(name = "online", nullable = false)
    private boolean online;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "0!58$_lecture*_external_links")
    private Set<ExternalLink> externalLinks;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "0!58$_lecture*_video_links")
    private Set<ExternalLink> videoLinks;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "0!58$_lecture*working_areas")
    private Set<Area> workingAreas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "0!58$_lecture*target_audiences",
        joinColumns = @JoinColumn(name = "lecture_id"),
        inverseJoinColumns = @JoinColumn(name = "target_audience_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<TargetAudience> targetAudiences;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "0!58$_lecture*topics",
        joinColumns = @JoinColumn(name = "lecture_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<LectureTopic> topics;

    public Lecture(CreateLectureDTO lectureData, Set<TargetAudience> targetAudiences, Set<LectureTopic> topics) {
        this.title = lectureData.getTitle();
        this.description = lectureData.getDescription();
        this.duration = lectureData.getDuration();
        this.rank = 4.0;
        // Timestamps are now handled automatically by JPA auditing
        this.imageUrl = lectureData.getImageUrl();
        this.status = lectureData.getLectureStatus();
        this.online = lectureData.isOnline();
        this.approved = false; // Default to false, can be changed later
        this.externalLinks = lectureData.getExternalLinks() == null ? new HashSet<>() :
            lectureData.getExternalLinks().stream()
                .map(link -> new ExternalLink(link.getUrl(), link.getDescription()))
                .collect(Collectors.toSet());
        this.videoLinks = lectureData.getVideoLinks() == null ? new HashSet<>() :
            lectureData.getVideoLinks().stream()
                .map(link -> new ExternalLink(link.getUrl(), link.getDescription()))
                .collect(Collectors.toSet());
        this.workingAreas = null; // Will be set later
        this.targetAudiences = targetAudiences != null ? new HashSet<>(targetAudiences) : new HashSet<>();
        this.topics = topics != null ? new HashSet<>(topics) : new HashSet<>();
    }

    public void enrollLecturer(Lecturer lecturer) {
        if (lecturer != null) {
            this.lecturers.add(lecturer);
            addWorkingAreas(lecturer.getWorkingAreas());
        }
    }
    
    public void removeLecturer(Lecturer lecturer) {
        if (lecturer != null) {
            this.lecturers.remove(lecturer);
            lecturer.removeLecture(this);
        }
    }
    
    public void removeAllLecturers() {
        HashSet<Lecturer> lecturersCopy = new HashSet<>(this.lecturers);       
        for (Lecturer lecturer : lecturersCopy) {
            this.removeLecturer(lecturer);
        }
    }

    public int getLecturerCount() {
        return this.lecturers.size();
    }

    public boolean searchLecturer(Lecturer lecturer) {
        return this.lecturers.contains(lecturer);
    }

    public List<UUID> getLecturersIds() {
        return this.lecturers.stream()
                .map(Lecturer::getUserId)
                .toList();
    }
    
    public List<String> getLecturersNames() {
        return this.lecturers.stream()
                .map(Lecturer::getFullName)
                .toList();
    }

    public void initWorkingAreas(){
        this.workingAreas = new HashSet<>();
        for (Lecturer lecturer : this.lecturers) {
            this.workingAreas.addAll(lecturer.getWorkingAreas());
        }
    }
    
    public void addWorkingAreas(Set<Area> areas) {
        if (areas != null) {
            if (this.workingAreas == null) {
                this.workingAreas = new HashSet<>();
            }
            this.workingAreas.addAll(areas);
        }
    }

    public void setTopics(Set<LectureTopic> topics) {
        if (topics != null) {
            if (this.topics == null) {
                this.topics = new HashSet<>();
            }
            this.topics.clear();
            this.topics.addAll(topics);
        }
    }

    public void setTargetAudiences(Set<TargetAudience> targetAudiences) {
        if (targetAudiences != null) {
            if (this.targetAudiences == null) {
                this.targetAudiences = new HashSet<>();
            }
            this.targetAudiences.clear();
            this.targetAudiences.addAll(targetAudiences);
            for(Lecturer lecturer : this.lecturers) {
                lecturer.initTargetAudiences();
            }
        }
    }

    public String LectureInfo() {
        return "Lecture: \n\t" + title + "\n" +
                "lectureId: \n\t" + lectureId + "\n" +
                "description: \n\t" + description + "\n" +
                "duration: \n\t" + duration + "\n" +
                "approved: \n\t" + (approved ? "True" : "False");
    }

    public String LectureInfoHebrew() {
        return "הרצאה: \n\t" + title + "\n" +
                "מזהה ההרצאה: \n\t" + lectureId + "\n" +
                "תיאור ההרצאה: \n\t" + description + "\n" +
                "משך ההרצאה: \n\t" + duration + "\n" +
                "סטאטוס: \n\t" + (approved ? "מאושר" : "לא מאושר");
    }
}
