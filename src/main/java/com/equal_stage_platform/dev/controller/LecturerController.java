// LecturerController.java
package com.equal_stage_platform.dev.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.service.LecturerService;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    // כל המרצים
    @GetMapping
    public ResponseEntity<List<ResponseLecturerDTO>> getAllLecturers() {
        return ResponseEntity.ok(lecturerService.getAllLecturers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseLecturerDTO> getLecturerById(@PathVariable Long userId) {
        return ResponseEntity.ok(lecturerService.getLecturerById(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResponseLecturerDTO>> searchLecturersByName(@RequestParam String name) {
        return ResponseEntity.ok(lecturerService.searchLecturersByName(name));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        return ResponseEntity.ok(lecturerService.getAllCities());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<ResponseLecturerDTO>> getLecturersByCity(@PathVariable String city) {
        return ResponseEntity.ok(lecturerService.getLecturersByCity(city));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseLecturerDTO> createLecturer(@RequestBody CreateLecturerDTO createDTO) {
        return ResponseEntity.ok(lecturerService.createLecturer(createDTO));
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteLecturer(@PathVariable Long userId) {
        lecturerService.deleteLecturer(userId);
        return ResponseEntity.ok("Lecturer deleted successfully");
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getLecturersCount() {
        return ResponseEntity.ok(lecturerService.getLecturersCount());
    }
}