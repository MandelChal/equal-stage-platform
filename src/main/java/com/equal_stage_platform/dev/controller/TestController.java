package com.equal_stage_platform.dev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.model.Lecturer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @PostMapping("/create-lecturer")
    public Map<String, Object> createSingleLecturer() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // יצירת lecturer פשוט
            Lecturer lecturer = new Lecturer();
            lecturer.setFirstName("Test");
            lecturer.setLastName("User");
            lecturer.setEmail("test" + System.currentTimeMillis() + "@example.com");
            lecturer.setPhone("0501234567");
            lecturer.setCity("Tel Aviv");
            lecturer.setBio("Test bio");
            lecturer.setCreatedAt(LocalDateTime.now());
            
            // שמירה
            Lecturer saved = lecturerRepository.save(lecturer);
            
            response.put("success", true);
            response.put("message", "Lecturer created successfully");
            response.put("lecturer", saved);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            e.printStackTrace(); // זה יופיע בלוגים
        }
        
        return response;
    }

    @GetMapping("/count")
    public Map<String, Object> getCounts() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long lecturerCount = lecturerRepository.count();
            long lectureCount = lectureRepository.count();
            
            response.put("success", true);
            response.put("lecturer_count", lecturerCount);
            response.put("lecture_count", lectureCount);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }
}