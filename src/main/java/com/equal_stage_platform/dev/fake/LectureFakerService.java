package com.equal_stage_platform.dev.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.service.LectureService;
import com.equal_stage_platform.dev.service.LecturerService;
import com.github.javafaker.Faker;


@Service
public class LectureFakerService {

    @Autowired
    private LectureService lectureService;

    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private LecturerFakerService lecturerFakerService;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    public CreateLectureDTO generateFakeLectureData() {
        CreateLectureDTO lecture = new CreateLectureDTO();
        String title = generateTitle();
        lecture.setTitle(title);
        String description = generateDescription();
        lecture.setDescription(description);
        // lecture.setImageUrl("https://picsum.photos/800/600?random=" + faker.random().nextInt(10000));
        lecture.setImageUrl("https://thispersondoesnotexist.com");
        lecture.setDuration(generateDuration());
        lecture.setPrice(generatePrice());
        lecture.setLectureStatus(getRandomStatus());
        lecture.setOnline(faker.bool().bool());
        return lecture;
    }

    /**
     * Generates a lecture with approved lecturers only using existing services
     * Following the flow requirement that only approved lecturers can create lectures
     */
    public ResponseLectureDTO generateLectureWithLecturers() {
        List<ResponseLecturerDTO> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("Cannot create lecture: No APPROVED lecturers found in system! Please approve some lecturers first.");
        }
        
        // Select a random approved lecturer
        ResponseLecturerDTO selectedLecturer = approvedLecturers.get(random.nextInt(approvedLecturers.size()));
        
        // Create lecture data
        CreateLectureDTO lectureData = generateFakeLectureData();
        lectureData.setUserId(selectedLecturer.getUserId());
        
        // Create lecture through LectureService
        ResponseLectureDTO lecture = lectureService.createLecture(lectureData);
        
        System.out.println("🎓 Created lecture: " + lecture.getTitle() + " with approved lecturer: " + selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName());
        
