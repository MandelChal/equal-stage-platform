package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.service.LectureService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.equal_stage_platform.dev.model.enums.LectureStatus;

@RestController
@RequestMapping("/lectures")
public class LectureController {

    private final LectureService lectureService;
    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseLectureDTO> createLecture(@RequestBody CreateLectureDTO lectureData) {
        return ResponseEntity.ok(lectureService.createLecture(lectureData));
    }

    @PatchMapping("/update/{lectureId}/status/{status}")
    public ResponseEntity<ResponseLectureDTO> updateLectureStatus(@PathVariable Long lectureId, @PathVariable LectureStatus status) {
        return ResponseEntity.ok(lectureService.updateLectureStatus(lectureId, status));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseLectureDTO>> getAllLectures() {
        return ResponseEntity.ok(lectureService.getAllLectures());
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<ResponseLectureDTO> getLectureById(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureById(lectureId));
    }

    
    
}
