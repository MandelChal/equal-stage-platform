package com.equal_stage_platform.dev.model;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "0!58$_lecturers")
public class Lecturer {
    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

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

    @ManyToMany
    @JoinTable(
        name = "0!58$_lectures_lecturers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "lecture_id")
    )
    private Set<Lecture> lectures = new HashSet<>();
    
    public Lecturer(CreateLecturerDTO lecturerData) {
        this.userId = lecturerData.getUserId();
        this.firstName = lecturerData.getFirstName();
        this.lastName = lecturerData.getLastName();
        this.bio = lecturerData.getBio();
        this.city = lecturerData.getCity();
        this.email = lecturerData.getEmail();
        this.phone = lecturerData.getPhone();
        this.imageUrl = lecturerData.getImageUrl();
        this.status = LecturerStatus.PENDING; // Default status when created
        this.lectures = new HashSet<>(); // Initialize the set of lectures
        // Set created and updated timestamps
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastUpdatedAt = now;
    }
    public void enrollLecture(Lecture lecture) {
        this.lectures.add(lecture);
    }
}
