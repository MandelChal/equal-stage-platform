package com.equal_stage_platform.dev.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * מייצג ישות הרצאה במסד הנתונים.
 * ממופה לטבלה 'lectures'.
 */
@Entity
@Table(name = "lectures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {

    /**
     * מזהה ייחודי של ההרצאה.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Integer lectureId;

    /**
     * מזהה המרצה שמעביר את ההרצאה (foreign key ל-lecturers).
     */
    @ManyToOne
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    /**
     * כותרת ההרצאה.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * תיאור ההרצאה.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * מיקום ההרצאה (פיזי או בזום).
     */
    @Column(name = "location")
    private String location;

    /**
     * תאריך ושעת התחלה של ההרצאה.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * תאריך ושעת סיום של ההרצאה.
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * תמונה או פוסטר של ההרצאה (אם יש).
     */
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    /**
     * האם ההרצאה מתקיימת באופן מקוון.
     */
    @Column(name = "is_online")
    private Boolean isOnline;

    /**
     * תאריך יצירה של ההרצאה (לשימוש פנימי).
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * קשר Many-to-Many עם תגיות הרצאה דרך טבלת חיבור בשם tags_lecture.
     */
    @ManyToMany
    @JoinTable(name = "tags_lecture", joinColumns = @JoinColumn(name = "lecture_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    /**
     * פעולה שמתבצעת אוטומטית לפני יצירת ישות חדשה:
     * קובעת את createdAt לזמן נוכחי אם לא הוגדר.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
