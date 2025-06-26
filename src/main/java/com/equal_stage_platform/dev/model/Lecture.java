package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lectures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"lectures"})
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "lecture_id", nullable = false, unique = true)
    private Integer lectureId;

    // @ManyToOne
    // @JoinColumn(name = "lecturer_id", nullable = false)
    // private Lecturer lecturer;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "is_available", nullable = true)
    private Boolean isAvailable;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "price", nullable = false)
    private Integer price;


    @ManyToMany(mappedBy = "lectures", fetch = FetchType.LAZY)
    @JsonIgnore      
    private Set<Lecturer> lecturers = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isAvailable == null) {
            isAvailable = true;
        }
        if (isOnline == null) {
            isOnline = false;
        }
    }

    public void addLecturer(Lecturer lecturer) {
        if (lecturer != null) {
            this.lecturers.add(lecturer);
            // lecturer.getLectures().add(this);
        }
    }
    
    public void removeLecturer(Lecturer lecturer) {
        if (lecturer != null) {
            this.lecturers.remove(lecturer);
            // lecturer.getLectures().remove(this);
        }
    }
    
    public void removeAllLecturers() {
        Set<Lecturer> lecturersCopy = new HashSet<>(this.lecturers);
        
        for (Lecturer lecturer : lecturersCopy) {
            this.removeLecturer(lecturer);
        }
    }
    
    public void setLecturers(Set<Lecturer> newLecturers) {
        this.removeAllLecturers();
        
        if (newLecturers != null) {
            for (Lecturer lecturer : newLecturers) {
                this.addLecturer(lecturer);
            }
        }
    }

   }