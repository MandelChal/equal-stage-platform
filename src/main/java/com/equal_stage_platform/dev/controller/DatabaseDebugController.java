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
import com.equal_stage_platform.dev.service.DatabaseInitializer;

@RestController
@RequestMapping("/api/db-debug")
@CrossOrigin(origins = "*")
public class DatabaseDebugController {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    /**
     * Get database statistics and overview
     * GET /api/db-debug/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getDatabaseStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Basic counts
            long totalLectures = lectureRepository.count();
            long totalLecturers = lecturerRepository.count();
            
            stats.put("timestamp", LocalDateTime.now());
            stats.put("totalLectures", totalLectures);
            stats.put("totalLecturers", totalLecturers);
            
            if (totalLectures > 0) {
                // Lecture statistics
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
                
                // Location statistics
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
                // Lecturer statistics
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
            
            return ResponseEntity.ok(
                ApiResponseDTO.success(stats, "Database statistics retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to get database statistics: " + e.getMessage()));
        }
    }

    /**
     * Recreate fake data
     * POST /api/db-debug/recreate-data
     */
    @PostMapping("/recreate-data")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> recreateFakeData() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Clear existing data
            long lecturesBefore = lectureRepository.count();
            long lecturersBefore = lecturerRepository.count();
            
            lectureRepository.deleteAll();
            lecturerRepository.deleteAll();
            
            result.put("deletedLectures", lecturesBefore);
            result.put("deletedLecturers", lecturersBefore);
            
            // Recreate fake data
            databaseInitializer.createFakeData();
            
            // Get new counts
            long lecturesAfter = lectureRepository.count();
            long lecturersAfter = lecturerRepository.count();
            
            result.put("createdLectures", lecturesAfter);
            result.put("createdLecturers", lecturersAfter);
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(
                ApiResponseDTO.success(result, "Fake data recreated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to recreate fake data: " + e.getMessage()));
        }
    }

    /**
     * Clear all data from database
     * DELETE /api/db-debug/clear-all
     */
    @DeleteMapping("/clear-all")
    @Transactional
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> clearAllData(
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
            
            return ResponseEntity.ok(
                ApiResponseDTO.success(result, "All data cleared successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to clear data: " + e.getMessage()));
        }
    }

    /**
     * Get sample data from each table
     * GET /api/db-debug/sample-data
     */
    // @GetMapping("/sample-data")
    // public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSampleData(
    //         @RequestParam(required = false, defaultValue = "5") int limit) {
    //     try {
    //         Map<String, Object> samples = new HashMap<>();
            
    //         // Sample lectures
    //         List<Map<String, Object>> sampleLectures = lectureRepository.findAll().stream()
    //             .limit(limit)
    //             .map(lecture -> Map.of(
    //                 "id", lecture.getLectureId(),
    //                 "title", lecture.getTitle(),
    //                 "location", lecture.getLocation() != null ? lecture.getLocation() : "N/A",
    //                 "isOnline", lecture.getIsOnline(),
    //                 "isAvailable", lecture.getIsAvailable(),
    //                 "startTime", lecture.getStartTime(),
    //                 "price", lecture.getPrice()
    //             ))
    //             .collect(Collectors.toList());
            
    //         // Sample lecturers
    //         List<Map<String, Object>> sampleLecturers = lecturerRepository.findAll().stream()
    //             .limit(limit)
    //             .map(lecturer -> Map.of(
    //                 "id", lecturer.getUserId(),
    //                 "firstName", lecturer.getFirstName(),
    //                 "lastName", lecturer.getLastName(),
    //                 "email", lecturer.getEmail(),
    //                 "city", lecturer.getCity() != null ? lecturer.getCity() : "N/A",
    //                 "createdAt", lecturer.getCreatedAt()
    //             ))
    //             .collect(Collectors.toList());
            
    //         samples.put("lectures", sampleLectures);
    //         samples.put("lecturers", sampleLecturers);
    //         samples.put("timestamp", LocalDateTime.now());
    //         samples.put("limit", limit);
            
    //         return ResponseEntity.ok(
    //             ApiResponseDTO.success(samples, "Sample data retrieved successfully")
    //         );
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body(ApiResponseDTO.error("Failed to get sample data: " + e.getMessage()));
    //     }
    // }

    /**
     * Test database integrity
     * GET /api/db-debug/integrity-check
     */
    @GetMapping("/integrity-check")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> databaseIntegrityCheck() {
        try {
            Map<String, Object> integrity = new HashMap<>();
            
            // Check for data consistency
            long totalLectures = lectureRepository.count();
            long totalLecturers = lecturerRepository.count();
            
            integrity.put("timestamp", LocalDateTime.now());
            integrity.put("basicCounts", Map.of(
                "lectures", totalLectures,
                "lecturers", totalLecturers
            ));
            
            // Check for invalid data
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
            
            return ResponseEntity.ok(
                ApiResponseDTO.success(integrity, "Database integrity check completed")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to perform integrity check: " + e.getMessage()));
        }
    }

    /**
     * Get database schema information
     * GET /api/db-debug/schema-info
     */
    @GetMapping("/schema-info")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSchemaInfo() {
        try {
            Map<String, Object> schema = new HashMap<>();
            
            schema.put("timestamp", LocalDateTime.now());
            schema.put("tables", Map.of(
                "lectures", Map.of(
                    "entityClass", "Lecture.class",
                    "tableName", "lectures",
                    "primaryKey", "lecture_id (Integer)",
                    "relationships", "ManyToMany with Lecturer"
                ),
                "lecturers", Map.of(
                    "entityClass", "Lecturer.class", 
                    "tableName", "0!58$_lecturers",
                    "primaryKey", "user_id (Long)",
                    "relationships", "ManyToMany with Lecture"
                ),
                "joinTable", Map.of(
                    "tableName", "0!58$_lectures_lecturers",
                    "columns", "user_id, lecture_id"
                )
            ));
            
            return ResponseEntity.ok(
                ApiResponseDTO.success(schema, "Database schema information retrieved")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to get schema info: " + e.getMessage()));
        }
    }
}