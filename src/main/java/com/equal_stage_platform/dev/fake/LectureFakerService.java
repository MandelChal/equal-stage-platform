package com.equal_stage_platform.dev.fake;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.github.javafaker.Faker;

@Service
public class LectureFakerService {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // הפונקציה המקורית שלך - נשארת בדיוק כמו שהיא
    public Lecture generateFakeLecture() {
        Lecture lecture = new Lecture();
        String title = faker.job().field() + " - " + faker.company().buzzword();
        lecture.setTitle(title);
        String description = generateRandomDescription();
        lecture.setDescription(description);
        boolean isOnline = faker.bool().bool();
        lecture.setIsOnline(isOnline);
        lecture.setLocation(isOnline ? "Zoom / Teams" : faker.address().cityName() + ", " + faker.address().streetName());
        lecture.setIsAvailable(faker.random().nextDouble() > 0.1); // 90% סיכוי לזמין
        LocalDateTime startTime = generateFutureDateTime();
        int durationHours = faker.random().nextInt(1, 3);
        lecture.setStartTime(startTime);
        lecture.setEndTime(startTime.plusHours(durationHours));
        lecture.setImageUrl("https://picsum.photos/800/600?random=" + faker.random().nextInt(1000));
        lecture.setPrice(faker.number().numberBetween(0, 1500));
        lecture.setCreatedAt(LocalDateTime.now());

        return lecture;
    }

    // פונקציה חדשה - יצירת מספר הרצאות ושמירה ב-DB
    public List<Lecture> createFakeLectures(int count) {
        List<Lecture> lectures = new ArrayList<>();
        
        // קבל מרצים זמינים
        List<Lecturer> availableLecturers = lecturerRepository.findAll();
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = generateFakeLecture();
                
                // חבר מרצים אם יש כאלה
                if (!availableLecturers.isEmpty()) {
                    Set<Lecturer> lecturers = selectRandomLecturers(availableLecturers);
                    lecture.setLecturers(lecturers);
                }
                
                lectures.add(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating lecture " + i + ": " + e.getMessage());
            }
        }
        
