package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.service.LecturerService;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.equal_stage_platform.dev.model.enums.LecturerStatus;

@RestController
@RequestMapping("/lecturers")
public class LecturerController {
    private final LecturerService lecturerService;
    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseLecturerDTO> createLecturer(@RequestBody CreateLecturerDTO lecturerData) {
        return ResponseEntity.ok(lecturerService.createLecturer(lecturerData));
    }

    @PatchMapping("/update/{lecturerId}/status/{status}")
    public ResponseEntity<ResponseLecturerDTO> updateLecturerStatus(@PathVariable UUID userId, @PathVariable LecturerStatus status) {
        return ResponseEntity.ok(lecturerService.updateLecturerStatus(userId, status));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseLecturerDTO>> getAllLecturers() {
        return ResponseEntity.ok(lecturerService.getAllLecturers());
    }

    @GetMapping("/all/approved")
    public ResponseEntity<List<ResponseLecturerDTO>> getAllApprovedLecturers() {
        return ResponseEntity.ok(lecturerService.getLecturersByStatus(LecturerStatus.APPROVED));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseLecturerDTO> getLecturerById(@PathVariable UUID userId) {
        return ResponseEntity.ok(lecturerService.getLecturerById(userId));
    }

    @GetMapping("/lectures/{lecturerId}/all")
    public ResponseEntity<List<ResponseLectureDTO>> getLecturesByLecturerId(@PathVariable UUID lecturerId) {
        return ResponseEntity.ok(lecturerService.getLecturesByLecturerId(lecturerId));
    }

    @GetMapping("/lectures/{lecturerId}/{lectureId}")
    public ResponseEntity<ResponseLectureDTO> getLectureById(@PathVariable UUID lecturerId, @PathVariable Long lectureId) {
        return ResponseEntity.ok(lecturerService.getLectureById(lecturerId, lectureId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ResponseLecturerDTO>> getPendingLecturers() {
        return ResponseEntity.ok(lecturerService.getLecturersByStatus(LecturerStatus.PENDING));
    }

    @PostMapping("/approve/{lecturerId}")
    public ResponseEntity<ResponseLecturerDTO> approveLecturer(@PathVariable UUID lecturerId) {
        return ResponseEntity.ok(lecturerService.updateLecturerStatus(lecturerId, LecturerStatus.APPROVED));
    }

    @PostMapping("/reject/{lecturerId}")
    public ResponseEntity<ResponseLecturerDTO> rejectLecturer(@PathVariable UUID lecturerId) {
        return ResponseEntity.ok(lecturerService.updateLecturerStatus(lecturerId, LecturerStatus.REJECTED));
    }
}
