package com.equal_stage_platform.dev.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.equal_stage_platform.dev.service.AdvancedLectureFakerService;

@RestController
@RequestMapping("/api/advanced-faker")
@CrossOrigin(origins = "*")
public class AdvancedFakerController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @Autowired
    private AdvancedLectureFakerService advancedLectureFakerService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    // ========== NEW: Auto-Create Lecturer if Not Exists ==========

    /**
     * יוצר הרצאה עם מרצה - אם המרצה לא קיים, יוצר אותו אוטומטית
     */
    @PostMapping("/lectures/create-with-auto-create-lecturer")
    @Transactional
    public ResponseEntity<Map<String, Object>> createLectureWithAutoCreateLecturer(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "true") Boolean createIfNotExists) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createLectureWithLecturerOrCreate(
                firstName, lastName, email, createIfNotExists);
            
            response.put("success", result.get("success"));
            response.put("action", result.get("action"));
            response.put("message", result.get("message"));
            response.put("lecture", result.get("lecture"));
            response.put("lecturer", result.get("lecturer"));
            response.put("was_lecturer_created", result.get("was_lecturer_created"));
            response.put("total_lectures", lectureRepository.count());
            response.put("total_lecturers", lecturerRepository.count());
            
            if (!(Boolean) result.get("success")) {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * יוצר הרצאה פייק (ללא פרטי מרצה ספציפיים) - יוצר מרצה פייק חדש
     */
    @PostMapping("/lectures/create-fake-with-new-lecturer")
    @Transactional
    public ResponseEntity<Map<String, Object>> createFakeLectureWithNewLecturer() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // קריאה ללא פרטים ספציפיים - יוצר מרצה פייק חדש
            var result = advancedLectureFakerService.createLectureWithLecturerOrCreate(
                null, null, null, true);
            
            response.put("success", true);
            response.put("message", "נוצרה הרצאה פייק עם מרצה פייק חדש!");
            response.put("lecture", result.get("lecture"));
            response.put("lecturer", result.get("lecturer"));
            response.put("action", "created_fake_lecturer_and_lecture");
            response.put("total_lectures", lectureRepository.count());
            response.put("total_lecturers", lecturerRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * יוצר הרצאות מרובות עם יצירה אוטומטית של מרצים
     */
    @PostMapping("/lectures/create-multiple-with-auto-create-lecturers")
    @Transactional
    public ResponseEntity<Map<String, Object>> createMultipleLecturesWithAutoCreateLecturers(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(required = false) String lecturerDetailsJson) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String[]> lecturerDetailsList = null;
            
            // אם סופק JSON עם פרטי מרצים
            if (lecturerDetailsJson != null && !lecturerDetailsJson.trim().isEmpty()) {
                try {
                    // פרסינג פשוט של JSON (ניתן לשדרג לפרסר JSON מלא)
                    lecturerDetailsList = parseSimpleLecturerDetailsJson(lecturerDetailsJson);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "שגיאה בפרסינג פרטי המרצים: " + e.getMessage());
                    response.put("json_example", "[{\"firstName\":\"ישראל\",\"lastName\":\"כהן\",\"email\":\"israel@example.com\"},{\"firstName\":\"שרה\",\"lastName\":\"לוי\"}]");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            var result = advancedLectureFakerService.createMultipleLecturesWithAutoCreateLecturers(
                count, lecturerDetailsList);
            
            response.put("success", true);
            response.put("message", result.get("message"));
            response.put("lectures", result.get("lectures"));
            response.put("lectures_count", result.get("lectures_count"));
            response.put("new_lecturers", result.get("new_lecturers"));
            response.put("new_lecturers_count", result.get("new_lecturers_count"));
            response.put("existing_lecturers_used", result.get("existing_lecturers_used"));
            response.put("existing_lecturers_count", result.get("existing_lecturers_count"));
            response.put("total_lectures_in_db", result.get("total_lectures_in_db"));
            response.put("total_lecturers_in_db", result.get("total_lecturers_in_db"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * חיפוש מרצים לפי פרטים חלקיים
     */
    @GetMapping("/lecturers/search")
    public ResponseEntity<Map<String, Object>> searchLecturers(@RequestParam String searchTerm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "יש לספק מונח חיפוש (שם, אימייל וכד')");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.searchLecturersByDetails(searchTerm);
            
            response.put("success", true);
            response.put("search_term", result.get("search_term"));
            response.put("matches_found", result.get("matches_found"));
            response.put("matching_lecturers", result.get("matching_lecturers"));
            response.put("total_lecturers_in_db", result.get("total_lecturers_in_db"));
            
            if ((Integer) result.get("matches_found") == 0) {
                response.put("message", "לא נמצאו מרצים המתאימים למונח החיפוש: " + searchTerm);
            } else {
                response.put("message", "נמצאו " + result.get("matches_found") + " מרצים");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * יוצר הרצאות לרשימת מרצים ספציפית (עם יצירה אוטומטית)
     */
    @PostMapping("/lectures/create-for-specific-lecturers")
    @Transactional
    public ResponseEntity<Map<String, Object>> createLecturesForSpecificLecturers(
            @RequestParam String lecturerNames) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // פרסינג שמות המרצים (מופרדים בפסיקים)
            String[] names = lecturerNames.split(",");
            List<Lecture> createdLectures = new ArrayList<>();
            List<Lecturer> newLecturers = new ArrayList<>();
            List<Lecturer> existingLecturers = new ArrayList<>();
            
            for (String fullName : names) {
                String trimmedName = fullName.trim();
                if (trimmedName.isEmpty()) continue;
                
                String[] nameParts = trimmedName.split("\\s+");
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                
                var result = advancedLectureFakerService.createLectureWithLecturerOrCreate(
                    firstName, lastName, null, true);
                
                if ((Boolean) result.get("success")) {
                    createdLectures.add((Lecture) result.get("lecture"));
                    
                    Lecturer lecturer = (Lecturer) result.get("lecturer");
                    if ((Boolean) result.get("was_lecturer_created")) {
                        newLecturers.add(lecturer);
                    } else {
                        existingLecturers.add(lecturer);
                    }
                }
            }
            
            response.put("success", true);
            response.put("message", "נוצרו " + createdLectures.size() + " הרצאות עבור המרצים");
            response.put("input_names", lecturerNames);
            response.put("lectures", createdLectures);
            response.put("lectures_count", createdLectures.size());
            response.put("new_lecturers", newLecturers);
            response.put("new_lecturers_count", newLecturers.size());
            response.put("existing_lecturers", existingLecturers);
            response.put("existing_lecturers_count", existingLecturers.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * בדיקה האם מרצה קיים (ללא יצירה)
     */
    @GetMapping("/lecturers/check-exists")
    public ResponseEntity<Map<String, Object>> checkLecturerExists(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if ((firstName == null || firstName.trim().isEmpty()) && 
                (lastName == null || lastName.trim().isEmpty()) && 
                (email == null || email.trim().isEmpty())) {
                response.put("success", false);
                response.put("message", "יש לספק לפחות פרט אחד (שם פרטי, שם משפחה, או אימייל)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // קריאה פנימית לפונקציית החיפוש
            var searchResult = advancedLectureFakerService.searchLecturersByDetails(
                (firstName != null ? firstName : "") + " " + 
                (lastName != null ? lastName : "") + " " + 
                (email != null ? email : ""));
            
            List<Map<String, Object>> matches = 
                (List<Map<String, Object>>) searchResult.get("matching_lecturers");
            
            boolean exists = !matches.isEmpty();
            
            response.put("success", true);
            response.put("exists", exists);
            response.put("search_details", Map.of(
                "firstName", firstName != null ? firstName : "",
                "lastName", lastName != null ? lastName : "",
                "email", email != null ? email : ""
            ));
            
            if (exists) {
                response.put("message", "נמצאו " + matches.size() + " מרצים מתאימים");
                response.put("matching_lecturers", matches);
            } else {
                response.put("message", "לא נמצא מרצה מתאים");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Basic Lecture Creation ==========

    @PostMapping("/lectures/create-single")
    @Transactional
    public ResponseEntity<Map<String, Object>> createSingleLecture() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createSingleLecture();
            
            response.put("success", true);
            response.put("message", "Single lecture created successfully!");
            response.put("lecture", result.get("lecture"));
            response.put("total_lectures", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lectures/create-multiple")
    @Transactional
    public ResponseEntity<Map<String, Object>> createMultipleLectures(@RequestParam(defaultValue = "5") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createMultipleLectures(count);
            
            response.put("success", true);
            response.put("message", "Created " + count + " lectures successfully!");
            response.put("count", count);
            response.put("lectures", result.get("lectures"));
            response.put("total_in_db", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Enhanced Lecture Creation with Existing Lecturers ==========

    /**
     * יוצר הרצאה חדשה ומצרף אליה מרצה קיים לפי ID
     */
    @PostMapping("/lectures/create-with-existing-lecturer")
    @Transactional
    public ResponseEntity<Map<String, Object>> createLectureWithExistingLecturer(
            @RequestParam Long lecturerId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();
            Optional<Lecturer> lecturer = lecturers.stream()
                .filter(l -> l.getUserId().equals(lecturerId))
                .findFirst();
            
            if (lecturer.isEmpty()) {
                response.put("success", false);
                response.put("message", "מרצה עם ID " + lecturerId + " לא נמצא במערכת");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.createLectureWithSpecificLecturer(lecturer.get());
            
            response.put("success", true);
            response.put("message", "הרצאה נוצרה בהצלחה עם המרצה: " + lecturer.get().getFirstName() + " " + lecturer.get().getLastName());
            response.put("lecture", result.get("lecture"));
            response.put("lecturer", lecturer.get());
            response.put("total_lectures", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * יוצר הרצאה פייק למרצה קיים לפי שם (חיפוש חלקי)
     */
    @PostMapping("/lectures/create-fake-for-lecturer-name")
    @Transactional
    public ResponseEntity<Map<String, Object>> createFakeLectureForLecturerName(
            @RequestParam String lecturerName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createFakeLectureForLecturerName(lecturerName);
            
            if (!(Boolean) result.get("success")) {
                response.put("success", false);
                response.put("message", result.get("message"));
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("message", result.get("message"));
            response.put("lecture", result.get("lecture"));
            response.put("lecturer", result.get("lecturer"));
            response.put("total_lectures", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * יוצר הרצאה פייק עם בחירה אוטומטית של מרצה אקראי
     */
    @PostMapping("/lectures/create-fake-random-lecturer")
    @Transactional
    public ResponseEntity<Map<String, Object>> createFakeLectureWithRandomLecturer() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> availableLecturers = lecturerRepository.findAll();
            
            if (availableLecturers.isEmpty()) {
                response.put("success", false);
                response.put("message", "אין מרצים זמינים במערכת. צור מרצים תחילה.");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.createFakeLectureWithRandomLecturer(availableLecturers);
            
            response.put("success", true);
            response.put("message", "הרצאה פייק נוצרה בהצלחה עם מרצה אקראי!");
            response.put("lecture", result.get("lecture"));
            response.put("lecturer", result.get("lecturer"));
            response.put("total_lectures", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * יוצר הרצאות מרובות עם מרצים קיימים (בחירה אקראית)
     */
    @PostMapping("/lectures/create-multiple-with-existing-lecturers")
    @Transactional
    public ResponseEntity<Map<String, Object>> createMultipleLecturesWithExistingLecturers(
            @RequestParam(defaultValue = "5") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> availableLecturers = lecturerRepository.findAll();
            
            if (availableLecturers.isEmpty()) {
                response.put("success", false);
                response.put("message", "אין מרצים זמינים במערכת. צור מרצים תחילה.");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.createMultipleLecturesWithExistingLecturers(count, availableLecturers);
            
            response.put("success", true);
            response.put("message", "נוצרו " + count + " הרצאות בהצלחה עם מרצים קיימים!");
            response.put("count", count);
            response.put("lectures", result.get("lectures"));
            response.put("available_lecturers_count", availableLecturers.size());
            response.put("total_in_db", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * יוצר סדרת הרצאות עבור מרצה ספציפי
     */
    @PostMapping("/lectures/create-series-for-lecturer")
    @Transactional
    public ResponseEntity<Map<String, Object>> createLectureSeriesForLecturer(
            @RequestParam Long lecturerId,
            @RequestParam(defaultValue = "3") int seriesCount,
            @RequestParam(defaultValue = "false") boolean isWorkshopSeries) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();
            Optional<Lecturer> lecturer = lecturers.stream()
                .filter(l -> l.getUserId().equals(lecturerId))
                .findFirst();
            
            if (lecturer.isEmpty()) {
                response.put("success", false);
                response.put("message", "מרצה עם ID " + lecturerId + " לא נמצא במערכת");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.createLectureSeriesForLecturer(
                lecturer.get(), seriesCount, isWorkshopSeries);
            
            response.put("success", true);
            response.put("message", "נוצרה סדרת " + seriesCount + " הרצאות עבור " + 
                lecturer.get().getFirstName() + " " + lecturer.get().getLastName());
            response.put("lecturer", lecturer.get());
            response.put("series_count", seriesCount);
            response.put("lectures", result.get("lectures"));
            response.put("is_workshop_series", isWorkshopSeries);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Existing Lecture Creation Methods ==========

    @PostMapping("/lectures/create-israeli")
    @Transactional
    public ResponseEntity<Map<String, Object>> createIsraeliTechLectures(@RequestParam(defaultValue = "8") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createIsraeliTechLectures(count);
            
            response.put("success", true);
            response.put("message", "Created " + count + " Israeli tech lectures!");
            response.put("count", count);
            response.put("lectures", result.get("lectures"));
            response.put("total_in_db", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lectures/create-upcoming")
    @Transactional
    public ResponseEntity<Map<String, Object>> createUpcomingLectures(@RequestParam(defaultValue = "6") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.generateUpcomingLectures(count);
            
            response.put("success", true);
            response.put("message", "Created " + count + " upcoming lectures!");
            response.put("count", count);
            response.put("lectures", result.get("lectures"));
            response.put("total_in_db", lectureRepository.count());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lectures/create-with-lecturers")
    @Transactional
    public ResponseEntity<Map<String, Object>> createLectureWithLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.generateSingleLectureWithLecturers();
            
            response.put("success", true);
            response.put("message", "Lecture created with lecturers!");
            response.put("lecture", result.get("lecture"));
            response.put("attached_lecturers", result.get("attached_lecturers"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Advanced System Creation ==========

    @PostMapping("/create-complete-system")
    @Transactional
    public ResponseEntity<Map<String, Object>> createCompleteSystem(
            @RequestParam(defaultValue = "10") int lecturerCount,
            @RequestParam(defaultValue = "20") int lectureCount) {
        
        try {
            System.out.println("Creating complete system with " + lecturerCount + " lecturers and " + lectureCount + " lectures...");
            
            Map<String, Object> result = advancedLectureFakerService.createCompleteSystemWithRelations(lecturerCount, lectureCount);
            
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

    @PostMapping("/create-new-relations")
    @Transactional
    public ResponseEntity<Map<String, Object>> createNewRelations(@RequestParam(defaultValue = "5") int newRelations) {
        try {
            System.out.println("Creating " + newRelations + " new relations...");
            
            var result = advancedLectureFakerService.createNewManyToManyRelations(newRelations);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("Error creating new relations: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating new relations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/create-super-lecture")
    @Transactional
    public ResponseEntity<Map<String, Object>> createSuperLecture() {
        try {
            var result = advancedLectureFakerService.createSuperLecture();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("Error creating super lecture: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating super lecture: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/fix-orphan-lectures")
    @Transactional
    public ResponseEntity<Map<String, Object>> fixOrphanLectures() {
        try {
            var result = advancedLectureFakerService.fixOrphanLectures();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("Error fixing orphan lectures: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fixing orphan lectures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ========== Analysis & Information ==========

    @GetMapping("/analyze-relations")
    public ResponseEntity<Map<String, Object>> analyzeManyToManyRelations() {
        try {
            System.out.println("Analyzing many-to-many relations...");
            
            Map<String, Object> analysis = advancedLectureFakerService.analyzeManyToManyRelations();
            
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            System.err.println("Error analyzing relations: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error analyzing relations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/lonely-lecturers")
    public ResponseEntity<Map<String, Object>> findLonelyLecturers() {
        try {
            var result = advancedLectureFakerService.findLonelyLecturers();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error finding lonely lecturers: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/orphan-lectures")
    public ResponseEntity<Map<String, Object>> findOrphanLectures() {
        try {
            var result = advancedLectureFakerService.findOrphanLectures();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error finding orphan lectures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/relations-matrix")
    public ResponseEntity<Map<String, Object>> getRelationsMatrix() {
        try {
            var result = advancedLectureFakerService.getRelationsMatrix();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error generating relations matrix: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/clean-duplicate-relations")
    @Transactional
    public ResponseEntity<Map<String, Object>> cleanDuplicateRelations() {
        try {
            var result = advancedLectureFakerService.cleanDuplicateRelations();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error cleaning duplicate relations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ========== Enhanced Data Retrieval ==========

    /**
     * מחזיר רשימת כל ההרצאות עם פרטים מלאים על המרצים
     */
    @GetMapping("/lectures/all-detailed")
    public ResponseEntity<Map<String, Object>> getAllLecturesDetailed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.getAllLecturesWithDetails(page, size, sortBy, sortDirection);
            
            response.put("success", true);
            response.put("lectures", result.get("lectures"));
            response.put("pagination", result.get("pagination"));
            response.put("statistics", result.get("statistics"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * מחזיר רשימת כל המרצים הזמינים
     */
    @GetMapping("/lecturers/available")
    public ResponseEntity<Map<String, Object>> getAvailableLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();
            
            response.put("success", true);
            response.put("count", lecturers.size());
            response.put("lecturers", lecturers);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * מחזיר רשימת כל ההרצאות הזמינות (פשוט)
     */
    @GetMapping("/lectures/simple")
    public ResponseEntity<Map<String, Object>> getSimpleLectures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            
            // יצירת רשימה פשוטה עם מידע בסיסי
            List<Map<String, Object>> simpleLectures = lectures.stream()
                .map(lecture -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("id", lecture.getLectureId());
                    info.put("title", lecture.getTitle());
                    info.put("price", lecture.getPrice());
                    info.put("duration", lecture.getDuration());
                    info.put("is_online", lecture.isOnline());
                    info.put("status", lecture.getStatus());
                    info.put("created_at", lecture.getCreatedAt());
                    
                    // מידע מרצים פשוט
                    List<String> lecturerNames = lecture.getLecturers().stream()
                        .map(lecturer -> lecturer.getFirstName() + " " + lecturer.getLastName())
                        .collect(Collectors.toList());
                    info.put("lecturers", lecturerNames);
                    info.put("lecturers_count", lecturerNames.size());
                    
                    return info;
                })
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("count", lectures.size());
            response.put("lectures", simpleLectures);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * מחזיר הרצאות של מרצה ספציפי לפי ID
     */
    @GetMapping("/lectures/by-lecturer")
    public ResponseEntity<Map<String, Object>> getLecturesByLecturer(@RequestParam Long lecturerId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();
            Optional<Lecturer> lecturer = lecturers.stream()
                .filter(l -> l.getUserId().equals(lecturerId))
                .findFirst();
            
            if (lecturer.isEmpty()) {
                response.put("success", false);
                response.put("message", "מרצה עם ID " + lecturerId + " לא נמצא במערכת");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.getLecturesByLecturer(lecturer.get());
            
            response.put("success", true);
            response.put("lecturer", lecturer.get());
            response.put("lectures", result.get("lectures"));
            response.put("count", result.get("count"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * מחזיר הרצאות של מרצה ספציפי לפי שם (חיפוש חלקי)
     */
    @GetMapping("/lectures/by-lecturer-name")
    public ResponseEntity<Map<String, Object>> getLecturesByLecturerName(@RequestParam String lecturerName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.getLecturesByLecturerName(lecturerName);
            
            if (!(Boolean) result.get("success")) {
                response.put("success", false);
                response.put("message", result.get("message"));
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("search_term", lecturerName);
            response.put("matching_lecturers", result.get("matching_lecturers"));
            response.put("total_lectures", result.get("total_lectures"));
            response.put("lectures_by_lecturer", result.get("lectures_by_lecturer"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * מחזיר הרצאות של מרצה ספציפי - חיפוש גמיש (לפי ID או שם)
     */
    @GetMapping("/lectures/search-lecturer")
    public ResponseEntity<Map<String, Object>> searchLecturerLectures(
            @RequestParam(required = false) Long lecturerId,
            @RequestParam(required = false) String lecturerName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (lecturerId == null && (lecturerName == null || lecturerName.trim().isEmpty())) {
                response.put("success", false);
                response.put("message", "יש לספק ID מרצה או שם מרצה");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> result;
            
            if (lecturerId != null) {
                // חיפוש לפי ID
                List<Lecturer> lecturers = lecturerRepository.findAll();
                Optional<Lecturer> lecturer = lecturers.stream()
                    .filter(l -> l.getUserId().equals(lecturerId))
                    .findFirst();
                
                if (lecturer.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "מרצה עם ID " + lecturerId + " לא נמצא במערכת");
                    return ResponseEntity.badRequest().body(response);
                }
                
                result = advancedLectureFakerService.getLecturesByLecturer(lecturer.get());
                response.put("search_type", "by_id");
                response.put("lecturer", lecturer.get());
                
            } else {
                // חיפוש לפי שם
                result = advancedLectureFakerService.getLecturesByLecturerName(lecturerName);
                response.put("search_type", "by_name");
                response.put("search_term", lecturerName);
                
                if (!(Boolean) result.get("success")) {
                    response.put("success", false);
                    response.put("message", result.get("message"));
                    return ResponseEntity.badRequest().body(response);
                }
                
                response.put("matching_lecturers", result.get("matching_lecturers"));
                response.put("lectures_by_lecturer", result.get("lectures_by_lecturer"));
            }
            
            response.put("success", true);
            response.put("lectures", result.get("lectures"));
            response.put("count", result.get("count"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Existing Data Retrieval Methods ==========

    @GetMapping("/lectures/all")
    public ResponseEntity<Map<String, Object>> getAllLectures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            
            response.put("success", true);
            response.put("count", lectures.size());
            response.put("lectures", lectures);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lectures/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingLectures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecture> allLectures = lectureRepository.findAll();
            List<Lecture> upcomingLectures = allLectures.stream()
                // .filter(lecture -> lecture.getStartTime().isAfter(LocalDateTime.now()))
                // .filter(Lecture::getIsAvailable)
                // .sorted((l1, l2) -> l1.getStartTime().compareTo(l2.getStartTime()))
                .toList();
            
            response.put("success", true);
            response.put("count", upcomingLectures.size());
            response.put("upcoming_lectures", upcomingLectures);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lectures/stats")
    public ResponseEntity<Map<String, Object>> getLectureStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            
            long onlineLectures = lectures.stream()
                .filter(Lecture::isOnline)
                .count();
            
            // long availableLectures = lectures.stream().filter(Lecture::getIsAvailable).count();
            long upcomingLectures = lectures.stream()
                // .filter(lecture -> lecture.getStartTime().isAfter(LocalDateTime.now()))
                .count();
            
            double avgPrice = lectures.stream()
                .mapToInt(Lecture::getPrice)
                .average()
                .orElse(0.0);
            
            response.put("success", true);
            response.put("total_lectures", lectures.size());
            response.put("online_lectures", onlineLectures);
            response.put("physical_lectures", lectures.size() - onlineLectures);
            // response.put("available_lectures", availableLectures);
            response.put("upcoming_lectures", upcomingLectures);
            response.put("average_price", Math.round(avgPrice * 100.0) / 100.0);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Data Cleanup ==========

    @DeleteMapping("/lectures/clear")
    @Transactional
    public ResponseEntity<Map<String, Object>> clearAllLectures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long countBefore = lectureRepository.count();
            lectureRepository.deleteAll();
            
            response.put("success", true);
            response.put("message", "All lectures deleted successfully");
            response.put("deleted_count", countBefore);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Helper Methods ==========

    /**
     * פרסינג פשוט של JSON עם פרטי מרצים
     */
    private List<String[]> parseSimpleLecturerDetailsJson(String json) {
        List<String[]> result = new ArrayList<>();
        
        // פרסינג פשוט מאוד - ניתן לשפר עם ספרייה מתאימה
        // מצפה לפורמט: [{"firstName":"ישראל","lastName":"כהן","email":"israel@example.com"}]
        
        // הסרת סוגריים וחלוקה לפי אובייקטים
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        
        String[] objects = json.split("\\},\\s*\\{");
        
        for (String obj : objects) {
            obj = obj.trim();
            if (obj.startsWith("{")) obj = obj.substring(1);
            if (obj.endsWith("}")) obj = obj.substring(0, obj.length() - 1);
            
            String firstName = "";
            String lastName = "";
            String email = "";
            
            String[] fields = obj.split(",");
            for (String field : fields) {
                String[] keyValue = field.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    
                    switch (key) {
                        case "firstName": firstName = value; break;
                        case "lastName": lastName = value; break;
                        case "email": email = value; break;
                    }
                }
            }
            
            result.add(new String[]{firstName, lastName, email});
        }
        
        return result;
    }

    // ========== Enhanced Utility Endpoints ==========

    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getAvailableEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        endpoints.put("basic_lectures", List.of(
            "POST /api/advanced-faker/lectures/create-single - Create single lecture",
            "POST /api/advanced-faker/lectures/create-multiple?count=5 - Create multiple lectures",
            "POST /api/advanced-faker/lectures/create-israeli?count=8 - Create Israeli tech lectures",
            "POST /api/advanced-faker/lectures/create-upcoming?count=6 - Create upcoming lectures",
            "POST /api/advanced-faker/lectures/create-with-lecturers - Create lecture with lecturers"
        ));

        endpoints.put("enhanced_lectures", List.of(
            "POST /api/advanced-faker/lectures/create-with-existing-lecturer?lecturerId=1 - Create lecture with existing lecturer (by ID)",
            "POST /api/advanced-faker/lectures/create-fake-for-lecturer-name?lecturerName=ישראל - Create fake lecture for lecturer (by name)",
            "POST /api/advanced-faker/lectures/create-fake-random-lecturer - Create fake lecture with random lecturer",
            "POST /api/advanced-faker/lectures/create-multiple-with-existing-lecturers?count=5 - Create multiple lectures with existing lecturers"
        ));

        // ✅ NEW: Auto-Create Lecturer Endpoints
        endpoints.put("auto_create_lecturer", List.of(
            "POST /api/advanced-faker/lectures/create-with-auto-create-lecturer?firstName=ישראל&lastName=כהן&email=israel@example.com&createIfNotExists=true - Create lecture with lecturer (auto-create if not exists)",
            "POST /api/advanced-faker/lectures/create-fake-with-new-lecturer - Create fake lecture with new fake lecturer",
            "POST /api/advanced-faker/lectures/create-for-specific-lecturers?lecturerNames=ישראל כהן,שרה לוי,דוד אברהם - Create lectures for specific lecturers (comma-separated names)",
            "GET /api/advanced-faker/lecturers/search?searchTerm=ישראל - Search lecturers by details (name, email, etc.)",
            "GET /api/advanced-faker/lecturers/check-exists?firstName=ישראל&lastName=כהן&email=israel@example.com - Check if lecturer exists"
        ));
        
        endpoints.put("data_retrieval", List.of(
            "GET /api/advanced-faker/lectures/all - Get all lectures (basic)",
            "GET /api/advanced-faker/lectures/simple - Get all lectures (simple format like lecturers)",
            "GET /api/advanced-faker/lectures/all-detailed?page=0&size=50&sortBy=createdAt&sortDirection=DESC - Get all lectures with full details",
            "GET /api/advanced-faker/lectures/upcoming - Get upcoming lectures",
            "GET /api/advanced-faker/lectures/stats - Get lecture statistics",
            "GET /api/advanced-faker/lecturers/available - Get all available lecturers",
            "GET /api/advanced-faker/lectures/by-lecturer?lecturerId=1 - Get lectures by specific lecturer ID",
            "GET /api/advanced-faker/lectures/by-lecturer-name?lecturerName=ישראל - Get lectures by lecturer name (partial search)"
        
        ));
        
        endpoints.put("system", List.of(
            "POST /api/advanced-faker/create-complete-system?lecturerCount=10&lectureCount=20 - Create complete system"
            // "POST /api/advanced-faker/create-super-lecture - Create super lecture with all lecturers",
            // "POST /api/advanced-faker/fix-orphan-lectures - Fix orphan lectures",
            // "GET /api/advanced-faker/analyze-relations - Analyze many-to-many relations",
            // "GET /api/advanced-faker/lonely-lecturers - Find lonely lecturers",
            // "GET /api/advanced-faker/orphan-lectures - Find orphan lectures",
            // "GET /api/advanced-faker/relations-matrix - Get relations matrix",
            // "POST /api/advanced-faker/clean-duplicate-relations - Clean duplicate relations"
        ));

        endpoints.put("cleanup", List.of(
            "DELETE /api/advanced-faker/lectures/clear - Clear all lectures"
        ));

        endpoints.put("utility", List.of(
            "GET /api/advanced-faker/endpoints - This endpoint list",
            "GET /api/advanced-faker/ping - Health check"
        ));

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Enhanced Faker Controller - עם יצירה אוטומטית של מרצים! 🚀",
            "endpoints", endpoints,
            "new_features", List.of(
                "✅ יצירת הרצאה עם מרצה - יוצר מרצה אם לא קיים",
                "✅ יצירת הרצאות מרובות עם יצירה אוטומטית של מרצים",
                "✅ חיפוש מרצים לפי פרטים (שם, אימייל וכד')",
                "✅ בדיקה האם מרצה קיים",
                "✅ יצירת הרצאות לרשימת שמות מרצים",
                "✅ תמיכה ב-JSON לפרטי מרצים",
                "✅ יצירת הרצאה פייק עם מרצה פייק חדש",
                "✅ גמישות מלאה - מחפש קודם, יוצר אם לא קיים"
            ),
            "examples", Map.of(
                "create_with_existing_or_new", "POST /api/advanced-faker/lectures/create-with-auto-create-lecturer?firstName=ישראל&lastName=כהן",
                "create_fake_new_lecturer", "POST /api/advanced-faker/lectures/create-fake-with-new-lecturer",
                "search_lecturers", "GET /api/advanced-faker/lecturers/search?searchTerm=כהן",
                "check_exists", "GET /api/advanced-faker/lecturers/check-exists?email=israel@example.com",
                "multiple_with_names", "POST /api/advanced-faker/lectures/create-for-specific-lecturers?lecturerNames=ישראל כהן,שרה לוי"
            ),
            "timestamp", LocalDateTime.now()
        ));
    }
}