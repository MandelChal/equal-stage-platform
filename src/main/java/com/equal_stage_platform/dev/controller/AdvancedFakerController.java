// package com.equal_stage_platform.dev.controller;

// import com.equal_stage_platform.dev.model.Lecture;

// import com.equal_stage_platform.dev.repository.LectureRepository;
// import com.equal_stage_platform.dev.repository.LecturerRepository;
// import com.equal_stage_platform.dev.service.AdvancedLectureFakerService;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/advanced-faker")
// @CrossOrigin(origins = "*")
// public class AdvancedFakerController {

//     @Autowired
//     private AdvancedLectureFakerService advancedLectureFakerService;

//     @Autowired
//     private LectureRepository lectureRepository;

//     @Autowired
//     private LecturerRepository lecturerRepository;

//     // ========== Basic Lecture Creation ==========

//     @PostMapping("/lectures/create-single")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createSingleLecture() {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             var result = advancedLectureFakerService.createSingleLecture();
            
//             response.put("success", true);
//             response.put("message", "Single lecture created successfully!");
//             response.put("lecture", result.get("lecture"));
//             response.put("total_lectures", lectureRepository.count());
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     @PostMapping("/lectures/create-multiple")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createMultipleLectures(@RequestParam(defaultValue = "5") int count) {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             var result = advancedLectureFakerService.createMultipleLectures(count);
            
//             response.put("success", true);
//             response.put("message", "Created " + count + " lectures successfully!");
//             response.put("count", count);
//             response.put("lectures", result.get("lectures"));
//             response.put("total_in_db", lectureRepository.count());
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     @PostMapping("/lectures/create-israeli")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createIsraeliTechLectures(@RequestParam(defaultValue = "8") int count) {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             var result = advancedLectureFakerService.createIsraeliTechLectures(count);
            
//             response.put("success", true);
//             response.put("message", "Created " + count + " Israeli tech lectures!");
//             response.put("count", count);
//             response.put("lectures", result.get("lectures"));
//             response.put("total_in_db", lectureRepository.count());
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     @PostMapping("/lectures/create-upcoming")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createUpcomingLectures(@RequestParam(defaultValue = "6") int count) {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             var result = advancedLectureFakerService.generateUpcomingLectures(count);
            
//             response.put("success", true);
//             response.put("message", "Created " + count + " upcoming lectures!");
//             response.put("count", count);
//             response.put("lectures", result.get("lectures"));
//             response.put("total_in_db", lectureRepository.count());
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     @PostMapping("/lectures/create-with-lecturers")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createLectureWithLecturers() {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             var result = advancedLectureFakerService.generateSingleLectureWithLecturers();
            
//             response.put("success", true);
//             response.put("message", "Lecture created with lecturers!");
//             response.put("lecture", result.get("lecture"));
//             response.put("attached_lecturers", result.get("attached_lecturers"));
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     // ========== Advanced System Creation ==========

//     @PostMapping("/create-complete-system")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createCompleteSystem(
//             @RequestParam(defaultValue = "10") int lecturerCount,
//             @RequestParam(defaultValue = "20") int lectureCount) {
        
//         try {
//             System.out.println("Creating complete system with " + lecturerCount + " lecturers and " + lectureCount + " lectures...");
            
//             Map<String, Object> result = advancedLectureFakerService.createCompleteSystemWithRelations(lecturerCount, lectureCount);
            
//             if ((Boolean) result.get("success")) {
//                 System.out.println("Successfully created complete system!");
//                 System.out.println("- Lecturers: " + result.get("lecturers_created"));
//                 System.out.println("- Lectures: " + result.get("lectures_created"));
//                 System.out.println("- Total Relations: " + result.get("total_relations"));
//             }
            
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             System.err.println("Error creating complete system: " + e.getMessage());
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error creating complete system: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     @PostMapping("/create-new-relations")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createNewRelations(@RequestParam(defaultValue = "5") int newRelations) {
//         try {
//             System.out.println("Creating " + newRelations + " new relations...");
            
//             var result = advancedLectureFakerService.createNewManyToManyRelations(newRelations);
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             System.err.println("Error creating new relations: " + e.getMessage());
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error creating new relations: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     @PostMapping("/create-super-lecture")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> createSuperLecture() {
//         try {
//             var result = advancedLectureFakerService.createSuperLecture();
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             System.err.println("Error creating super lecture: " + e.getMessage());
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error creating super lecture: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     @PostMapping("/fix-orphan-lectures")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> fixOrphanLectures() {
//         try {
//             var result = advancedLectureFakerService.fixOrphanLectures();
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             System.err.println("Error fixing orphan lectures: " + e.getMessage());
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error fixing orphan lectures: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     // ========== Analysis & Information ==========

//     @GetMapping("/analyze-relations")
//     public ResponseEntity<Map<String, Object>> analyzeManyToManyRelations() {
//         try {
//             System.out.println("Analyzing many-to-many relations...");
            
//             Map<String, Object> analysis = advancedLectureFakerService.analyzeManyToManyRelations();
            
//             return ResponseEntity.ok(analysis);
            
//         } catch (Exception e) {
//             System.err.println("Error analyzing relations: " + e.getMessage());
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error analyzing relations: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     @GetMapping("/lonely-lecturers")
//     public ResponseEntity<Map<String, Object>> findLonelyLecturers() {
//         try {
//             var result = advancedLectureFakerService.findLonelyLecturers();
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error finding lonely lecturers: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     @GetMapping("/orphan-lectures")
//     public ResponseEntity<Map<String, Object>> findOrphanLectures() {
//         try {
//             var result = advancedLectureFakerService.findOrphanLectures();
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error finding orphan lectures: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     @GetMapping("/relations-matrix")
//     public ResponseEntity<Map<String, Object>> getRelationsMatrix() {
//         try {
//             var result = advancedLectureFakerService.getRelationsMatrix();
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error generating relations matrix: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     @PostMapping("/clean-duplicate-relations")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> cleanDuplicateRelations() {
//         try {
//             var result = advancedLectureFakerService.cleanDuplicateRelations();
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("message", "Error cleaning duplicate relations: " + e.getMessage());
//             return ResponseEntity.badRequest().body(error);
//         }
//     }

//     // ========== Data Retrieval ==========

//     @GetMapping("/lectures/all")
//     public ResponseEntity<Map<String, Object>> getAllLectures() {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             List<Lecture> lectures = lectureRepository.findAll();
            
//             response.put("success", true);
//             response.put("count", lectures.size());
//             response.put("lectures", lectures);
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     @GetMapping("/lectures/upcoming")
//     public ResponseEntity<Map<String, Object>> getUpcomingLectures() {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             List<Lecture> allLectures = lectureRepository.findAll();
//             List<Lecture> upcomingLectures = allLectures.stream()
//                 .filter(lecture -> lecture.getStartTime().isAfter(LocalDateTime.now()))
//                 .filter(Lecture::getIsAvailable)
//                 .sorted((l1, l2) -> l1.getStartTime().compareTo(l2.getStartTime()))
//                 .toList();
            
//             response.put("success", true);
//             response.put("count", upcomingLectures.size());
//             response.put("upcoming_lectures", upcomingLectures);
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     @GetMapping("/lectures/stats")
//     public ResponseEntity<Map<String, Object>> getLectureStats() {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             List<Lecture> lectures = lectureRepository.findAll();
            
//             long onlineLectures = lectures.stream().filter(Lecture::getIsOnline).count();
//             long availableLectures = lectures.stream().filter(Lecture::getIsAvailable).count();
//             long upcomingLectures = lectures.stream()
//                 .filter(lecture -> lecture.getStartTime().isAfter(LocalDateTime.now()))
//                 .count();
            
//             double avgPrice = lectures.stream()
//                 .mapToInt(Lecture::getPrice)
//                 .average()
//                 .orElse(0.0);
            
//             response.put("success", true);
//             response.put("total_lectures", lectures.size());
//             response.put("online_lectures", onlineLectures);
//             response.put("physical_lectures", lectures.size() - onlineLectures);
//             response.put("available_lectures", availableLectures);
//             response.put("upcoming_lectures", upcomingLectures);
//             response.put("average_price", Math.round(avgPrice * 100.0) / 100.0);
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     // ========== Data Cleanup ==========

//     @DeleteMapping("/lectures/clear")
//     @Transactional
//     public ResponseEntity<Map<String, Object>> clearAllLectures() {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             long countBefore = lectureRepository.count();
//             lectureRepository.deleteAll();
            
//             response.put("success", true);
//             response.put("message", "All lectures deleted successfully");
//             response.put("deleted_count", countBefore);
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Error: " + e.getMessage());
//         }
        
//         return ResponseEntity.ok(response);
//     }

//     // ========== Utility Endpoints ==========

//     @GetMapping("/endpoints")
//     public ResponseEntity<Map<String, Object>> getAvailableEndpoints() {
//         Map<String, Object> endpoints = new HashMap<>();
        
//         endpoints.put("lectures", List.of(
//             "POST /api/advanced-faker/lectures/create-single - Create single lecture",
//             "POST /api/advanced-faker/lectures/create-multiple?count=5 - Create multiple lectures",
//             "POST /api/advanced-faker/lectures/create-israeli?count=8 - Create Israeli tech lectures",
//             "POST /api/advanced-faker/lectures/create-upcoming?count=6 - Create upcoming lectures",
//             "POST /api/advanced-faker/lectures/create-with-lecturers - Create lecture with lecturers",
//             "GET /api/advanced-faker/lectures/all - Get all lectures",
//             "GET /api/advanced-faker/lectures/upcoming - Get upcoming lectures",
//             "GET /api/advanced-faker/lectures/stats - Get lecture statistics",
//             "DELETE /api/advanced-faker/lectures/clear - Clear all lectures"
//         ));
        
//         endpoints.put("system", List.of(
//             "POST /api/advanced-faker/create-complete-system?lecturerCount=10&lectureCount=20 - Create complete system",
//             "POST /api/advanced-faker/create-new-relations?newRelations=5 - Create new relations",
//             "POST /api/advanced-faker/create-super-lecture - Create super lecture with all lecturers",
//             "POST /api/advanced-faker/fix-orphan-lectures - Fix orphan lectures",
//             "GET /api/advanced-faker/analyze-relations - Analyze many-to-many relations",
//             "GET /api/advanced-faker/lonely-lecturers - Find lonely lecturers",
//             "GET /api/advanced-faker/orphan-lectures - Find orphan lectures",
//             "GET /api/advanced-faker/relations-matrix - Get relations matrix",
//             "POST /api/advanced-faker/clean-duplicate-relations - Clean duplicate relations"
//         ));

//         endpoints.put("utility", List.of(
//             "GET /api/advanced-faker/endpoints - This endpoint list"
//         ));

//         return ResponseEntity.ok(Map.of(
//             "success", true,
//             "endpoints", endpoints,
//             "timestamp", LocalDateTime.now()
//         ));
//     }
// }