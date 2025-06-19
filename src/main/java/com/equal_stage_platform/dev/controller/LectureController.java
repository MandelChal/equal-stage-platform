// LectureController.java
package com.equal_stage_platform.dev.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;

import com.equal_stage_platform.dev.service.LectureService;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // יצירת הרצאה חדשה
    @PostMapping("/create")
    public ResponseEntity<ResponseLectureDTO> createLecture(@RequestBody CreateLectureDTO lectureData) {
        return ResponseEntity.ok(lectureService.createLecture(lectureData));
    }

    // // עדכון סטטוס הרצאה
    // @PatchMapping("/update/{lectureId}/status/{status}")
    // public ResponseEntity<ResponseLectureDTO> updateLectureStatus(@PathVariable Long lectureId, @PathVariable LectureStatus status) {
    //     return ResponseEntity.ok(lectureService.updateLectureStatus(lectureId, status));
    // }

    // כל ההרצאות
    @GetMapping("/all")
    public ResponseEntity<List<ResponseLectureDTO>> getAllLectures() {
        return ResponseEntity.ok(lectureService.getAllLectures());
    }

    // כל ההרצאות (alias)
    @GetMapping
    public ResponseEntity<List<ResponseLectureDTO>> getLectures() {
        return ResponseEntity.ok(lectureService.getAllLectures());
    }

    // הרצאה ספציפית
    @GetMapping("/{lectureId}")
    public ResponseEntity<ResponseLectureDTO> getLectureById(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureById(lectureId));
    }

    // הרצאות זמינות בלבד
    @GetMapping("/available")
    public ResponseEntity<List<ResponseLectureDTO>> getAvailableLectures() {
        return ResponseEntity.ok(lectureService.getAvailableLectures());
    }

    // הרצאות אונליין
    @GetMapping("/online")
    public ResponseEntity<List<ResponseLectureDTO>> getOnlineLectures() {
        return ResponseEntity.ok(lectureService.getOnlineLectures());
    }

    // הרצאות פיזיות
    @GetMapping("/physical")
    public ResponseEntity<List<ResponseLectureDTO>> getPhysicalLectures() {
        return ResponseEntity.ok(lectureService.getPhysicalLectures());
    }

    // הרצאות עתידיות
    @GetMapping("/future")
    public ResponseEntity<List<ResponseLectureDTO>> getFutureLectures() {
        return ResponseEntity.ok(lectureService.getFutureLectures());
    }

    // חיפוש הרצאות לפי כותרת
    @GetMapping("/search")
    public ResponseEntity<List<ResponseLectureDTO>> searchLecturesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(lectureService.searchLecturesByTitle(title));
    }

    // רשימת כל המיקומים
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getAllLocations() {
        return ResponseEntity.ok(lectureService.getAllLocations());
    }

    // הרצאות לפי מיקום
    @GetMapping("/location/{location}")
    public ResponseEntity<List<ResponseLectureDTO>> getLecturesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(lectureService.getLecturesByLocation(location));
    }

    // הרצאות לפי מרצה
    @GetMapping("/lecturer/{lecturerId}")
    public ResponseEntity<List<ResponseLectureDTO>> getLecturesByLecturer(@PathVariable Long lecturerId) {
        return ResponseEntity.ok(lectureService.getLecturesByLecturer(lecturerId));
    }

    // מחיקת הרצאה
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<String> deleteLecture(@PathVariable Integer lectureId) {
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.ok("Lecture deleted successfully");
    }

    // ספירת הרצאות
    @GetMapping("/count")
    public ResponseEntity<Long> getLecturesCount() {
        return ResponseEntity.ok(lectureService.getLecturesCount());
    }

    // הרצאות זמינות ועתידיות
    @GetMapping("/available-future")
    public ResponseEntity<List<ResponseLectureDTO>> getAvailableAndFutureLectures() {
        return ResponseEntity.ok(lectureService.getAvailableLectures());
    }
}