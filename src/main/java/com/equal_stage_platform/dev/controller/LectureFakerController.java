package com.equal_stage_platform.dev.controller;

import com.equal_stage_platform.dev.fake.LectureFakerService;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faker/lectures")
public class LectureFakerController {

    @Autowired
    private LectureFakerService lectureFakerService;

    @Autowired
    private LectureRepository lectureRepository;

    @PostMapping("/create-single")
    @Transactional
    public Map<String, Object> createSingleLecture() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Creating single lecture using your original method...");
            
            // משתמש בפונקציה המקורית שלך
            Lecture lecture = lectureFakerService.generateFakeLecture();
            Lecture saved = lectureRepository.save(lecture);
            
            System.out.println("Lecture created: " + saved.getTitle());
            
            response.put("success", true);
            response.put("message", "Single lecture created successfully!");
            response.put("lecture", saved);
            response.put("total_lectures", lectureRepository.count());
            
        } catch (Exception e) {
            System.err.println("Error creating single lecture: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/create-multiple")
    @Transactional
    public Map<String, Object> createMultipleLectures(@RequestParam(defaultValue = "5") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Creating " + count + " lectures using your enhanced method...");
            
            // משתמש בפונקציה החדשה שמבוססת על הקוד שלך
            List<Lecture> lectures = lectureFakerService.createFakeLectures(count);
            
            System.out.println("Created " + lectures.size() + " lectures successfully");
            
            response.put("success", true);
            response.put("message", "Created " + lectures.size() + " lectures successfully!");
            response.put("count", lectures.size());
            response.put("lectures", lectures);
            response.put("total_in_db", lectureRepository.count());
            
        } catch (Exception e) {
            System.err.println("Error creating multiple lectures: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/create-israeli")
    @Transactional
    public Map<String, Object> createIsraeliTechLectures(@RequestParam(defaultValue = "8") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Creating " + count + " Israeli tech lectures...");
            
            List<Lecture> lectures = lectureFakerService.createIsraeliTechLectures(count);
            
            System.out.println("Created " + lectures.size() + " Israeli tech lectures");
            
            response.put("success", true);
            response.put("message", "Created " + lectures.size() + " Israeli tech lectures!");
            response.put("count", lectures.size());
            response.put("lectures", lectures);
            response.put("total_in_db", lectureRepository.count());
            
        } catch (Exception e) {
            System.err.println("Error creating Israeli tech lectures: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/create-upcoming")
    @Transactional
    public Map<String, Object> createUpcomingLectures(@RequestParam(defaultValue = "6") int count) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Creating " + count + " upcoming lectures...");
            
            List<Lecture> lectures = lectureFakerService.generateUpcomingLectures(count);
            
            System.out.println("Created " + lectures.size() + " upcoming lectures");
            
            response.put("success", true);
            response.put("message", "Created " + lectures.size() + " upcoming lectures!");
            response.put("count", lectures.size());
            response.put("lectures", lectures);
            response.put("total_in_db", lectureRepository.count());
            
        } catch (Exception e) {
            System.err.println("Error creating upcoming lectures: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/create-with-lecturers")
    @Transactional
    public Map<String, Object> createLectureWithLecturers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Creating lecture with attached lecturers...");
            
            Lecture lecture = lectureFakerService.generateSingleLectureWithLecturers();
            
            System.out.println("Created lecture with " + lecture.getLecturers().size() + " lecturers");
            
            response.put("success", true);
            response.put("message", "Lecture created with lecturers!");
            response.put("lecture", lecture);
            response.put("attached_lecturers", lecture.getLecturers().size());
            
        } catch (Exception e) {
            System.err.println("Error creating lecture with lecturers: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/all")
    public Map<String, Object> getAllLectures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            
            response.put("success", true);
            response.put("count", lectures.size());
            response.put("lectures", lectures);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/upcoming")
    public Map<String, Object> getUpcomingLectures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecture> allLectures = lectureRepository.findAll();
            List<Lecture> upcomingLectures = allLectures.stream()
                .filter(lecture -> lecture.getStartTime().isAfter(LocalDateTime.now()))
                .filter(Lecture::getIsAvailable)
                .sorted((l1, l2) -> l1.getStartTime().compareTo(l2.getStartTime()))
                .toList();
            
            response.put("success", true);
            response.put("count", upcomingLectures.size());
            response.put("upcoming_lectures", upcomingLectures);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @DeleteMapping("/clear")
    @Transactional
    public Map<String, Object> clearAllLectures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long countBefore = lectureRepository.count();
            lectureRepository.deleteAll();
            
            response.put("success", true);
            response.put("message", "All lectures deleted successfully");
            response.put("deleted_count", countBefore);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/stats")
    public Map<String, Object> getLectureStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            
            long onlineLectures = lectures.stream().filter(Lecture::getIsOnline).count();
            long availableLectures = lectures.stream().filter(Lecture::getIsAvailable).count();
            long upcomingLectures = lectures.stream()
                .filter(lecture -> lecture.getStartTime().isAfter(LocalDateTime.now()))
                .count();
            
            double avgPrice = lectures.stream()
                .mapToInt(Lecture::getPrice)
                .average()
                .orElse(0.0);
            
            response.put("success", true);
            response.put("total_lectures", lectures.size());
            response.put("online_lectures", onlineLectures);
            response.put("physical_lectures", lectures.size() - onlineLectures);
            response.put("available_lectures", availableLectures);
            response.put("upcoming_lectures", upcomingLectures);
            response.put("average_price", Math.round(avgPrice * 100.0) / 100.0);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }
}