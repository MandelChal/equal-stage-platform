// LectureService.java
package com.equal_stage_platform.dev.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.SimpleLecturerDTO;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;

import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;

@Service
@Transactional
public class LectureService {

    @Autowired
    private LectureRepository lectureRepository;
    
    @Autowired
    private LecturerRepository lecturerRepository;

    public List<ResponseLectureDTO> getAllLectures() {
        return lectureRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ResponseLectureDTO getLectureById(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId.intValue())
                .orElseThrow(() -> new RuntimeException("Lecture not found with id: " + lectureId));
        return mapToResponseDTO(lecture);
    }

    public List<ResponseLectureDTO> getAvailableLectures() {
        return lectureRepository.findByIsAvailableTrue().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseLectureDTO> getOnlineLectures() {
        return lectureRepository.findByIsOnlineTrue().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseLectureDTO> getPhysicalLectures() {
        return lectureRepository.findByIsOnlineFalse().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseLectureDTO> getFutureLectures() {
        return lectureRepository.findByStartTimeAfter(LocalDateTime.now()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseLectureDTO> searchLecturesByTitle(String title) {
        return lectureRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllLocations() {
        return lectureRepository.findAllLocations();
    }

    public List<ResponseLectureDTO> getLecturesByLocation(String location) {
        return lectureRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseLectureDTO> getLecturesByLecturer(Long lecturerId) {
        return lectureRepository.findByLecturerId(lecturerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ResponseLectureDTO createLecture(CreateLectureDTO createDTO) {
        Lecture lecture = new Lecture();
        lecture.setTitle(createDTO.getTitle());
        lecture.setDescription(createDTO.getDescription());
        lecture.setLocation(createDTO.getLocation());
        lecture.setStartTime(createDTO.getStartTime());
        lecture.setEndTime(createDTO.getEndTime());
        lecture.setImageUrl(createDTO.getImageUrl());
        lecture.setIsOnline(createDTO.getIsOnline() != null ? createDTO.getIsOnline() : false);
        lecture.setIsAvailable(createDTO.getIsAvailable() != null ? createDTO.getIsAvailable() : true);
        lecture.setPrice(createDTO.getPrice());

        // הוספת מרצים אם קיימים
        if (createDTO.getLecturerIds() != null && !createDTO.getLecturerIds().isEmpty()) {
            Set<Lecturer> lecturers = new HashSet<>();
            for (Long lecturerId : createDTO.getLecturerIds()) {
                Lecturer lecturer = lecturerRepository.findById(lecturerId)
                        .orElseThrow(() -> new RuntimeException("Lecturer not found with id: " + lecturerId));
                lecturers.add(lecturer);
            }
            lecture.setLecturers(lecturers);
        }

        Lecture savedLecture = lectureRepository.save(lecture);
        return mapToResponseDTO(savedLecture);
    }


    public void deleteLecture(Integer lectureId) {
        if (!lectureRepository.existsById(lectureId)) {
            throw new RuntimeException("Lecture not found with id: " + lectureId);
        }
        lectureRepository.deleteById(lectureId);
    }

    public Long getLecturesCount() {
        return lectureRepository.count();
    }

    // public List<ResponseLectureDTO> getAvailableAndFutureLectures() {
    //     return lectureRepository.findByIsAvailableTrueAndStartTimeAfter(LocalDateTime.now()).stream()
    //             .map(this::mapToResponseDTO)
    //             .collect(Collectors.toList());
    // }

    // Mapper method
    private ResponseLectureDTO mapToResponseDTO(Lecture lecture) {
        Set<SimpleLecturerDTO> lecturersDTOs = null;
        
        try {
            if (lecture.getLecturers() != null) {
                lecturersDTOs = lecture.getLecturers().stream()
                        .map(lecturer -> SimpleLecturerDTO.builder()
                                .userId(lecturer.getUserId())
                                .firstName(lecturer.getFirstName())
                                .lastName(lecturer.getLastName())
                                .city(lecturer.getCity())
                                .imageUrl(lecturer.getImageUrl())
                                .build())
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            // במקרה של בעיה עם lazy loading, תחזיר set ריק
            lecturersDTOs = Set.of();
        }

        return ResponseLectureDTO.builder()
                .lectureId(lecture.getLectureId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .location(lecture.getLocation())
                .isAvailable(lecture.getIsAvailable())
                .startTime(lecture.getStartTime())
                .endTime(lecture.getEndTime())
                .imageUrl(lecture.getImageUrl())
                .isOnline(lecture.getIsOnline())
                .createdAt(lecture.getCreatedAt())
                .price(lecture.getPrice())
                .lecturers(lecturersDTOs != null ? lecturersDTOs : Set.of())
                .build();
    }
}