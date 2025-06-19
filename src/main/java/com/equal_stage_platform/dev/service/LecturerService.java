package com.equal_stage_platform.dev.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.dto.SimpleLectureDTO;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LecturerRepository;

@Service
@Transactional
public class LecturerService {

    @Autowired
    private LecturerRepository lecturerRepository;

    /**
     * קבלת כל המרצים
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> getAllLecturers() {
        return lecturerRepository.findAll().stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * קבלת מרצה לפי ID
     */
    @Transactional(readOnly = true)
    public ResponseLecturerDTO getLecturerById(Long userId) {
        Lecturer lecturer = lecturerRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Lecturer not found with id: " + userId));
        return convertToResponseDTO(lecturer);
    }

    /**
     * חיפוש מרצים לפי שם
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> searchLecturersByName(String name) {
        return lecturerRepository.findByNameContaining(name).stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * קבלת רשימת כל הערים
     */
    @Transactional(readOnly = true)
    public List<String> getAllCities() {
        return lecturerRepository.findAllCities();
    }

    /**
     * קבלת מרצים לפי עיר
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> getLecturersByCity(String city) {
        return lecturerRepository.findByCityContainingIgnoreCase(city).stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * יצירת מרצה חדש
     */
    public ResponseLecturerDTO createLecturer(CreateLecturerDTO createDTO) {
        // בדיקה שאין כבר אימייל כזה
        if (lecturerRepository.findByEmail(createDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + createDTO.getEmail());
        }

        // בדיקה שאין כבר טלפון כזה
        if (lecturerRepository.findByPhone(createDTO.getPhone()).isPresent()) {
            throw new RuntimeException("Phone number already exists: " + createDTO.getPhone());
        }

        // יצירת מרצה חדש
        Lecturer lecturer = Lecturer.builder()
            .firstName(createDTO.getFirstName())
            .lastName(createDTO.getLastName())
            .bio(createDTO.getBio())
            .city(createDTO.getCity())
            .email(createDTO.getEmail())
            .phone(createDTO.getPhone())
            .imageUrl(createDTO.getImageUrl())
            .createdAt(LocalDateTime.now())
            .build();

        // שמירת המרצה
        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        
        return convertToResponseDTO(savedLecturer);
    }

    /**
     * מחיקת מרצה
     */
    public void deleteLecturer(Long userId) {
        Lecturer lecturer = lecturerRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Lecturer not found with id: " + userId));
        
        // הסרת הקשרים עם הרצאות לפני המחיקה
        lecturer.removeAllLectures();
        lecturerRepository.save(lecturer);
        
        // מחיקת המרצה
        lecturerRepository.deleteById(userId);
    }

    /**
     * ספירת מרצים
     */
    @Transactional(readOnly = true)
    public Long getLecturersCount() {
        return lecturerRepository.count();
    }

    /**
     * קבלת מרצים עם הרצאות
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> getLecturersWithLectures() {
        return lecturerRepository.findLecturersWithLectures().stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * בדיקה אם אימייל כבר קיים
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return lecturerRepository.findByEmail(email).isPresent();
    }

    /**
     * בדיקה אם טלפון כבר קיים
     */
    @Transactional(readOnly = true)
    public boolean phoneExists(String phone) {
        return lecturerRepository.findByPhone(phone).isPresent();
    }

    /**
     * קבלת מרצה לפי אימייל
     */
    @Transactional(readOnly = true)
    public ResponseLecturerDTO getLecturerByEmail(String email) {
        Lecturer lecturer = lecturerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Lecturer not found with email: " + email));
        return convertToResponseDTO(lecturer);
    }

    /**
     * המרת Lecturer entity ל-ResponseLecturerDTO
     */
    private ResponseLecturerDTO convertToResponseDTO(Lecturer lecturer) {
        Set<SimpleLectureDTO> lectureDTOs = lecturer.getLectures().stream()
            .map(lecture -> SimpleLectureDTO.builder()
                .lectureId(lecture.getLectureId())
                .title(lecture.getTitle())
                .startTime(lecture.getStartTime())
                .endTime(lecture.getEndTime())
                .location(lecture.getLocation())
                .isOnline(lecture.getIsOnline())
                .price(lecture.getPrice())
                .isAvailable(lecture.getIsAvailable())
                .build())
            .collect(Collectors.toSet());

        return ResponseLecturerDTO.builder()
            .userId(lecturer.getUserId())
            .firstName(lecturer.getFirstName())
            .lastName(lecturer.getLastName())
            .bio(lecturer.getBio())
            .city(lecturer.getCity())
            .email(lecturer.getEmail())
            .phone(lecturer.getPhone())
            .imageUrl(lecturer.getImageUrl())
            .createdAt(lecturer.getCreatedAt())
            .lectures(lectureDTOs)
            .build();
    }
}