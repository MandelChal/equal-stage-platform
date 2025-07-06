
package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.service.LectureService;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.service.JwtService;
import com.equal_stage_platform.dev.exception.LectureException;
import com.equal_stage_platform.dev.exception.AuthException;

@RestController
@RequestMapping("/lectures")
public class LectureController {
    //TODO: check each endpoint is registered in config

    private final LectureService lectureService;
    private final JwtService jwtService;
    public LectureController(LectureService lectureService, JwtService jwtService) {
        this.lectureService = lectureService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    // @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')") // Only lecturers and admins can create lectures
    public ResponseEntity<?> createLecture(@RequestHeader("Authorization") String token, @RequestBody CreateLectureDTO lectureData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            lectureData.setUserId(userId);
            return ResponseEntity.ok(lectureService.createLecture(lectureData));
        } catch (LectureException e) {
            // logger.error("LectureException while creating lecture", e);
            return e.getMessage().equals("Lecturer is not approved") ? ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while creating lecture", e);
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

    @GetMapping("/allAdmin")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
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
        try {
            return ResponseEntity.ok(lectureService.getLectureById(lectureId));
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
        try {
            return ResponseEntity.ok(lectureService.getLectureByTitle(lectureTitle));
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

    // מחיקת הרצאה
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


