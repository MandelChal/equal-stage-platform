package com.equal_stage_platform.dev.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.ApiResponseDTO;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.equal_stage_platform.dev.service.AdvancedLectureFakerService;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private AdvancedLectureFakerService advancedLectureFakerService;

    // ========== System Health & Status ==========

    @GetMapping("/health")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> systemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
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
            
            return ResponseEntity.ok(ApiResponseDTO.success(health, "System health check completed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Health check failed: " + e.getMessage()));
        }
    }

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

            return ResponseEntity.ok(ApiResponseDTO.success(status, "Database status retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to get database status: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getDatabaseStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long totalLectures = lectureRepository.count();
            long totalLecturers = lecturerRepository.count();
            
            stats.put("timestamp", LocalDateTime.now());
            stats.put("totalLectures", totalLectures);
            stats.put("totalLecturers", totalLecturers);
            
            if (totalLectures > 0) {
                long availableLectures = lectureRepository.findByIsAvailableTrue().size();
                long onlineLectures = lectureRepository.findByIsOnlineTrue().size();
                long futureLectures = lectureRepository.findByStartTimeAfter(LocalDateTime.now()).size();
                
                stats.put("lectureBreakdown", Map.of(
                    "available", availableLectures,
                    "unavailable", totalLectures - availableLectures,
                    "online", onlineLectures,
                    "physical", totalLectures - onlineLectures,
                    "future", futureLectures,
                    "past", totalLectures - futureLectures
                ));
                
                List<String> topLocations = lectureRepository.findAll().stream()
                    .map(Lecture::getLocation)
                    .collect(Collectors.groupingBy(loc -> loc, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                    .collect(Collectors.toList());
                
                stats.put("topLocations", topLocations);
            }
            
            if (totalLecturers > 0) {
                List<String> topCities = lecturerRepository.findAll().stream()
                    .map(Lecturer::getCity)
                    .filter(city -> city != null && !city.trim().isEmpty())
                    .collect(Collectors.groupingBy(city -> city, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                    .collect(Collectors.toList());
                
                stats.put("topCities", topCities);
            }
            
            return ResponseEntity.ok(ApiResponseDTO.success(stats, "Database statistics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to get database statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/integrity-check")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> databaseIntegrityCheck() {
        try {
            Map<String, Object> integrity = new HashMap<>();
            
            long totalLectures = lectureRepository.count();
            long totalLecturers = lecturerRepository.count();
            
            integrity.put("timestamp", LocalDateTime.now());
            integrity.put("basicCounts", Map.of("lectures", totalLectures, "lecturers", totalLecturers));
            
            List<Map<String, Object>> issues = new java.util.ArrayList<>();
            
            // Check lectures with null required fields
            List<Lecture> lecturesWithIssues = lectureRepository.findAll().stream()
                .filter(lecture -> lecture.getTitle() == null || lecture.getTitle().trim().isEmpty() ||
                                 lecture.getStartTime() == null || lecture.getEndTime() == null)
                .collect(Collectors.toList());
            
            if (!lecturesWithIssues.isEmpty()) {
                issues.add(Map.of(
                    "type", "INVALID_LECTURES",
                    "count", lecturesWithIssues.size(),
                    "description", "Lectures with missing required fields"
                ));
            }
            
            // Check lecturers with null required fields
            List<Lecturer> lecturersWithIssues = lecturerRepository.findAll().stream()
                .filter(lecturer -> lecturer.getFirstName() == null || lecturer.getFirstName().trim().isEmpty() ||
                                  lecturer.getLastName() == null || lecturer.getLastName().trim().isEmpty() ||
                                  lecturer.getEmail() == null || lecturer.getEmail().trim().isEmpty())
                .collect(Collectors.toList());
            
            if (!lecturersWithIssues.isEmpty()) {
                issues.add(Map.of(
                    "type", "INVALID_LECTURERS",
                    "count", lecturersWithIssues.size(),
                    "description", "Lecturers with missing required fields"
                ));
            }
            
            integrity.put("issues", issues);
            integrity.put("status", issues.isEmpty() ? "HEALTHY" : "ISSUES_FOUND");
            
            return ResponseEntity.ok(ApiResponseDTO.success(integrity, "Database integrity check completed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to perform integrity check: " + e.getMessage()));
        }
    }

    // ========== Data Creation ==========

    @PostMapping("/create-lecturer")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createSingleLecturer() {
        try {
            var result = advancedLectureFakerService.createSingleLecturer();
            return ResponseEntity.ok(ApiResponseDTO.success(result, "Fake lecturer created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lecturer: " + e.getMessage()));
        }
    }

    @PostMapping("/create-lecturers")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createMultipleLecturers(
            @RequestParam(defaultValue = "5") int count) {
        try {
            if (count > 50) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Maximum allowed is 50 lecturers"));
            }

            var result = advancedLectureFakerService.createMultipleLecturers(count);
            return ResponseEntity.ok(ApiResponseDTO.success(result, count + " fake lecturers created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lecturers: " + e.getMessage()));
        }
    }

    @PostMapping("/create-lecture")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createSingleLecture() {
        try {
            var result = advancedLectureFakerService.createSingleLecture();
            return ResponseEntity.ok(ApiResponseDTO.success(result, "Fake lecture created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lecture: " + e.getMessage()));
        }
    }

    @PostMapping("/create-lectures")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createMultipleLectures(
            @RequestParam(defaultValue = "10") int count) {
        try {
            if (count > 100) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Maximum allowed is 100 lectures"));
            }

            var result = advancedLectureFakerService.createMultipleLectures(count);
            return ResponseEntity.ok(ApiResponseDTO.success(result, count + " fake lectures created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating lectures: " + e.getMessage()));
        }
    }

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

            var result = advancedLectureFakerService.createCompleteSystemWithRelations(lecturerCount, lectureCount);
            return ResponseEntity.ok(ApiResponseDTO.success(result, "Complete system created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error creating complete system: " + e.getMessage()));
        }
    }

    @PostMapping("/recreate-data")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> recreateFakeData() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            long lecturesBefore = lectureRepository.count();
            long lecturersBefore = lecturerRepository.count();
            
            lectureRepository.deleteAll();
            lecturerRepository.deleteAll();
            
            result.put("deletedLectures", lecturesBefore);
            result.put("deletedLecturers", lecturersBefore);
            
            advancedLectureFakerService.createFakeData();
            
            long lecturesAfter = lectureRepository.count();
            long lecturersAfter = lecturerRepository.count();
            
            result.put("createdLectures", lecturesAfter);
            result.put("createdLecturers", lecturersAfter);
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(ApiResponseDTO.success(result, "Fake data recreated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to recreate fake data: " + e.getMessage()));
        }
    }

    // ========== Data Deletion ==========

    @PostMapping("/clear-lecturers")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> clearAllLecturers() {
        try {
            long countBefore = lecturerRepository.count();
            lecturerRepository.deleteAll();

            Map<String, Object> result = Map.of(
                "deleted_count", countBefore,
                "remaining_lecturers", lecturerRepository.count(),
                "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(ApiResponseDTO.success(result, countBefore + " lecturers deleted from database"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error deleting lecturers: " + e.getMessage()));
        }
    }

    @PostMapping("/clear-lectures")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> clearAllLectures() {
        try {
            long countBefore = lectureRepository.count();
            lectureRepository.deleteAll();

            Map<String, Object> result = Map.of(
                "deleted_count", countBefore,
                "remaining_lectures", lectureRepository.count(),
                "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(ApiResponseDTO.success(result, countBefore + " lectures deleted from database"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error deleting lectures: " + e.getMessage()));
        }
    }

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

            Map<String, Object> result = Map.of(
                "deletedLecturers", lecturersBefore,
                "deletedLectures", lecturesBefore,
                "total_deleted", lecturersBefore + lecturesBefore,
                "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(ApiResponseDTO.success(result, 
                String.format("All data deleted: %d lecturers, %d lectures", 
                    lecturersBefore, lecturesBefore)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error deleting all data: " + e.getMessage()));
        }
    }

    @DeleteMapping("/clear-all")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> clearAllDataDelete(
            @RequestParam(required = false, defaultValue = "false") boolean confirm) {
        
        if (!confirm) {
            return ResponseEntity.badRequest()
                .body(ApiResponseDTO.error("Confirmation required. Add ?confirm=true to proceed"));
        }
        
        try {
            long lecturesBefore = lectureRepository.count();
            long lecturersBefore = lecturerRepository.count();
            
            lectureRepository.deleteAll();
            lecturerRepository.deleteAll();
            
            Map<String, Object> result = Map.of(
                "deletedLectures", lecturesBefore,
                "deletedLecturers", lecturersBefore,
                "timestamp", LocalDateTime.now()
            );
            
            return ResponseEntity.ok(ApiResponseDTO.success(result, "All data cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to clear data: " + e.getMessage()));
        }
    }

    // ========== Utility Endpoints ==========

    @GetMapping("/endpoints")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getAvailableEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        endpoints.put("info", List.of(
            "GET /api/debug/health - System health check",
            "GET /api/debug/status - Database status",
            "GET /api/debug/stats - Database statistics", 
            "GET /api/debug/integrity-check - Database integrity check",
            "GET /api/debug/endpoints - This endpoint list"
        ));
        
        endpoints.put("create", List.of(
            "POST /api/debug/create-lecturer - Create single lecturer",
            "POST /api/debug/create-lecturers?count=5 - Create multiple lecturers",
            "POST /api/debug/create-lecture - Create single lecture",
            "POST /api/debug/create-lectures?count=10 - Create multiple lectures",
            "POST /api/debug/create-full-system?lecturerCount=20&lectureCount=50 - Create complete system",
            "POST /api/debug/recreate-data - Recreate all fake data"
        ));
        
        endpoints.put("clear", List.of(
            "POST /api/debug/clear-lecturers - Delete all lecturers",
            "POST /api/debug/clear-lectures - Delete all lectures", 
            "POST /api/debug/clear-all?confirm=true - Delete all data (requires confirmation)",
            "DELETE /api/debug/clear-all?confirm=true - Delete all data (requires confirmation)"
        ));

        return ResponseEntity.ok(ApiResponseDTO.success(endpoints, "Available debug endpoints"));
    }

    @GetMapping("/schema-info")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSchemaInfo() {
        try {
            Map<String, Object> schema = Map.of(
                "timestamp", LocalDateTime.now(),
                "tables", Map.of(
                    "lectures", Map.of(
                        "entityClass", "Lecture.class",
                        "tableName", "lectures", 
                        "primaryKey", "lecture_id (Integer)",
                        "relationships", "ManyToMany with Lecturer"
                    ),
                    "lecturers", Map.of(
                        "entityClass", "Lecturer.class",
                        "tableName", "lecturers",
                        "primaryKey", "user_id (Long)",
                        "relationships", "ManyToMany with Lecture"
                    ),
                    "joinTable", Map.of(
                        "tableName", "lectures_lecturers",
                        "columns", "user_id, lecture_id"
                    )
                )
            );
            
            return ResponseEntity.ok(ApiResponseDTO.success(schema, "Database schema information retrieved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to get schema info: " + e.getMessage()));
        }
    }

    @GetMapping("/error-test")
    public ResponseEntity<ApiResponseDTO<String>> errorTest() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseDTO.error("This is a test error for debugging purposes"));
    }
}