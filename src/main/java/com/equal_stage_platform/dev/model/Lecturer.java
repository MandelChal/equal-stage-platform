package com.equal_stage_platform.dev.model;

import jakarta.persistence.*; // עבור אנוטציות JPA
import lombok.Getter; // עבור Lombok @Getter
import lombok.Setter; // עבור Lombok @Setter
import lombok.NoArgsConstructor; // עבור Lombok @NoArgsConstructor
import lombok.AllArgsConstructor; // עבור Lombok @AllArgsConstructor
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// import java.time.LocalDateTime; // עבור טיפול ב-timestamp

/**
 * מייצג ישות מרצה במסד הנתונים.
 * מחלקה זו ממפה לטבלת 'lecturers'.
 */
@Entity // מסמן מחלקה זו כישות JPA, הממפה לטבלת מסד נתונים
@Table(name = "lecturers") // מציין את שם הטבלה המדויק במסד הנתונים
@Getter // אנוטציית Lombok ליצירה אוטומטית של כל מתודות ה-getter
@Setter // אנוטציית Lombok ליצירה אוטומטית של כל מתודות ה-setter
@NoArgsConstructor // אנוטציית Lombok ליצירת בנאי ללא ארגומנטים
@AllArgsConstructor // אנוטציית Lombok ליצירת בנאי עם כל הארגומנטים
public class Lecturer {

    /**
     * המזהה הייחודי של המרצה.
     * זהו המפתח הראשי של טבלת 'lecturers'.
     * זהו גם מפתח זר המפנה ל-'user_info.user_id' בדיאגרמה שלך,
     * אך לשם הפשטות, נתייחס אליו כמפתח ראשי כאן.
     * ביישום אמיתי, ייתכן שיהיה לך יחס מורכב יותר
     * עם טבלת `user_info`, אולי באמצעות `@MapsId` או `@OneToOne`.
     */
    @Id // מסמן שדה זה כמפתח ראשי
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // השתמש בזה אם user_id
    // הוא auto-incremented על ידי ה-DB
    // מכיוון ש-user_id נראה מקושר ל-user_info, ייתכן שהוא לא נוצר אוטומטית
    // על ידי טבלת lecturers עצמה, אלא יורש או מוגדר ידנית.
    // מניחים שהוא מוקצה ידנית או מגיע מ-user_info.
    @Column(name = "user_id") // מציין את שם העמודה במסד הנתונים
    private Integer userId;

    /**
     * שם המרצה.
     */
    @Column(name = "name", nullable = false) // 'nullable = false' אומר שעמודה זו אינה יכולה להיות null
    private String name;

    /**
     * ביוגרפיה או תיאור של המרצה.
     */
    @Column(name = "bio", columnDefinition = "TEXT") // 'columnDefinition = "TEXT"' ממפה לסוג TEXT של PostgreSQL
    private String bio;

    /**
     * העיר בה נמצא המרצה.
     */
    @Column(name = "city")
    private String city;

    /**
     * כתובת הדוא"ל של המרצה.
     */
    @Column(name = "email", unique = true, nullable = false) // 'unique = true' מבטיח שהדוא"ל ייחודי
    private String email;

    /**
     * מספר הטלפון של המרצה.
     */
    @Column(name = "phone")
    private String phone;

    /**
     * כתובת URL לתמונת המרצה.
     */
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    /**
     * חותמת זמן שבה נוצר רשומת המרצה.
     */
    @Column(name = "created_at", nullable = false, updatable = false) // 'updatable = false' אומר ששדה זה לא יעודכן לאחר
                                                                      // יצירה
    private LocalDateTime createdAt;

    // ניתן להוסיף כאן שדות נוספים אם לטבלת 'lecturers' יש עמודות נוספות
    // או אם ברצונך לכלול יחסים לישויות אחרות כמו 'tags_lecturer' או
    // 'lecturer_lecture'.

    // הערה: Lombok יוצר אוטומטית getters, setters, no-arg, ו-all-arg constructors.
    // לכן, אין צורך לכתוב אותם ידנית. זה שומר על קוד נקי.

    // // דוגמה למתודה מותאמת אישית (אופציונלי)
    // @PrePersist // אנוטציה זו מבטיחה שמתודה זו תיקרא לפני שישות חדשה נשמרת
    // (persisted)
    // protected void onCreate() {
    // if (createdAt == null) { // הגדר רק אם לא הוגדר כבר (לדוגמה, על ידי בנאי או
    // setter מפורש)
    // createdAt = LocalDateTime.now();
    // }
    // }

    /**
     * קשר One-to-Many:
     * מרצה יכול להעביר מספר הרצאות.
     * mappedBy = "lecturer" אומר שהקשר נמצא בצד של Lecture.
     */
    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    /**
     * קשר Many-to-Many עם תגיות (tags) דרך טבלת חיבור בשם tags_lecturer.
     */
    @ManyToMany
    @JoinTable(name = "tags_lecturer", joinColumns = @JoinColumn(name = "lecturer_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    /**
     * מתבצע לפני יצירה – מוודא תאריך יצירה אם לא הוגדר.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
