// package com.equal_stage_platform.dev.model;

// import java.time.LocalDateTime;

// import jakarta.persistence.*;
// import lombok.Data;

// @Data
// @Entity
// @Table(name = "0!58$_lecturers")
// public class Lecturer {
//     @Id
//     @Column(name = "user_id", nullable = false, unique = true)
//     private Long userId;

//     @Column(name = "first_name", nullable = false)
//     private String privateName;

//     @Column(name = "last_name", nullable = false)
//     private String lastName;

//     @Column(name = "bio", columnDefinition = "TEXT")
//     private String bio;

//     @Column(name = "city", nullable = false)
//     private String city;

//     @Column(name = "email", nullable = false, unique = true)
//     private String email;

//     @Column(name = "phone", nullable = false, unique = true)
//     private String phone;

//     @Column(name = "image_url", columnDefinition = "TEXT")
//     private LocalDateTime imageUrl;

//     @Column(name = "created_at", nullable = false, updatable = false)
//     private LocalDateTime createdAt;

//     @Column(name = "last_updated_at", nullable = false)
//     private String lastUpdatedAt;

       
// }
