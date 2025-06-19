package com.equal_stage_platform.dev.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecturers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"lectures"})
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "city")
    private String city;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "lectures_lecturers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "lecture_id")
    )
    @Builder.Default
    private Set<Lecture> lectures = new HashSet<>();
    
    // Constructor for manual creation
    public Lecturer(String firstName, String lastName, String bio, String city, String email, String phone, String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.city = city;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.lectures = new HashSet<>();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lectures == null) {
            lectures = new HashSet<>();
        }
    }

    // Helper methods for managing bidirectional relationship
    public void addLecture(Lecture lecture) {
        if (lecture != null) {
            this.lectures.add(lecture);
            lecture.getLecturers().add(this);
        }
    }
    
    public void removeLecture(Lecture lecture) {
        if (lecture != null) {
            this.lectures.remove(lecture);
            lecture.getLecturers().remove(this);
        }
    }
    
    public void removeAllLectures() {
        Set<Lecture> lecturesCopy = new HashSet<>(this.lectures);
        for (Lecture lecture : lecturesCopy) {
            this.removeLecture(lecture);
        }
    }
    
    public void setLectures(Set<Lecture> newLectures) {
        this.removeAllLectures();
        if (newLectures != null) {
            for (Lecture lecture : newLectures) {
                this.addLecture(lecture);
            }
        }
    }

    // Enroll lecture method (alias for addLecture)
    public void enrollLecture(Lecture lecture) {
        this.addLecture(lecture);
    }

    // Full name helper method
    public String getFullName() {
        return firstName + " " + lastName;
    }
}