        return lecture;
    }

    /**
     * Creates multiple lectures with approved lecturers only using existing services
     */
    public List<ResponseLectureDTO> createLecturesWithLecturers(int count) {
        List<ResponseLecturerDTO> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("Cannot create " + count + " lectures: No APPROVED lecturers found in system!");
        }
        
        List<ResponseLectureDTO> lectures = new ArrayList<>();
        System.out.println("Creating " + count + " lectures with APPROVED lecturers only...");
        
        for (int i = 0; i < count; i++) {
            try {
                ResponseLectureDTO lecture = generateLectureWithLecturers();
                lectures.add(lecture);
            } catch (Exception e) {
                System.err.println("Error creating lecture " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        System.out.println("Successfully created " + lectures.size() + " lectures with approved lecturers");
        return lectures;
    }

    /**
     * Creates Israeli tech lectures with approved lecturers only using existing services
     */
    public List<ResponseLectureDTO> createIsraeliTechLectures(int count) {
        List<ResponseLecturerDTO> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("Cannot create Israeli tech lectures: No APPROVED lecturers found!");
        }
        
        List<ResponseLectureDTO> lectures = new ArrayList<>();
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
                // Select random approved lecturer
                ResponseLecturerDTO selectedLecturer = approvedLecturers.get(random.nextInt(approvedLecturers.size()));
                
                // Create Israeli tech lecture data
                String[] topic = israeliTechTopics[random.nextInt(israeliTechTopics.length)];
                CreateLectureDTO lectureData = new CreateLectureDTO();
                lectureData.setUserId(selectedLecturer.getUserId());
                lectureData.setTitle(topic[0]);
                lectureData.setDescription(generateIsraeliDescription(topic[0], topic[1]));
                lectureData.setPrice(faker.number().numberBetween(150, 600));
                lectureData.setDuration(faker.number().numberBetween(90, 240));
                lectureData.setLectureStatus(LectureStatus.ON_AIR);
                lectureData.setOnline(random.nextBoolean());
                lectureData.setImageUrl("https://thispersondoesnotexist.com");
                
                // Create lecture through LectureService
                ResponseLectureDTO lecture = lectureService.createLecture(lectureData);
                lectures.add(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating Israeli tech lecture " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        System.out.println("Created " + lectures.size() + " Israeli tech lectures with approved lecturers");
        return lectures;
    }

    /**
     * Creates upcoming lectures with approved lecturers only using existing services
     */
    public List<ResponseLectureDTO> generateUpcomingLectures(int count) {
        List<ResponseLecturerDTO> approvedLecturers = getApprovedLecturers();
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("Cannot create upcoming lectures: No APPROVED lecturers found!");
        }
        
        List<ResponseLectureDTO> lectures = new ArrayList<>();
        System.out.println("Creating " + count + " upcoming lectures with approved lecturers...");
        
        for (int i = 0; i < count; i++) {
            try {
                // Select random approved lecturer
                ResponseLecturerDTO selectedLecturer = approvedLecturers.get(random.nextInt(approvedLecturers.size()));
                
                // Create upcoming lecture data
                CreateLectureDTO lectureData = generateFakeLectureData();
                lectureData.setUserId(selectedLecturer.getUserId());
                lectureData.setTitle("בקרוב: " + lectureData.getTitle());
                lectureData.setLectureStatus(LectureStatus.ON_AIR);
                
                // Create lecture through LectureService
                ResponseLectureDTO lecture = lectureService.createLecture(lectureData);
                lectures.add(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating upcoming lecture " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        System.out.println("Created " + lectures.size() + " upcoming lectures with approved lecturers");
        return lectures;
    }

    /**
     * Gets only approved lecturers using LecturerService
     */
    private List<ResponseLecturerDTO> getApprovedLecturers() {
        try {
            return lecturerService.getLecturersByStatus(LecturerStatus.APPROVED);
        } catch (Exception e) {
            System.err.println("Error getting approved lecturers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gets lecturers that are visible for search (APPROVED status only)
     */
    public List<ResponseLecturerDTO> getVisibleLecturers() {
        return getApprovedLecturers();
    }

    /**
     * Checks if a lecturer can create lectures by checking their status
     */
    public boolean canLecturerCreateLectures(UUID lecturerId) {
        try {
            ResponseLecturerDTO lecturer = lecturerService.getLecturerById(lecturerId, true); // isAdmin = true
            return lecturer.getStatus() == LecturerStatus.APPROVED;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a lecture for a specific lecturer (if approved) using LectureService
     */
    public ResponseLectureDTO createLectureForSpecificLecturer(UUID lecturerId) {
        try {
            ResponseLecturerDTO lecturer = lecturerService.getLecturerById(lecturerId, true);
            
            if (lecturer.getStatus() != LecturerStatus.APPROVED) {
                throw new IllegalStateException("Cannot create lecture: Lecturer is not approved. Status: " + lecturer.getStatus());
            }
            
            CreateLectureDTO lectureData = generateFakeLectureData();
            lectureData.setUserId(lecturerId);
            
            ResponseLectureDTO lecture = lectureService.createLecture(lectureData);
            
            System.out.println("Created lecture for specific lecturer: " + lecturer.getFirstName() + " " + lecturer.getLastName());
            
            return lecture;
            
        } catch (Exception e) {
            System.err.println("Error creating lecture for lecturer " + lecturerId + ": " + e.getMessage());
            throw new RuntimeException("Failed to create lecture for lecturer", e);
        }
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
            "מתאים למפתחים בכל הרמות\n" +
            "דגש על יישום מעשי ופרויקטים\n\n" +
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
            // LectureStatus.DRAFT,
            // LectureStatus.PENDING_REVIEW
        };
        return statuses[random.nextInt(statuses.length)];
    }

    @Deprecated
    public List<ResponseLectureDTO> createFakeLectures(int count) {
        System.out.println("WARNING: Using deprecated createFakeLectures - use createLecturesWithLecturers instead!");
        return createLecturesWithLecturers(count);
    }

    @Deprecated
    public ResponseLectureDTO generateSingleLectureWithLecturers() {
        System.out.println("WARNING: Using deprecated generateSingleLectureWithLecturers - use generateLectureWithLecturers instead!");
        return generateLectureWithLecturers();
    }
}