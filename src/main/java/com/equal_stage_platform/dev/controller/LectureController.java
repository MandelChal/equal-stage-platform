
package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.service.LectureService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.service.JwtService;
import com.equal_stage_platform.dev.exception.LectureException;
import com.equal_stage_platform.dev.exception.AuthException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;
    private final JwtService jwtService;
    // private static final Logger logger = LoggerFactory.getLogger(LectureController.class);
    public LectureController(LectureService lectureService, JwtService jwtService) {
        this.lectureService = lectureService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    // @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')") // Only lecturers and admins can create lectures
    public ResponseEntity<?> createLecture(@RequestHeader("Authorization") String token, @Valid @RequestBody CreateLectureDTO lectureData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            lectureData.setUserId(userId);
            // logger.info("Attempting to create lecture for user ID: {}", userId);
            ResponseEntity<?> response = ResponseEntity.status(HttpStatus.CREATED).body(lectureService.createLecture(lectureData));
            // logger.info("Lecture created successfully for user ID: {}", userId);
            return response;
        } catch (LectureException e) {
            // logger.error("LectureException while creating lecture for user ID: {}", jwtService.extractUserId(token.replace("Bearer ", "")), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while creating lecture for user ID: {}", jwtService.extractUserId(token.replace("Bearer ", "")), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while creating lecture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/update/{lectureId}/status/{status}")
    // @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')") // Only lecturers and admins can create lectures
    public ResponseEntity<?> updateLectureStatus(@RequestHeader("Authorization") String token, @PathVariable Long lectureId, @PathVariable LectureStatus status) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.ok(lectureService.updateLectureStatus(userId, lectureId, status));
        } catch (LectureException e) {
            // logger.error("LectureException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    // retrive lectures of approved lecturers and ON_AIR status
    @GetMapping("/all")
    public ResponseEntity<?> getAllLectures() {
        try {
            return ResponseEntity.ok(lectureService.getAllLectures()); 
        } catch (LectureException e) {
            // logger.error("LectureException while fetching all lectures", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching all lectures", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/admin/all")
    // @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<?> getAllLecturesForAdmin() {
        try {
            return ResponseEntity.ok(lectureService.getAllLecturesAdmin());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching all lectures", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching all lectures", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<?> getLectureById(@PathVariable Long lectureId) {
        return getLectureByIdAdmin(lectureId, false);
    }

    @GetMapping("/admin/{lectureId}")
    public ResponseEntity<?> getLectureByIdAdmin(@PathVariable Long lectureId) {
        return getLectureByIdAdmin(lectureId, true);
    }

    private ResponseEntity<?> getLectureByIdAdmin(Long lectureId, boolean isAdmin) {
        try {
            return ResponseEntity.ok(lectureService.getLectureById(lectureId, isAdmin));
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/all/isOnline")
    public ResponseEntity<?> getOnlineLectures() {
        try {
            return ResponseEntity.ok(lectureService.getAllOnlineLectures());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/search/{lectureTitle}")
    public ResponseEntity<?> searchLectureByTitle(@PathVariable String lectureTitle) {
        return searchLectureByTitle(lectureTitle, false);
    }

    @GetMapping("/admin/search/{lectureTitle}")
    public ResponseEntity<?> searchLectureByTitleAdmin(@PathVariable String lectureTitle) {
        return searchLectureByTitle(lectureTitle, true);
    }

    private ResponseEntity<?> searchLectureByTitle(String lectureTitle, boolean isAdmin) {
        try {
            return ResponseEntity.ok(lectureService.getLectureByTitle(lectureTitle, isAdmin));
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/physical")
    public ResponseEntity<?> getPhysicalLectures() {
        try {
            return ResponseEntity.ok(lectureService.getPhysicalLectures());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/del/{lectureId}")
    // @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')") // Only lecturers and admins can delete lectures
    public ResponseEntity<?> deleteLecture(@RequestHeader("Authorization") String token, @PathVariable Long lectureId) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.ok(lectureService.deleteLecture(userId, lectureId)); 
        } catch (LectureException e) {
            // logger.error("LectureException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}