        try {
            return lectureRepository.saveAll(lectures);
        } catch (Exception e) {
            System.err.println("Error saving lectures: " + e.getMessage());
            throw new RuntimeException("Failed to save lectures: " + e.getMessage());
        }
    }

    // פונקציה חדשה - הרצאות עם נושאים ישראליים
    public List<Lecture> createIsraeliTechLectures(int count) {
        List<Lecture> lectures = new ArrayList<>();
        List<Lecturer> availableLecturers = lecturerRepository.findAll();
        
        // נושאים טכנולוגיים ישראליים
        String[][] israeliTechTopics = {
            {"React ו-Next.js למתחילים", "פיתוח אפליקציות מודרניות בReact"},
            {"Python לניתוח נתונים", "מדעי הנתונים עם Python ו-Pandas"},
            {"אבטחת מידע וסייבר", "הגנה על מערכות ומידע רגיש"},
            {"AWS וקלאוד קומפיוטינג", "תשתיות ענן מתקדמות"},
            {"Machine Learning בפועל", "למידת מכונה עם דוגמאות מעשיות"},
            {"פיתוח אפליקציות מובייל", "Flutter ו-React Native"},
            {"DevOps ו-CI/CD", "אוטומציה ופיתוח מתמשך"},
            {"Angular למתקדמים", "פיתוח SPA מתקדם"},
            {"Node.js ו-Express", "פיתוח שרתים ב-JavaScript"},
            {"מסדי נתונים ו-MongoDB", "NoSQL ופתרונות מסד נתונים"}
        };
        
        String[] israeliCities = {
            "תל אביב", "חיפה", "ירושלים", "באר שבע", "נתניה", 
            "פתח תקווה", "רמת גן", "הרצליה", "כפר סבא", "רעננה"
        };
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = new Lecture();
                
                // בחר נושא אקראי
                String[] topic = israeliTechTopics[random.nextInt(israeliTechTopics.length)];
                lecture.setTitle(topic[0]);
                lecture.setDescription(generateIsraeliDescription(topic[0], topic[1]));
                
                // זמנים
                LocalDateTime startTime = generateFutureDateTime();
                lecture.setStartTime(startTime);
                lecture.setEndTime(startTime.plusHours(faker.random().nextInt(1, 4)));
                
                // מיקום
                boolean isOnline = faker.bool().bool();
                lecture.setIsOnline(isOnline);
                if (isOnline) {
                    lecture.setLocation("הרצאה מקוונת - Zoom");
                } else {
                    String city = israeliCities[random.nextInt(israeliCities.length)];
                    lecture.setLocation(city + " - " + "מרכז הייטק " + city);
                }
                
                lecture.setIsAvailable(faker.random().nextDouble() > 0.15); // 85% זמין
                lecture.setPrice(faker.number().numberBetween(100, 500));
                lecture.setImageUrl("https://picsum.photos/800/600?random=" + faker.random().nextInt(1000));
                lecture.setCreatedAt(LocalDateTime.now());
                
                // חבר מרצים
                if (!availableLecturers.isEmpty()) {
                    Set<Lecturer> lecturers = selectRandomLecturers(availableLecturers);
                    lecture.setLecturers(lecturers);
                }
                
                lectures.add(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating Israeli tech lecture " + i + ": " + e.getMessage());
            }
        }
        
        return lectureRepository.saveAll(lectures);
    }

    // הפונקציה המקורית שלך - נשארת כמו שהיא
    private String generateRandomDescription() {
        return faker.lorem().sentence(10) + "\n\n" +
               "במהלך ההרצאה נעסוק ב-" + faker.hacker().noun() + ", " +
               faker.hacker().verb() + " ונלמד על " + faker.hacker().adjective() + " " + faker.hacker().noun() + "." +
               "\n\nמתאים ל-" + faker.job().seniority() + " בתחום ה-" + faker.job().field() + ".";
    }

    // תיאור ישראלי מתקדם יותר
    private String generateIsraeliDescription(String title, String subtitle) {
        String[] benefits = {
            "שיפור הכישורים המקצועיים", "הכנה לראיונות עבודה", 
            "פיתוח פרויקטים אמיתיים", "למידה מקצועית ומעמיקה"
        };
        
        String[] methods = {
            "הדרכה מעשית", "תרגולים חיים", "פרויקט אמיתי", 
            "דוגמאות מהתעשייה", "Q&A אינטראקטיבי"
        };
        
        return String.format(
            "%s - %s\n\n" +
            "בהרצאה זו נלמד על %s באופן מעמיק ומעשי. " +
            "ההרצאה כוללת %s ו%s. " +
            "מתאים לכל רמות הניסיון - ממתחילים ועד מתקדמים.\n\n" +
            "מטרות ההרצאה:\n" +
            "• %s\n" +
            "• למידה מעשית עם כלים מתקדמים\n" +
            "• הכרת מגמות עדכניות בתחום\n" +
            "• רכישת ידע מעשי ליישום מיידי\n\n" +
            "דרישות קדם: רצון ללמוד וסקרנות טכנולוגית!",
            title, subtitle, title.toLowerCase(),
            methods[random.nextInt(methods.length)],
            methods[random.nextInt(methods.length)],
            benefits[random.nextInt(benefits.length)]
        );
    }

    // הפונקציה המקורית שלך - נשארת כמו שהיא
    private LocalDateTime generateFutureDateTime() {
        int daysFromNow = faker.number().numberBetween(1, 90);
        int hour = faker.number().numberBetween(9, 20);
        int minute = faker.bool().bool() ? 0 : 30;

        return LocalDateTime.now()
                .plusDays(daysFromNow)
                .truncatedTo(ChronoUnit.DAYS)
                .plusHours(hour)
                .plusMinutes(minute);
    }

    // פונקציה חדשה - בחירת מרצים אקראיים
    private Set<Lecturer> selectRandomLecturers(List<Lecturer> availableLecturers) {
        Set<Lecturer> selected = new HashSet<>();
        
        // בחר 1-3 מרצים
        int numberOfLecturers = faker.number().numberBetween(1, 
            Math.min(4, availableLecturers.size() + 1));
        
        while (selected.size() < numberOfLecturers && selected.size() < availableLecturers.size()) {
            Lecturer randomLecturer = availableLecturers.get(
                random.nextInt(availableLecturers.size())
            );
            selected.add(randomLecturer);
        }
        
        return selected;
    }

    // פונקציות נוספות שימושיות
    public Lecture generateSingleLectureWithLecturers() {
        Lecture lecture = generateFakeLecture();
        
        List<Lecturer> availableLecturers = lecturerRepository.findAll();
        if (!availableLecturers.isEmpty()) {
            Set<Lecturer> lecturers = selectRandomLecturers(availableLecturers);
            lecture.setLecturers(lecturers);
        }
        
        return lectureRepository.save(lecture);
    }

    public List<Lecture> generateUpcomingLectures(int count) {
        List<Lecture> lectures = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Lecture lecture = generateFakeLecture();
            
            // וודא שההרצאה עתידית
            LocalDateTime futureTime = LocalDateTime.now().plusDays(faker.number().numberBetween(1, 30));
            lecture.setStartTime(futureTime);
            lecture.setEndTime(futureTime.plusHours(2));
            lecture.setIsAvailable(true); // כל ההרצאות העתידיות זמינות
            
            lectures.add(lecture);
        }
        
        return lectureRepository.saveAll(lectures);
    }
}