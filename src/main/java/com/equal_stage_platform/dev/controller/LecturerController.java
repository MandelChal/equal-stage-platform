package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.service.LecturerService;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.service.JwtService;
import com.equal_stage_platform.dev.exception.LecturerException;
import com.equal_stage_platform.dev.exception.LectureException;
import com.equal_stage_platform.dev.exception.AuthException;

@RestController
@RequestMapping("/lecturers")
public class LecturerController {
    // If you want to log errors, uncomment the next line:
    // private static final Logger logger = LoggerFactory.getLogger(LecturerController.class);

    private final LecturerService lecturerService;
    private final JwtService jwtService;
    public LecturerController(LecturerService lecturerService, JwtService jwtService) {
        this.lecturerService = lecturerService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLecturer(@RequestHeader("Authorization") String token, @RequestBody CreateLecturerDTO lecturerData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            lecturerData.setUserId(userId);
            return ResponseEntity.ok(lecturerService.createLecturer(lecturerData));
        } catch (LecturerException e) {
            // logger.error("LecturerException while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //TODO - verify if to get userId from token or from path variable
    //TODO - think of generic way to keep same code for lecturer and admin
    @PatchMapping("/update/{lecturerId}/status/{status}")
    public ResponseEntity<?> updateLecturerStatus(@PathVariable UUID userId, @PathVariable LecturerStatus status) {
        try {
            return ResponseEntity.ok(lecturerService.updateLecturerStatus(userId, status));
        } catch (LecturerException e) {
            // logger.error("LecturerException while updating lecturer status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecturer status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLecturers() {
        try {
            return ResponseEntity.ok(lecturerService.getAllLecturers());
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching all lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching all lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/all/approved")
    public ResponseEntity<?> getAllApprovedLecturers() {
        try {
            return ResponseEntity.ok(lecturerService.getLecturersByStatus(LecturerStatus.APPROVED));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching approved lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching approved lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("searchById/{userId}")
    public ResponseEntity<?> getLecturerById(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(lecturerService.getLecturerById(userId));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching lecturer by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecturer by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/lectures/{lecturerId}/all")
    public ResponseEntity<?> getLecturesByLecturerId(@PathVariable UUID lecturerId) {
        try {
            return ResponseEntity.ok(lecturerService.getLecturesByLecturerId(lecturerId));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching lectures by lecturer ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lectures by lecturer ID", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lectures by lecturer ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/lectures/{lecturerId}/{lectureId}")
    public ResponseEntity<?> getLectureById(@PathVariable UUID lecturerId, @PathVariable Long lectureId) {
        try {
            return ResponseEntity.ok(lecturerService.getLectureById(lecturerId, lectureId));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingLecturers() {
        try {
            return ResponseEntity.ok(lecturerService.getLecturersByStatus(LecturerStatus.PENDING));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching pending lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching pending lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/approve/{lecturerId}")
    public ResponseEntity<?> approveLecturer(@PathVariable UUID lecturerId) {
        try {
            return ResponseEntity.ok(lecturerService.updateLecturerStatus(lecturerId, LecturerStatus.APPROVED));
        } catch (LecturerException e) {
            // logger.error("LecturerException while approving lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while approving lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/reject/{lecturerId}")
    public ResponseEntity<?> rejectLecturer(@PathVariable UUID lecturerId) {
        try {
            return ResponseEntity.ok(lecturerService.updateLecturerStatus(lecturerId, LecturerStatus.REJECTED));
        } catch (LecturerException e) {
            // logger.error("LecturerException while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //TODO - replaced @RequestParam String name TO , added {name} to route 
    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchLecturersByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(lecturerService.searchLecturersByName(name));
        } catch (LecturerException e) {
            // logger.error("LecturerException while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // TODO -> 1. verify that delete lecturer will deletes its lectures
    // TODO -> 2. make sure that lecturerService.deleteLecturer will return "Lecturer deleted successfully" on success
    // TODO -> 3. changed "@PathVariable Long userId" TO "@PathVariable UUID lecturerId" - implement the change in lecturerService.deleteLecturer
    // TODO -> 4. make sure that 
    @DeleteMapping("del/{userId}")
    public ResponseEntity<String> deleteLecturer(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(lecturerService.deleteLecturer(userId);); //TODO - make sure that lecturerService.deleteLecturer will return "Lecturer deleted successfully" on success
        } catch (LecturerException e) {
            // logger.error("LecturerException while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("delLecturerProfile")
    public ResponseEntity<String> deleteLecturer(@RequestHeader("Authorization") String token) { //TODO -> verify that delete lecturer will deletes its lectures
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.ok(lecturerService.deleteLecturer(userId)); //TODO - make sure that lecturerService.deleteLecturer will return "Lecturer deleted successfully" on success
        } catch (LecturerException e) {
            // logger.error("LecturerException while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}