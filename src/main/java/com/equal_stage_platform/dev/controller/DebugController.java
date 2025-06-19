package com.equal_stage_platform.dev.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.ApiResponseDTO;
import com.equal_stage_platform.dev.fake.LectureFakerService;
import com.equal_stage_platform.dev.fake.LecturerFakerService;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerFakerService lecturerFakerService;

    @Autowired
    private LectureFakerService lectureFakerService;

    // ========== Status & Health ==========

    /**
     * Comprehensive system health check
     * GET /api/debug/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> systemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
            // Basic system info
            health.put("timestamp", LocalDateTime.now());
            health.put("status", "UP");
            health.put("service", "Equal Stage Platform Debug");
            
            // Database status
            long lecturerCount = lecturerRepository.count();
            long lectureCount = lectureRepository.count();
            
            health.put("database", Map.of(
                "status", "CONNECTED",
                "lecturerCount", lecturerCount,
                "lectureCount", lectureCount,
                "totalRecords", lecturerCount + lectureCount
            ));
            
            // Memory info
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            health.put("memory", Map.of(
                "totalMB", totalMemory / (1024 * 1024),
                "usedMB", usedMemory / (1024 * 1024),
                "freeMB", freeMemory / (1024 * 1024),
                "usagePercent", Math.round((double) usedMemory / totalMemory * 100)
            ));
            
            return ResponseEntity.ok(
                ApiResponseDTO.success(health, "System health check completed")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Health check failed: " + e.getMessage()));
        }
    }

    /**
     * Database status with detailed statistics
     * GET /api/debug/status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getDbStatus() {
        try {
            Map<String, Object> status = new HashMap<>();

            long lecturerCount = lecturerRepository.count();
            long lectureCount = lectureRepository.count();

            status.put("timestamp", LocalDateTime.now());
            status.put("lecturer_count", lecturerCount);
            status.put("lecture_count", lectureCount);
            status.put("database_connected", true);
            status.put("status", "healthy");

            if (lectureCount > 0) {
                long availableLectures = lectureRepository.findByIsAvailableTrue().size();
                long onlineLectures = lectureRepository.findByIsOnlineTrue().size();
                long futureLectures = lectureRepository.findByStartTimeAfter(LocalDateTime.now()).size();

                status.put("available_lectures", availableLectures);
                status.put("unavailable_lectures", lectureCount - availableLectures);
                status.put("online_lectures", onlineLectures);
                status.put("physical_lectures", lectureCount - onlineLectures);
                status.put("future_lectures", futureLectures);
                status.put("past_lectures", lectureCount - futureLectures);
            }

            return ResponseEntity.ok(
                ApiResponseDTO.success(status, "Database status retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to get database status: " + e.getMessage()));
        }
    }

    /**
     * Get sample data for inspection
     * GET /api/debug/sample-data
     */
    // @GetMapping("/sample-data")
    // public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSampleData(
    //         @RequestParam(defaultValue = "3") int limit) {
    //     try {
    //         Map<String, Object> samples = new HashMap<>();
            
    //         // Sample lecturers
    //         List<Map<String, Object>> sampleLecturers = lecturerRepository.findAll().stream()
    //             .limit(limit)
    //             .map(lecturer -> {
    //                 Map<String, Object> lecturerMap = new HashMap<>();
    //                 lecturerMap.put("id", lecturer.getUserId());
    //                 lecturerMap.put("firstName", lecturer.getFirstName());
    //                 lecturerMap.put("lastName", lecturer.getLastName());
    //                 lecturerMap.put("email", lecturer.getEmail());
    //                 lecturerMap.put("city", lecturer.getCity());
    //                 lecturerMap.put("createdAt", lecturer.getCreatedAt());
    //                 return lecturerMap;
    //             })
    //             .collect(Collectors.toList());

    //         // Sample lectures
    //         List<Map<String, Object>> sampleLectures = lectureRepository.findAll().stream()
    //             .limit(limit)
    //             .map(lecture -> {
    //                 Map<String, Object> lectureMap = new HashMap<>();
    //                 lectureMap.put("id", lecture.getLectureId());
    //                 lectureMap.put("title", lecture.getTitle());
    //                 lectureMap.put("location", lecture.getLocation());
    //                 lectureMap.put("isOnline", lecture.getIsOnline());
    //                 lectureMap.put("isAvailable", lecture.getIsAvailable());
    //                 lectureMap.put("startTime", lecture.getStartTime());
    //                 lectureMap.put("price", lecture.getPrice());
    //                 return lectureMap;
    //             })
    //             .collect(Collectors.toList());

    //         samples.put("lecturers", sampleLecturers);
    //         samples.put("lectures", sampleLectures);
    //         samples.put("limit", limit);
    //         samples.put("timestamp", LocalDateTime.now());

    //         return ResponseEntity.ok(
    //             ApiResponseDTO.success(samples, "Sample data retrieved successfully")
    //         );
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body(ApiResponseDTO.error("Failed to get sample data: " + e.getMessage()));
    //     }
    // }

    // ========== Create Lecturers ==========

    /**
     * Create a single fake lecturer
     * POST /api/debug/create-lecturer
     */
    @PostMapping("/create-lecturer")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Lecturer>> createSingleFakeLecturer() {
        try {
            Lecturer lecturer = lecturerFakerService.generateFakeLecturer();
            Lecturer saved = lecturerRepository.save(lecturer);

            return ResponseEntity.ok(
                ApiResponseDTO.success(saved, "Fake lecturer created successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lecturer: " + e.getMessage()));
        }
    }

    /**
     * Create multiple fake lecturers (limit 50)
     * POST /api/debug/create-lecturers?count=10
     */
    @PostMapping("/create-lecturers")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createMultipleLecturers(
            @RequestParam(defaultValue = "5") int count) {
        try {
            if (count > 50) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Maximum allowed is 50 lecturers"));
            }

            if (count <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Count must be greater than 0"));
            }

            List<Lecturer> lecturers = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                lecturers.add(lecturerFakerService.generateFakeLecturer());
            }

            List<Lecturer> savedLecturers = lecturerRepository.saveAll(lecturers);

            Map<String, Object> result = new HashMap<>();
            result.put("created_count", savedLecturers.size());
            result.put("total_lecturers", lecturerRepository.count());
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(
                ApiResponseDTO.success(result, count + " fake lecturers created successfully")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lecturers: " + e.getMessage()));
        }
    }

    // ========== Create Lectures ==========

    /**
     * Create a single fake lecture
     * POST /api/debug/create-lecture
     */
    @PostMapping("/create-lecture")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Lecture>> createSingleFakeLecture() {
        try {
            Lecture lecture = lectureFakerService.generateFakeLecture();
            Lecture saved = lectureRepository.save(lecture);

            return ResponseEntity.ok(
                ApiResponseDTO.success(saved, "Fake lecture created successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lecture: " + e.getMessage()));
        }
    }

    /**
     * Create multiple fake lectures (limit 100)
     * POST /api/debug/create-lectures?count=20
     */
    @PostMapping("/create-lectures")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createMultipleLectures(
            @RequestParam(defaultValue = "10") int count) {
        try {
            if (count > 100) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Maximum allowed is 100 lectures"));
            }

            if (count <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Count must be greater than 0"));
            }

            List<Lecture> lectures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                lectures.add(lectureFakerService.generateFakeLecture());
            }

            List<Lecture> savedLectures = lectureRepository.saveAll(lectures);

            Map<String, Object> result = new HashMap<>();
            result.put("created_count", savedLectures.size());
            result.put("total_lectures", lectureRepository.count());
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(
                ApiResponseDTO.success(result, count + " fake lectures created successfully")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lectures: " + e.getMessage()));
        }
    }

    /**
     * Create full system with fake lecturers and lectures
     * POST /api/debug/create-full-system?lecturerCount=20&lectureCount=50
     */
    @PostMapping("/create-full-system")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createFullSystem(
            @RequestParam(defaultValue = "20") int lecturerCount,
            @RequestParam(defaultValue = "50") int lectureCount) {
        try {
            if (lecturerCount > 50 || lectureCount > 100) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Limits exceeded: Maximum 50 lecturers, 100 lectures"));
            }

            if (lecturerCount <= 0 || lectureCount <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Counts must be greater than 0"));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("timestamp", LocalDateTime.now());

            // Create lecturers
            List<Lecturer> lecturers = new ArrayList<>();
            for (int i = 0; i < lecturerCount; i++) {
                lecturers.add(lecturerFakerService.generateFakeLecturer());
            }
            List<Lecturer> savedLecturers = lecturerRepository.saveAll(lecturers);

            // Create lectures
            List<Lecture> lectures = new ArrayList<>();
            for (int i = 0; i < lectureCount; i++) {
                lectures.add(lectureFakerService.generateFakeLecture());
            }
            List<Lecture> savedLectures = lectureRepository.saveAll(lectures);

            result.put("created_lecturers", savedLecturers.size());
            result.put("created_lectures", savedLectures.size());
            result.put("total_lecturers", lecturerRepository.count());
            result.put("total_lectures", lectureRepository.count());

            return ResponseEntity.ok(
                ApiResponseDTO.success(result, 
                    String.format("Full system created: %d lecturers, %d lectures", 
                        savedLecturers.size(), savedLectures.size()))
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating full system: " + e.getMessage()));
        }
    }

    // ========== Clear Data ==========

    /**
     * Delete all lecturers (Caution!)
     * POST /api/debug/clear-lecturers
     */
    @PostMapping("/clear-lecturers")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> clearAllLecturers() {
        try {
            long countBefore = lecturerRepository.count();
            lecturerRepository.deleteAll();

            Map<String, Object> result = new HashMap<>();
            result.put("deleted_count", countBefore);
            result.put("remaining_lecturers", lecturerRepository.count());
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(
                ApiResponseDTO.success(result, countBefore + " lecturers deleted from database")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error deleting lecturers: " + e.getMessage()));
        }
    }

    /**
     * Delete all lectures (Caution!)
     * POST /api/debug/clear-lectures
     */
    @PostMapping("/clear-lectures")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> clearAllLectures() {
        try {
            long countBefore = lectureRepository.count();
            lectureRepository.deleteAll();

            Map<String, Object> result = new HashMap<>();
            result.put("deleted_count", countBefore);
            result.put("remaining_lectures", lectureRepository.count());
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(
                ApiResponseDTO.success(result, countBefore + " lectures deleted from database")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error deleting lectures: " + e.getMessage()));
        }
    }

    /**
     * Delete all data (Caution!)
     * POST /api/debug/clear-all?confirm=true
     */
    @PostMapping("/clear-all")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> clearAllData(
            @RequestParam(defaultValue = "false") boolean confirm) {
        
        if (!confirm) {
            return ResponseEntity.badRequest()
                .body(ApiResponseDTO.error("Confirmation required. Add ?confirm=true to proceed"));
        }
        
        try {
            long lecturersBefore = lecturerRepository.count();
            long lecturesBefore = lectureRepository.count();

            // Delete in correct order to avoid constraint violations
            lectureRepository.deleteAll();
            lecturerRepository.deleteAll();

            Map<String, Object> result = new HashMap<>();
            result.put("deleted_lecturers", lecturersBefore);
            result.put("deleted_lectures", lecturesBefore);
            result.put("total_deleted", lecturersBefore + lecturesBefore);
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(
                ApiResponseDTO.success(result, 
                    String.format("All data deleted: %d lecturers, %d lectures", 
                        lecturersBefore, lecturesBefore))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error deleting all data: " + e.getMessage()));
        }
    }

    /**
     * Test error response
     * GET /api/debug/error-test
     */
    @GetMapping("/error-test")
    public ResponseEntity<ApiResponseDTO<String>> errorTest() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseDTO.error("This is a test error for debugging purposes"));
    }

    /**
     * Get all available debug endpoints
     * GET /api/debug/endpoints
     */
    @GetMapping("/endpoints")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getAvailableEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        endpoints.put("info", List.of(
            "GET /api/debug/health - System health check",
            "GET /api/debug/status - Database status",
            "GET /api/debug/sample-data?limit=3 - Sample data",
            "GET /api/debug/endpoints - This endpoint list"
        ));
        
        endpoints.put("create", List.of(
            "POST /api/debug/create-lecturer - Create single lecturer",
            "POST /api/debug/create-lecturers?count=5 - Create multiple lecturers",
            "POST /api/debug/create-lecture - Create single lecture",
            "POST /api/debug/create-lectures?count=10 - Create multiple lectures",
            "POST /api/debug/create-full-system?lecturerCount=20&lectureCount=50 - Create full system"
        ));
        
        endpoints.put("clear", List.of(
            "POST /api/debug/clear-lecturers - Delete all lecturers",
            "POST /api/debug/clear-lectures - Delete all lectures",
            "POST /api/debug/clear-all?confirm=true - Delete all data (requires confirmation)"
        ));
        
        endpoints.put("test", List.of(
            "GET /api/debug/error-test - Test error response"
        ));

        return ResponseEntity.ok(
            ApiResponseDTO.success(endpoints, "Available debug endpoints")
        );
    }
}