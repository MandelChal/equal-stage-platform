package com.equal_stage_platform.dev.fake;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
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

    public Lecture generateFakeLecture() {
        Lecture lecture = new Lecture();
        String title = generateTitle();
        lecture.setTitle(title);
        String description = generateDescription();
        lecture.setDescription(description);
        lecture.setImageUrl("https://picsum.photos/800/600?random=" + faker.random().nextInt(10000));
        lecture.setDuration(generateDuration());
        lecture.setPrice(generatePrice());
        lecture.setCreatedAt(LocalDateTime.now());
        lecture.setUpdatedAt(LocalDateTime.now());
        lecture.setStatus(getRandomStatus());
        lecture.setOnline(faker.bool().bool());
        return lecture;
    }

    /**
     * Generates a lecture with approved lecturers only
     * Following the flow requirement that only approved lecturers can create lectures
     */
    public Lecture generateLectureWithLecturers() {
        List<Lecturer> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("❌ Cannot create lecture: No APPROVED lecturers found in system! Please approve some lecturers first.");
        }
        
        Lecture lecture = generateFakeLecture();
        Set<Lecturer> selectedLecturers = selectRandomLecturers(approvedLecturers);
        lecture.setLecturers(selectedLecturers);
        
        for (Lecturer lecturer : selectedLecturers) {
            lecturer.enrollLecture(lecture);
        }
        
        return lectureRepository.save(lecture);
    }

    /**
     * Creates multiple lectures with approved lecturers only
     */
    public List<Lecture> createLecturesWithLecturers(int count) {
        List<Lecturer> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("❌ Cannot create " + count + " lectures: No APPROVED lecturers found in system!");
        }
        
        List<Lecture> lectures = new ArrayList<>();
        System.out.println("🎓 Creating " + count + " lectures with APPROVED lecturers only...");
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = generateFakeLecture();
                Set<Lecturer> selectedLecturers = selectRandomLecturers(approvedLecturers);
                lecture.setLecturers(selectedLecturers);
                
                for (Lecturer lecturer : selectedLecturers) {
                    lecturer.enrollLecture(lecture);
                }
                
                lectures.add(lecture);
            } catch (Exception e) {
                System.err.println("⚠️ Error creating lecture " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        List<Lecture> savedLectures = lectureRepository.saveAll(lectures);
        System.out.println("✅ Successfully created " + savedLectures.size() + " lectures with approved lecturers");
        return savedLectures;
    }

    /**
     * Creates Israeli tech lectures with approved lecturers only
     */
    public List<Lecture> createIsraeliTechLectures(int count) {
        List<Lecturer> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("❌ Cannot create Israeli tech lectures: No APPROVED lecturers found!");
        }
        
        List<Lecture> lectures = new ArrayList<>();
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
            {"מסדי נתונים ו-MongoDB", "NoSQL ופתרונות מסד נתונים"},
            {"Docker ו-Kubernetes", "קונטיינרים וניהול מיקרו-שירותים"},
            {"GraphQL ו-APIs מודרניים", "פיתוח APIs יעילים"},
            {"TypeScript למתקדמים", "פיתוח JavaScript מתקדם עם Type Safety"},
            {"Blockchain ו-Web3", "טכנולוגיות מבוזרות חדשניות"},
            {"AI וChatGPT Integration", "שילוב בינה מלאכותית באפליקציות"}
        };
        
        System.out.println("🇮🇱 Creating " + count + " Israeli tech lectures with approved lecturers...");
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = new Lecture();
                String[] topic = israeliTechTopics[random.nextInt(israeliTechTopics.length)];
                lecture.setTitle(topic[0]);
                lecture.setDescription(generateIsraeliDescription(topic[0], topic[1]));
                lecture.setPrice(faker.number().numberBetween(150, 600));
                lecture.setDuration(faker.number().numberBetween(90, 240));
                lecture.setCreatedAt(LocalDateTime.now());
                lecture.setUpdatedAt(LocalDateTime.now());
                lecture.setStatus(LectureStatus.ON_AIR);
                lecture.setOnline(random.nextBoolean());
                lecture.setImageUrl("https://picsum.photos/800/600?tech&random=" + faker.random().nextInt(1000));
                
                Set<Lecturer> selectedLecturers = selectRandomLecturers(approvedLecturers);
                lecture.setLecturers(selectedLecturers);
                
                for (Lecturer lecturer : selectedLecturers) {
                    lecturer.enrollLecture(lecture);
                }
                
                lectures.add(lecture);
            } catch (Exception e) {
                System.err.println("⚠️ Error creating Israeli tech lecture " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        List<Lecture> savedLectures = lectureRepository.saveAll(lectures);
        System.out.println("✅ Created " + savedLectures.size() + " Israeli tech lectures with approved lecturers");
        return savedLectures;
    }

    /**
     * Creates upcoming lectures with approved lecturers only
     */
    public List<Lecture> generateUpcomingLectures(int count) {
        List<Lecturer> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("❌ Cannot create upcoming lectures: No APPROVED lecturers found!");
        }
        
        List<Lecture> lectures = new ArrayList<>();
        System.out.println("📅 Creating " + count + " upcoming lectures with approved lecturers...");
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = generateFakeLecture();
                LocalDateTime futureDate = LocalDateTime.now().plusDays(faker.number().numberBetween(7, 90));
                lecture.setCreatedAt(futureDate);
                lecture.setUpdatedAt(futureDate);
                lecture.setTitle("בקרוב: " + lecture.getTitle());
                lecture.setStatus(LectureStatus.ON_AIR);
                
                Set<Lecturer> selectedLecturers = selectRandomLecturers(approvedLecturers);
                lecture.setLecturers(selectedLecturers);
                
                for (Lecturer lecturer : selectedLecturers) {
                    lecturer.enrollLecture(lecture);
                }
                
                lectures.add(lecture);
            } catch (Exception e) {
                System.err.println("⚠️ Error creating upcoming lecture " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        List<Lecture> savedLectures = lectureRepository.saveAll(lectures);
        System.out.println("✅ Created " + savedLectures.size() + " upcoming lectures with approved lecturers");
        return savedLectures;
    }

    /**
     * Gets only approved lecturers (following the flow requirement)
     */
    private List<Lecturer> getApprovedLecturers() {
        return lecturerRepository.findAll().stream()
            .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
            .collect(Collectors.toList());
    }

    /**
     * Gets lecturers that are visible for search (APPROVED status only)
     */
    public List<Lecturer> getVisibleLecturers() {
        return getApprovedLecturers();
    }

    /**
     * Checks if a lecturer can create lectures
     */
    public boolean canLecturerCreateLectures(Lecturer lecturer) {
        return lecturer.getStatus() == LecturerStatus.APPROVED;
    }

    private Set<Lecturer> selectRandomLecturers(List<Lecturer> availableLecturers) {
        Set<Lecturer> selected = new HashSet<>();
        int numberOfLecturers = faker.number().numberBetween(1, Math.min(4, availableLecturers.size() + 1));
        
        while (selected.size() < numberOfLecturers && selected.size() < availableLecturers.size()) {
            Lecturer randomLecturer = availableLecturers.get(random.nextInt(availableLecturers.size()));
            selected.add(randomLecturer);
        }
        
        return selected;
    }

    private String generateTitle() {
        String[] patterns = {
            "%s - %s למתחילים",
            "מדריך מלא ל-%s",
            "%s בפועל: %s",
            "כל מה שרציתם לדעת על %s",
            "%s מתקדם: %s",
            "סדנת %s מעשית"
        };
        String[] technologies = {
            "Python", "JavaScript", "React", "Java", "Docker", "AWS", 
            "AI", "Data Science", "DevOps", "Cybersecurity", "Mobile Development"
        };
        String[] specializations = {
            "עם דגש על פרויקטים", "לתעשייה", "למפתחים", 
            "לעסקים", "לחברות הזנק", "למתחילים"
        };
        
        String pattern = patterns[random.nextInt(patterns.length)];
        String tech = technologies[random.nextInt(technologies.length)];
        String specialization = specializations[random.nextInt(specializations.length)];
        
        return String.format(pattern, tech, specialization);
    }

    private String generateDescription() {
        return faker.lorem().sentence(10) + "\n\n" +
               "במהלך ההרצאה נעסוק ב-" + faker.hacker().noun() + ", " +
               faker.hacker().verb() + " ונלמד על " + faker.hacker().adjective() + " " + faker.hacker().noun() + "." +
               "\n\nמתאים ל-" + faker.job().seniority() + " בתחום ה-" + faker.job().field() + ".";
    }

    private String generateIsraeliDescription(String title, String subtitle) {
        return String.format(
            "🇮🇱 %s - %s\n\n" +
            "הרצאה מתקדמת המיועדת למפתחים ישראלים.\n" +
            "נלמד על הטכנולוגיות הכי חמות בשוק הישראלי ובעולם.\n\n" +
            "מה נכסה:\n" +
            "• יסודות ומושגים מתקדמים\n" +
            "• טכניקות best practices מהתעשייה\n" +
            "• מקרי בוחן מחברות ישראליות מובילות\n" +
            "• כלים ופתרונות מעשיים\n\n" +
            "💼 מתאים למפתחים בכל הרמות\n" +
            "🚀 דגש על יישום מעשי ופרויקטים\n\n" +
            "ההרצאה מועברת בעברית עם מונחים טכניים באנגלית.",
            title, subtitle
        );
    }

    private int generateDuration() {
        int[] durations = {60, 90, 120, 150, 180, 240};
        return durations[random.nextInt(durations.length)];
    }

    private int generatePrice() {
        return faker.number().numberBetween(0, 1500);
    }

    private LectureStatus getRandomStatus() {
        LectureStatus[] statuses = {
            LectureStatus.ON_AIR,
            LectureStatus.ON_AIR,
            LectureStatus.ON_AIR,
            LectureStatus.IN_PROGRESS,
            LectureStatus.FREEZE
        };
        return statuses[random.nextInt(statuses.length)];
    }

    @Deprecated
    public List<Lecture> createFakeLectures(int count) {
        System.out.println("⚠️ WARNING: Using deprecated createFakeLectures - use createLecturesWithLecturers instead!");
        return createLecturesWithLecturers(count);
    }

    @Deprecated
    public Lecture generateSingleLectureWithLecturers() {
        System.out.println("⚠️ WARNING: Using deprecated generateSingleLectureWithLecturers - use generateLectureWithLecturers instead!");
        return generateLectureWithLecturers();
    }
}