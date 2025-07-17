package com.equal_stage_platform.dev.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.equal_stage_platform.dev.service.AdvancedLectureFakerService;

@RestController
@RequestMapping("/api/advanced-faker")
@CrossOrigin(origins = "*")
public class AdvancedFakerController {

    @GetMapping("/ping")
    public String ping() {
        return "pong - Flow-Aware Faker System 🚀";
    }

    @Autowired
    private AdvancedLectureFakerService advancedLectureFakerService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    // ========== Flow-Aware Lecturer Management ==========

    @PostMapping("/lecturers/create-single")
    @Transactional
    public ResponseEntity<Map<String, Object>> createSingleLecturer() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createSingleLecturer();
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("lecturer", result.get("lecturer"));
            response.put("status", result.get("status"));
            response.put("total_lecturers", result.get("total_lecturers"));
            response.put("flow_note", "Lecturer created in PENDING status. Requires admin approval to create lectures.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lecturers/create-multiple")
    @Transactional
    public ResponseEntity<Map<String, Object>> createMultipleLecturers(
            @RequestParam(defaultValue = "5") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createMultipleLecturers(count);
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("created_count", result.get("created_count"));
            response.put("lecturers", result.get("lecturers"));
            response.put("total_lecturers", result.get("total_lecturers"));
            response.put("flow_note", "All lecturers created in PENDING status. They need admin approval to create lectures.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lecturers/pending")
    public ResponseEntity<Map<String, Object>> getPendingLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.getPendingLecturers();
            
            response.put("success", result.get("success"));
            response.put("pending_lecturers", result.get("pending_lecturers"));
            response.put("pending_count", result.get("pending_count"));
            response.put("total_lecturers", result.get("total_lecturers"));
            response.put("flow_note", "These lecturers are waiting for admin approval.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lecturers/approve-all")
    @Transactional
    public ResponseEntity<Map<String, Object>> approveAllPendingLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.approveAllPendingLecturers();
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("approved_count", result.get("approved_count"));
            response.put("approved_lecturers", result.get("approved_lecturers"));
            response.put("flow_note", "Approved lecturers can now create lectures.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lecturers/approve/{lecturerId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> approveLecturer(@PathVariable UUID lecturerId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();
            Optional<Lecturer> lecturer = lecturers.stream()
                .filter(l -> l.getUserId().equals(lecturerId))
                .findFirst();
            
            if (lecturer.isEmpty()) {
                response.put("success", false);
                response.put("message", "Lecturer with ID " + lecturerId + " not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Lecturer lecturerToApprove = lecturer.get();
            lecturerToApprove.setStatus(LecturerStatus.APPROVED);
            lecturerToApprove.setLastUpdatedAt(LocalDateTime.now());
            
            Lecturer saved = lecturerRepository.save(lecturerToApprove);
            
            response.put("success", true);
            response.put("message", "Lecturer approved successfully");
            response.put("lecturer", saved);
            response.put("old_status", "PENDING");
            response.put("new_status", "APPROVED");
            response.put("flow_note", "Lecturer can now create lectures.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/lecturers/status/{lecturerId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateLecturerStatus(
            @PathVariable UUID lecturerId,
            @RequestParam LecturerStatus status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();
            Optional<Lecturer> lecturer = lecturers.stream()
                .filter(l -> l.getUserId().equals(lecturerId))
                .findFirst();
            
            if (lecturer.isEmpty()) {
                response.put("success", false);
                response.put("message", "Lecturer with ID " + lecturerId + " not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Lecturer lecturerToUpdate = lecturer.get();
            LecturerStatus oldStatus = lecturerToUpdate.getStatus();
            
            lecturerToUpdate.setStatus(status);
            lecturerToUpdate.setLastUpdatedAt(LocalDateTime.now());
            
            Lecturer saved = lecturerRepository.save(lecturerToUpdate);
            
            response.put("success", true);
            response.put("message", "Lecturer status updated successfully");
            response.put("lecturer", saved);
            response.put("old_status", oldStatus);
            response.put("new_status", status);
            
            // Add flow notes based on status
            switch (status) {
                case APPROVED:
                    response.put("flow_note", "Lecturer can now create lectures and is visible in searches.");
                    break;
                case PENDING:
                    response.put("flow_note", "Lecturer needs admin approval to create lectures.");
                    break;
                case FREEZE:
                    response.put("flow_note", "Lecturer is frozen and not visible in searches.");
                    break;
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Flow-Aware Lecture Creation ==========

    @PostMapping("/lectures/create-single")
    @Transactional
    public ResponseEntity<Map<String, Object>> createSingleLecture() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createSingleLecture();
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("lecture", result.get("lecture"));
            response.put("assigned_lecturers", result.get("assigned_lecturers"));
            response.put("total_lectures", result.get("total_lectures"));
            response.put("flow_note", "Lecture created with APPROVED lecturers only.");
            
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

    @PostMapping("/lectures/create-multiple")
    @Transactional
    public ResponseEntity<Map<String, Object>> createMultipleLectures(
            @RequestParam(defaultValue = "5") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createMultipleLectures(count);
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("created_count", result.get("created_count"));
            response.put("lectures", result.get("lectures"));
            response.put("total_lectures", result.get("total_lectures"));
            response.put("used_approved_lecturers", result.get("used_approved_lecturers"));
            response.put("flow_note", "All lectures created with APPROVED lecturers only.");
            
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

    @PostMapping("/lectures/create-with-existing-lecturer")
    @Transactional
    public ResponseEntity<Map<String, Object>> createLectureWithExistingLecturer(
            @RequestParam UUID lecturerId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();
            Optional<Lecturer> lecturer = lecturers.stream()
                .filter(l -> l.getUserId().equals(lecturerId))
                .findFirst();
            
            if (lecturer.isEmpty()) {
                response.put("success", false);
                response.put("message", "Lecturer with ID " + lecturerId + " not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.createLectureWithSpecificLecturer(lecturer.get());
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("lecture", result.get("lecture"));
            response.put("lecturer", result.get("lecturer"));
            response.put("lecturer_status", result.get("lecturer_status"));
            response.put("total_lectures", lectureRepository.count());
            
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

    // ========== Auto-Create Lecturer with Flow Support ==========

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
            response.put("lecturer_status", result.get("lecturer_status"));
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

    @PostMapping("/lectures/create-fake-with-new-lecturer")
    @Transactional
    public ResponseEntity<Map<String, Object>> createFakeLectureWithNewLecturer() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createLectureWithLecturerOrCreate(
                null, null, null, true);
            
            response.put("success", true);
            response.put("message", "Created fake lecture with new fake lecturer!");
            response.put("lecture", result.get("lecture"));
            response.put("lecturer", result.get("lecturer"));
            response.put("action", "created_fake_lecturer_and_lecture");
            response.put("lecturer_status", result.get("lecturer_status"));
            response.put("total_lectures", lectureRepository.count());
            response.put("total_lecturers", lecturerRepository.count());
            response.put("flow_note", "New lecturer was auto-approved for demo purposes.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Flow-Aware Search ==========

    @GetMapping("/lecturers/search")
    public ResponseEntity<Map<String, Object>> searchLecturers(@RequestParam String searchTerm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Search term is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            var result = advancedLectureFakerService.searchLecturersByDetails(searchTerm);
            
            response.put("success", true);
            response.put("search_term", result.get("search_term"));
            response.put("matches_found", result.get("matches_found"));
            response.put("matching_lecturers", result.get("matching_lecturers"));
            response.put("total_approved_lecturers", result.get("total_approved_lecturers"));
            response.put("total_lecturers_in_db", result.get("total_lecturers_in_db"));
            response.put("search_scope", result.get("search_scope"));
            response.put("flow_note", "Search results include only APPROVED lecturers.");
            
            if ((Integer) result.get("matches_found") == 0) {
                response.put("message", "No approved lecturers found matching: " + searchTerm);
            } else {
                response.put("message", "Found " + result.get("matches_found") + " approved lecturers");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

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
                response.put("message", "At least one search parameter is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Search among all lecturers (not just approved) for existence check
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            List<Lecturer> matches = allLecturers.stream()
                .filter(lecturer -> {
                    boolean match = false;
                    if (firstName != null && !firstName.trim().isEmpty()) {
                        match = lecturer.getFirstName().toLowerCase().contains(firstName.toLowerCase());
                    }
                    if (lastName != null && !lastName.trim().isEmpty()) {
                        match = match || lecturer.getLastName().toLowerCase().contains(lastName.toLowerCase());
                    }
                    if (email != null && !email.trim().isEmpty()) {
                        match = match || (lecturer.getEmail() != null && lecturer.getEmail().toLowerCase().contains(email.toLowerCase()));
                    }
                    return match;
                })
                .collect(Collectors.toList());
            
            boolean exists = !matches.isEmpty();
            
            response.put("success", true);
            response.put("exists", exists);
            response.put("search_details", Map.of(
                "firstName", firstName != null ? firstName : "",
                "lastName", lastName != null ? lastName : "",
                "email", email != null ? email : ""
            ));
            
            if (exists) {
                response.put("message", "Found " + matches.size() + " matching lecturers");
                response.put("matching_lecturers", matches.stream()
                    .map(lecturer -> Map.of(
                        "id", lecturer.getUserId(),
                        "name", lecturer.getFirstName() + " " + lecturer.getLastName(),
                        "email", lecturer.getEmail(),
                        "status", lecturer.getStatus()
                    ))
                    .collect(Collectors.toList()));
            } else {
                response.put("message", "No matching lecturers found");
            }
            
            response.put("flow_note", "This check searches all lecturers regardless of status.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== System Status ==========

    @GetMapping("/system/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            List<Lecture> allLectures = lectureRepository.findAll();
            
            // Count by status
            Map<LecturerStatus, Long> statusCounts = allLecturers.stream()
                .collect(Collectors.groupingBy(
                    Lecturer::getStatus,
                    Collectors.counting()
                ));
            
            response.put("success", true);
            response.put("total_lecturers", allLecturers.size());
            response.put("total_lectures", allLectures.size());
            response.put("lecturers_by_status", statusCounts);
            response.put("approved_lecturers", statusCounts.getOrDefault(LecturerStatus.APPROVED, 0L));
            response.put("pending_lecturers", statusCounts.getOrDefault(LecturerStatus.PENDING, 0L));
            response.put("frozen_lecturers", statusCounts.getOrDefault(LecturerStatus.FREEZE, 0L));
            
            // Calculate lecture statistics
            long lecturesWithApprovedLecturers = allLectures.stream()
                .filter(lecture -> lecture.getLecturers().stream()
                    .anyMatch(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED))
                .count();
            
            response.put("lectures_with_approved_lecturers", lecturesWithApprovedLecturers);
            response.put("flow_compliance", Map.of(
                "all_lectures_have_approved_lecturers", lecturesWithApprovedLecturers == allLectures.size(),
                "system_ready", statusCounts.getOrDefault(LecturerStatus.APPROVED, 0L) > 0
            ));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
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
            System.out.println("Creating complete system with proper flow...");
            
            Map<String, Object> result = advancedLectureFakerService.createCompleteSystemWithRelations(lecturerCount, lectureCount);
            
            if ((Boolean) result.get("success")) {
                System.out.println("Successfully created complete system following proper flow!");
                System.out.println("- Lecturers: " + result.get("lecturers_created"));
                System.out.println("- Lectures: " + result.get("lectures_created"));
                System.out.println("- Approved Lecturers: " + result.get("approved_lecturers"));
                System.out.println("- Total Relations: " + result.get("total_relations"));
                
                result.put("flow_note", "System created following proper flow: lecturers created → approved → lectures created");
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

    // ========== Existing Lecture Creation Methods (Updated) ==========

    @PostMapping("/lectures/create-israeli")
    @Transactional
    public ResponseEntity<Map<String, Object>> createIsraeliTechLectures(
            @RequestParam(defaultValue = "8") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.createIsraeliTechLectures(count);
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("count", count);
            response.put("lectures", result.get("lectures"));
            response.put("total_in_db", result.get("total_lectures"));
            response.put("approved_lecturers_used", result.get("approved_lecturers_used"));
            response.put("flow_note", "Israeli tech lectures created with APPROVED lecturers only.");
            
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

    @PostMapping("/lectures/create-upcoming")
    @Transactional
    public ResponseEntity<Map<String, Object>> createUpcomingLectures(
            @RequestParam(defaultValue = "6") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.generateUpcomingLectures(count);
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("count", count);
            response.put("lectures", result.get("lectures"));
            response.put("total_in_db", result.get("total_lectures"));
            response.put("approved_lecturers_used", result.get("approved_lecturers_used"));
            response.put("flow_note", "Upcoming lectures created with APPROVED lecturers only.");
            
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

    @PostMapping("/lectures/create-with-lecturers")
    @Transactional
    public ResponseEntity<Map<String, Object>> createLectureWithLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var result = advancedLectureFakerService.generateSingleLectureWithLecturers();
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("lecture", result.get("lecture"));
            response.put("attached_lecturers", result.get("attached_lecturers"));
            response.put("approved_lecturers_available", result.get("approved_lecturers_available"));
            response.put("flow_note", "Lecture created with APPROVED lecturers only.");
            
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

    // ========== Data Retrieval ==========

    @GetMapping("/lecturers/available")
    public ResponseEntity<Map<String, Object>> getAvailableLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            List<Lecturer> approvedLecturers = allLecturers.stream()
                .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("total_lecturers", allLecturers.size());
            response.put("approved_count", approvedLecturers.size());
            response.put("approved_lecturers", approvedLecturers);
            response.put("flow_note", "Only showing APPROVED lecturers who can create lectures.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lecturers/all-statuses")
    public ResponseEntity<Map<String, Object>> getAllLecturersWithStatuses() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            
            Map<LecturerStatus, List<Lecturer>> lecturersByStatus = allLecturers.stream()
                .collect(Collectors.groupingBy(Lecturer::getStatus));
            
            response.put("success", true);
            response.put("total_lecturers", allLecturers.size());
            response.put("lecturers_by_status", lecturersByStatus);
            response.put("status_counts", lecturersByStatus.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue().size()
                )));
            response.put("flow_note", "Showing all lecturers grouped by status.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
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
            response.put("flow_note", "Lecturers remain in the system with their current statuses.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/lecturers/clear")
    @Transactional
    public ResponseEntity<Map<String, Object>> clearAllLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long lectureCountBefore = lectureRepository.count();
            long lecturerCountBefore = lecturerRepository.count();
            
            // Clear lectures first (due to foreign key constraints)
            lectureRepository.deleteAll();
            lecturerRepository.deleteAll();
            
            response.put("success", true);
            response.put("message", "All lecturers and their lectures deleted successfully");
            response.put("deleted_lecturers", lecturerCountBefore);
            response.put("deleted_lectures", lectureCountBefore);
            response.put("flow_note", "System reset - ready for new data creation following proper flow.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== Utility Endpoints ==========

    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getAvailableEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        endpoints.put("lecturer_management", List.of(
            "POST /api/advanced-faker/lecturers/create-single - Create single lecturer (PENDING status)",
            "POST /api/advanced-faker/lecturers/create-multiple?count=5 - Create multiple lecturers (PENDING status)",
            "GET /api/advanced-faker/lecturers/pending - Get pending lecturers",
            "POST /api/advanced-faker/lecturers/approve-all - Approve all pending lecturers",
            "POST /api/advanced-faker/lecturers/approve/{lecturerId} - Approve specific lecturer",
            "PUT /api/advanced-faker/lecturers/status/{lecturerId}?status=APPROVED - Update lecturer status"
        ));

        endpoints.put("lecture_creation", List.of(
            "POST /api/advanced-faker/lectures/create-single - Create single lecture (requires approved lecturers)",
            "POST /api/advanced-faker/lectures/create-multiple?count=5 - Create multiple lectures (requires approved lecturers)",
            "POST /api/advanced-faker/lectures/create-with-existing-lecturer?lecturerId=... - Create lecture with specific lecturer",
            "POST /api/advanced-faker/lectures/create-israeli?count=8 - Create Israeli tech lectures",
            "POST /api/advanced-faker/lectures/create-upcoming?count=6 - Create upcoming lectures"
        ));

        endpoints.put("auto_create", List.of(
            "POST /api/advanced-faker/lectures/create-with-auto-create-lecturer?firstName=...&lastName=...&email=... - Create lecture with auto-created lecturer",
            "POST /api/advanced-faker/lectures/create-fake-with-new-lecturer - Create fake lecture with new fake lecturer"
        ));
        
        endpoints.put("search", List.of(
            "GET /api/advanced-faker/lecturers/search?searchTerm=... - Search approved lecturers",
            "GET /api/advanced-faker/lecturers/check-exists?firstName=...&lastName=...&email=... - Check if lecturer exists (all statuses)"
        ));

        endpoints.put("system", List.of(
            "GET /api/advanced-faker/system/status - Get system status and flow compliance",
            "POST /api/advanced-faker/create-complete-system?lecturerCount=10&lectureCount=20 - Create complete system with proper flow"
        ));

        endpoints.put("data_retrieval", List.of(
            "GET /api/advanced-faker/lecturers/available - Get approved lecturers only",
            "GET /api/advanced-faker/lecturers/all-statuses - Get all lecturers grouped by status"
        ));

        endpoints.put("cleanup", List.of(
            "DELETE /api/advanced-faker/lectures/clear - Clear all lectures",
            "DELETE /api/advanced-faker/lecturers/clear - Clear all lecturers and lectures"
        ));

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Flow-Aware Faker Controller - Following Proper Authentication Flow! 🚀",
            "endpoints", endpoints,
            "flow_requirements", List.of(
                "1. Lecturers are created in PENDING status",
                "2. Admin must approve lecturers before they can create lectures",
                "3. Only APPROVED lecturers can create lectures",
                "4. Only APPROVED lecturers are visible in searches",
                "5. FREEZE status makes lecturers invisible in searches",
                "6. System respects the authentication and authorization flow"
            ),
            "flow_example", List.of(
                "1. POST /lecturers/create-single (creates lecturer in PENDING)",
                "2. POST /lecturers/approve-all (admin approves lecturers)",
                "3. POST /lectures/create-single (creates lecture with approved lecturers)",
                "4. GET /lecturers/search?searchTerm=... (searches only approved lecturers)"
            ),
            "timestamp", LocalDateTime.now()
        ));
    }
}