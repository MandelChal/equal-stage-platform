package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;
import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.model.enums.LectureStatus;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "0!58$_lectures")
public class Lecture {

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

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "lectures", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<Lecturer> lecturers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LectureStatus status;

    @Column(name = "isOnline", nullable = false)
    private boolean isOnline;

    public Lecture(CreateLectureDTO lectureData) {
        this.title = lectureData.getTitle();
        this.description = lectureData.getDescription();
        this.duration = lectureData.getDuration();
        this.price = lectureData.getPrice();
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        // this.imageUrl = lectureData.get
        this.status = lectureData.getLectureStatus();
        this.isOnline = lectureData.isOnline();
    }

    public void enrollLecturer(Lecturer lecturer) {
        if (lecturer != null) {
            this.lecturers.add(lecturer);
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
}
