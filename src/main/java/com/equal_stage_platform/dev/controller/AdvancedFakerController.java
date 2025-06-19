package com.equal_stage_platform.dev.controller;

import com.equal_stage_platform.dev.fake.AdvancedLectureFakerService;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/advanced-faker")
public class AdvancedFakerController {

    @Autowired
    private AdvancedLectureFakerService advancedFakerService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    /**
     * יוצר מערכת מלאה עם יחסים רבים לרבים מתקדמים
     */
    @PostMapping("/create-complete-system")
    @Transactional
    public ResponseEntity<Map<String, Object>> createCompleteSystem(
            @RequestParam(defaultValue = "10") int lecturerCount,
            @RequestParam(defaultValue = "20") int lectureCount) {
        
        try {
            System.out.println("Creating complete system with " + lecturerCount + " lecturers and " + lectureCount + " lectures...");
            
            Map<String, Object> result = advancedFakerService.createCompleteSystemWithRelations(lecturerCount, lectureCount);
            
            if ((Boolean) result.get("success")) {
                System.out.println("Successfully created complete system!");
                System.out.println("- Lecturers: " + result.get("lecturers_created"));
                System.out.println("- Lectures: " + result.get("lectures_created"));
                System.out.println("- Total Relations: " + result.get("total_relations"));
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("Error creating complete system: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating complete system: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * מנתח את היחסים רבים לרבים במערכת
     */
    @GetMapping("/analyze-relations")
    public ResponseEntity<Map<String, Object>> analyzeManyToManyRelations() {
        try {
            System.out.println("Analyzing many-to-many relations...");
            
            Map<String, Object> analysis = advancedFakerService.analyzeManyToManyRelations();
            
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            System.err.println("Error analyzing relations: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error analyzing relations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * יוצר יחסים חדשים בין מרצים והרצאות קיימים
     */
    @PostMapping("/create-new-relations")
    @Transactional
    public ResponseEntity<Map<String, Object>> createNewRelations(@RequestParam(defaultValue = "5") int newRelations) {
        try {
            System.out.println("Creating " + newRelations + " new relations...");
            
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> lecturers = lecturerRepository.findAll();
            
            if (lectures.isEmpty() || lecturers.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "No lectures or lecturers found. Please create some first.");
                return ResponseEntity.badRequest().body(error);
            }
            
            Random random = new Random();
            int relationsCreated = 0;
            
            for (int i = 0; i < newRelations; i++) {
                // בחר הרצאה ומרצה אקראיים
                Lecture randomLecture = lectures.get(random.nextInt(lectures.size()));
                Lecturer randomLecturer = lecturers.get(random.nextInt(lecturers.size()));
                
                // בדוק אם הקשר כבר קיים
                if (!randomLecture.getLecturers().contains(randomLecturer)) {
                    randomLecture.getLecturers().add(randomLecturer);
                    lectureRepository.save(randomLecture);
                    relationsCreated++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Created " + relationsCreated + " new relations");
            response.put("relations_created", relationsCreated);
            response.put("total_relations_now", countAllRelations());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error creating new relations: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating new relations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * מוצא מרצים בודדים (שאין להם הרצאות)
     */
    @GetMapping("/lonely-lecturers")
    public ResponseEntity<Map<String, Object>> findLonelyLecturers() {
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            
            // מצא מרצים שלא מחוברים לאף הרצאה
            List<Lecturer> lonelyLecturers = allLecturers.stream()
                .filter(lecturer -> lectures.stream()
                    .noneMatch(lecture -> lecture.getLecturers().contains(lecturer)))
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("lonely_lecturers_count", lonelyLecturers.size());
            response.put("lonely_lecturers", lonelyLecturers);
            response.put("total_lecturers", allLecturers.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error finding lonely lecturers: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * מוצא הרצאות ללא מרצים
     */
    @GetMapping("/orphan-lectures")
    public ResponseEntity<Map<String, Object>> findOrphanLectures() {
        try {
            List<Lecture> allLectures = lectureRepository.findAll();
            
            List<Lecture> orphanLectures = allLectures.stream()
                .filter(lecture -> lecture.getLecturers() == null || lecture.getLecturers().isEmpty())
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orphan_lectures_count", orphanLectures.size());
            response.put("orphan_lectures", orphanLectures);
            response.put("total_lectures", allLectures.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error finding orphan lectures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * מתקן הרצאות ללא מרצים על ידי הוספת מרצים אקראיים
     */
    @PostMapping("/fix-orphan-lectures")
    @Transactional
    public ResponseEntity<Map<String, Object>> fixOrphanLectures() {
        try {
            List<Lecture> allLectures = lectureRepository.findAll();
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            
            if (allLecturers.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "No lecturers available to assign");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<Lecture> orphanLectures = allLectures.stream()
                .filter(lecture -> lecture.getLecturers() == null || lecture.getLecturers().isEmpty())
                .collect(Collectors.toList());
            
            Random random = new Random();
            int fixed = 0;
            
            for (Lecture lecture : orphanLectures) {
                // הוסף מרצה אקראי
                Lecturer randomLecturer = allLecturers.get(random.nextInt(allLecturers.size()));
                Set<Lecturer> lecturers = new HashSet<>();
                lecturers.add(randomLecturer);
                
                // 30% סיכוי להוסיף מרצה נוסף
                if (random.nextDouble() < 0.3 && allLecturers.size() > 1) {
                    Lecturer secondLecturer;
                    do {
                        secondLecturer = allLecturers.get(random.nextInt(allLecturers.size()));
                    } while (secondLecturer.equals(randomLecturer));
                    lecturers.add(secondLecturer);
                }
                
                lecture.setLecturers(lecturers);
                lectureRepository.save(lecture);
                fixed++;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Fixed " + fixed + " orphan lectures");
            response.put("lectures_fixed", fixed);
            response.put("total_relations_now", countAllRelations());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error fixing orphan lectures: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fixing orphan lectures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * יוצר "super lecture" עם כל המרצים
     */
    @PostMapping("/create-super-lecture")
    @Transactional
    public ResponseEntity<Map<String, Object>> createSuperLecture() {
        try {
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            
            if (allLecturers.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "No lecturers available");
                return ResponseEntity.badRequest().body(error);
            }
            
            Lecture superLecture = new Lecture();
            superLecture.setTitle("כנס המומחים הגדול - כל המרצים ביחד!");
            superLecture.setDescription(
                "אירוע מיוחד ויחיד מסוגו - כל המומחים שלנו ביחד במקום אחד!\n\n" +
                "זוהי הזדמנות נדירה לשמוע מכל המרצים המובילים שלנו בנושאים מגוונים:\n" +
                "• טכנולוגיות חדשניות\n" +
                "• מגמות בתעשייה\n" +
                "• חוויות מהשטח\n" +
                "• Q&A פתוח עם כל המומחים\n\n" +
                "האירוע יכלול הרצאות קצרות, פאנלים, ונטוורקינג.\n" +
                "מתאים לכל המתעניינים בטכנולוגיה!"
            );
            superLecture.setPrice(500); // מחיר מיוחד לאירוע מיוחד
            superLecture.setIsOnline(false);
            superLecture.setLocation("תל אביב - אולם הכנסים הגדול, מרכז עזריאלי");
            superLecture.setIsAvailable(true);
            superLecture.setImageUrl("https://picsum.photos/800/400?random=2000");
            superLecture.setCreatedAt(java.time.LocalDateTime.now());
            
            // זמנים - יום שלם
            java.time.LocalDateTime startTime = java.time.LocalDateTime.now().plusDays(30).withHour(9).withMinute(0);
            superLecture.setStartTime(startTime);
            superLecture.setEndTime(startTime.plusHours(8)); // 8 שעות
            
            // הוסף את כל המרצים
            Set<Lecturer> allLecturersSet = new HashSet<>(allLecturers);
            superLecture.setLecturers(allLecturersSet);
            
            Lecture savedLecture = lectureRepository.save(superLecture);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Super lecture created with all " + allLecturers.size() + " lecturers!");
            response.put("super_lecture", savedLecture);
            response.put("lecturers_count", allLecturers.size());
            response.put("total_relations_now", countAllRelations());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error creating super lecture: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating super lecture: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * מציג מטריצת יחסים (מי מלמד מה)
     */
    @GetMapping("/relations-matrix")
    public ResponseEntity<Map<String, Object>> getRelationsMatrix() {
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> lecturers = lecturerRepository.findAll();
            
            List<Map<String, Object>> matrix = new ArrayList<>();
            
            for (Lecturer lecturer : lecturers) {
                Map<String, Object> lecturerData = new HashMap<>();
                lecturerData.put("lecturer_id", lecturer.getUserId());
                lecturerData.put("lecturer_name", lecturer.getFirstName() + " " + lecturer.getLastName());
                lecturerData.put("lecturer_email", lecturer.getEmail());
                
                List<Map<String, Object>> lecturesData = lectures.stream()
                    .filter(lecture -> lecture.getLecturers().contains(lecturer))
                    .map(lecture -> {
                        Map<String, Object> lectureInfo = new HashMap<>();
                        lectureInfo.put("lecture_id", lecture.getLectureId());
                        lectureInfo.put("lecture_title", lecture.getTitle());
                        lectureInfo.put("lecture_price", lecture.getPrice());
                        lectureInfo.put("start_time", lecture.getStartTime());
                        lectureInfo.put("other_lecturers_count", lecture.getLecturers().size() - 1);
                        return lectureInfo;
                    })
                    .collect(Collectors.toList());
                
                lecturerData.put("lectures", lecturesData);
                lecturerData.put("lectures_count", lecturesData.size());
                matrix.add(lecturerData);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("relations_matrix", matrix);
            response.put("total_lecturers", lecturers.size());
            response.put("total_lectures", lectures.size());
            response.put("total_relations", countAllRelations());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error generating relations matrix: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * מנקה יחסים כפולים (אם יש)
     */
    @PostMapping("/clean-duplicate-relations")
    @Transactional
    public ResponseEntity<Map<String, Object>> cleanDuplicateRelations() {
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            int cleanedRelations = 0;
            
            for (Lecture lecture : lectures) {
                Set<Lecturer> uniqueLecturers = new HashSet<>(lecture.getLecturers());
                if (uniqueLecturers.size() != lecture.getLecturers().size()) {
                    lecture.setLecturers(uniqueLecturers);
                    lectureRepository.save(lecture);
                    cleanedRelations++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cleaned duplicate relations");
            response.put("lectures_cleaned", cleanedRelations);
            response.put("total_relations_now", countAllRelations());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error cleaning duplicate relations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Helper method
    private int countAllRelations() {
        return lectureRepository.findAll().stream()
            .mapToInt(lecture -> lecture.getLecturers().size())
            .sum();
    }
}