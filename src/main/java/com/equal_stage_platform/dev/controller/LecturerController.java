package com.equal_stage_platform.dev.controller;

import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LecturerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * בקר עבור ניהול פעולות מול ישות Lecturer (מרצה).
 */
@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerRepository lecturerRepository;

    /**
     * נקודת קצה להחזרת כל המרצים במערכת.
     * דוגמה לקריאה: GET /api/lecturers
     *
     * @return רשימה של כל המרצים
     */
    @GetMapping
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    /**
     * נקודת קצה לחיפוש מרצים לפי שם (חיפוש חלקי, ללא תלות באותיות גדולות/קטנות).
     * דוגמה לקריאה: GET /api/lecturers/search?name=דן
     *
     * @param name מחרוזת חיפוש של שם המרצה
     * @return רשימה של מרצים שמתאימים לשם
     */
    @GetMapping("/search")
    public List<Lecturer> searchLecturers(@RequestParam String name) {
        return lecturerRepository.findByNameContainingIgnoreCase(name);
    }
}
