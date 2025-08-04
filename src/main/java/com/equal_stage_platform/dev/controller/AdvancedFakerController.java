package com.equal_stage_platform.dev.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.ApiResponseDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.service.FakerDataService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/faker")
@CrossOrigin(origins = "*")
public class AdvancedFakerController {

    @Autowired
    private FakerDataService fakerDataService;

    /**
     * Creates a new user with fake data
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createFakeUser() {
        try {
            Map<String, Object> userData = fakerDataService.createFakeUser();
            
            ApiResponseDTO<Map<String, Object>> response = ApiResponseDTO.success(
                userData, 
                "New fake user created successfully: " + userData.get("firstName") + " " + userData.get("lastName")
            );
            
            logControllerAction("Create Fake User", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logControllerAction("Create Fake User", false);
            ApiResponseDTO<Map<String, Object>> errorResponse = ApiResponseDTO.error(
                "Error creating user: " + e.getMessage(),
                "USER_CREATION_FAILED"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Creates a new lecturer with automatic admin approval
     */
    @PostMapping("/create-lecturer")
    public ResponseEntity<ApiResponseDTO<ResponseLecturerDTO>> createApprovedLecturer() {
        try {
            ResponseLecturerDTO lecturer = fakerDataService.createApprovedLecturer();
            
            ApiResponseDTO<ResponseLecturerDTO> response = ApiResponseDTO.success(
                lecturer,
                "New lecturer created and automatically approved: " + 
                lecturer.getFirstName() + " " + lecturer.getLastName()
            );
            
            logControllerAction("Create Approved Lecturer", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logControllerAction("Create Approved Lecturer", false);
            ApiResponseDTO<ResponseLecturerDTO> errorResponse = ApiResponseDTO.error(
                "Error creating lecturer: " + e.getMessage(),
                "LECTURER_CREATION_FAILED"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Creates a new lecturer without automatic approval (PENDING status)
     */
    @PostMapping("/create-lecturer-pending")
    public ResponseEntity<ApiResponseDTO<ResponseLecturerDTO>> createPendingLecturer() {
        try {
            ResponseLecturerDTO lecturer = fakerDataService.createPendingLecturer();
            
            ApiResponseDTO<ResponseLecturerDTO> response = ApiResponseDTO.success(
                lecturer,
                "New lecturer created and is pending approval: " + 
                lecturer.getFirstName() + " " + lecturer.getLastName()
            );
            
            logControllerAction("Create Pending Lecturer", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logControllerAction("Create Pending Lecturer", false);
            ApiResponseDTO<ResponseLecturerDTO> errorResponse = ApiResponseDTO.error(
                "Error creating lecturer: " + e.getMessage(),
                "LECTURER_CREATION_FAILED"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Creates a new lecture with a new approved lecturer
     */
    @PostMapping("/create-lecture")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createLectureWithApprovedLecturer() {
        try {
            Map<String, Object> lectureData = fakerDataService.createLectureWithApprovedLecturer();
            
            ApiResponseDTO<Map<String, Object>> response = ApiResponseDTO.success(
                lectureData,
                "New lecture created with a new approved lecturer"
            );
            
            logControllerAction("Create Lecture with New Lecturer", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logControllerAction("Create Lecture with New Lecturer", false);
            ApiResponseDTO<Map<String, Object>> errorResponse = ApiResponseDTO.error(
                "Error creating lecture: " + e.getMessage(),
                "LECTURE_CREATION_FAILED"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Creates a new lecture using an existing approved lecturer
     */
    @PostMapping("/create-lecture-existing")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createLectureWithExistingLecturer() {
        try {
            Map<String, Object> lectureData = fakerDataService.createLectureWithExistingLecturer();
            
            ApiResponseDTO<Map<String, Object>> response = ApiResponseDTO.success(
                lectureData,
                "New lecture created with an existing lecturer"
            );
            
            logControllerAction("Create Lecture with Existing Lecturer", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logControllerAction("Create Lecture with Existing Lecturer", false);
            ApiResponseDTO<Map<String, Object>> errorResponse = ApiResponseDTO.error(
                "Error creating lecture: " + e.getMessage(),
                "LECTURE_CREATION_FAILED"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Initializes complete system with lecturers and lectures
     */
    @PostMapping("/initSystem")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> initializeSystem(
            @Valid @RequestBody Map<String, Integer> request) {
        
        try {
            // Validate input
            Integer lecturersCount = request.get("lecturersCount");
            Integer lecturesPerLecturer = request.get("lecturesPerLecturer");
            
            if (lecturersCount == null || lecturesPerLecturer == null || 
                lecturersCount <= 0 || lecturesPerLecturer <= 0) {
                ApiResponseDTO<Map<String, Object>> errorResponse = ApiResponseDTO.error(
                    "Lecturers count and lectures per lecturer must be greater than 0",
                    "INVALID_INPUT"
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (lecturersCount > 100 || lecturesPerLecturer > 20) {
                ApiResponseDTO<Map<String, Object>> errorResponse = ApiResponseDTO.error(
                    "Maximum limits exceeded: 100 lecturers, 20 lectures per lecturer",
                    "LIMITS_EXCEEDED"
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Map<String, Object> systemData = fakerDataService.initializeCompleteSystem(
                lecturersCount, 
                lecturesPerLecturer
            );
            
            ApiResponseDTO<Map<String, Object>> response = ApiResponseDTO.success(
                systemData,
                "System initialized successfully! Created " + systemData.get("lecturersCreated") + 
                " lecturers and " + systemData.get("lecturesCreated") + " lectures"
            );
            
            logControllerAction("Initialize Complete System", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logControllerAction("Initialize Complete System", false);
            ApiResponseDTO<Map<String, Object>> errorResponse = ApiResponseDTO.error(
                "Error initializing system: " + e.getMessage(),
                "SYSTEM_INIT_FAILED"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ========== Private Helper Methods ==========

    /**
     * Logs controller action to console
     */
    private void logControllerAction(String action, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        System.out.println(String.format("Controller Action - %s: %s", action, status));
    }
}