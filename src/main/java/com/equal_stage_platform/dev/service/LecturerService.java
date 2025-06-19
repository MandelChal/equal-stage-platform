package com.equal_stage_platform.dev.service;

// ---- necessary packages ----
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// ---- class imports ----
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;

@Service
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    // ---------------------- create / update / retrieve methods ----------------------
    /**
     * Creates a new lecturer in the system.
     *
     * @param lecturerData The data for the new lecturer.
     * @return A ResponseLecturerDTO containing the created lecturer's details.
     */
    public ResponseLecturerDTO createLecturer(CreateLecturerDTO lecturerData){   
        // save the lecturer to the database
        Lecturer lecturer = lecturerRepository.save(new Lecturer(lecturerData));
        // return the saved lecturer as a ResponseLecturerDTO
        return new ResponseLecturerDTO(lecturer);
    }

    /**
     * Retrieves all lecturers from the system.
     *
     * @return A list of ResponseLecturerDTO containing details of all lecturers.
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> getAllLecturers() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        return lecturers.stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer))
                .toList();
    }

    /**
     * Retrieves a lecturer by their user ID.
     *
     * @param userId The ID of the lecturer to retrieve.
     * @return A ResponseLecturerDTO containing the lecturer's details.
     */
    @Transactional(readOnly = true)
    public ResponseLecturerDTO getLecturerById(UUID userId) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found with userId: " + userId));
        
        return new ResponseLecturerDTO(lecturer);
    }

    /**
     * Retrieves a lecturer by their user ID.
     *
     * @param userId The ID of the lecturer to retrieve.
     * @return A ResponseLecturerDTO containing the lecturer's details.
     */
    @Transactional(readOnly = true)
    public Lecturer getLecturerEntityById(UUID userId) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found with userId: " + userId));
        
        return lecturer;
    }
    
    /**
     * Retrieves all lecturers with a specific status.
     *
     * @param status The status of the lecturers to retrieve.
     * @return A list of ResponseLecturerDTO containing details of lecturers with the specified status.
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> getLecturersByStatus(LecturerStatus status) {
        List<Lecturer> lecturers = lecturerRepository.findByStatus(status);
        return lecturers.stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer))
                .toList();
    }
    
    /**
     * Retrieves all lectures associated with a specific lecturer by their user ID.
     *
     * @param userId The ID of the lecturer whose lectures are to be retrieved.
     * @return A list of Lecture objects associated with the specified lecturer.
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getLecturesByLecturerId(UUID userId) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found with userId: " + userId));
        return lecturer.getLectures()
                .stream()
                .map(lecture -> new ResponseLectureDTO(userId, lecture))
                .toList();
    }

    /**
     * Retrieves a specific lecture by its ID and the lecturer's user ID.
     * 
     * @param lecturerId The ID of the lecturer.
     * @param lectureId The ID of the lecture to retrieve.
     * @return A ResponseLectureDTO containing the details of the specified lecture.
     */
    @Transactional(readOnly = true)
    public ResponseLectureDTO getLectureById(UUID lecturerId, Long lectureId) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found with userId: " + lecturerId));
        
        return lecturer.getLectures()
                .stream()
                .filter(lecture -> lecture.getLectureId().equals(lectureId))
                .findFirst()
                .map(lecture -> new ResponseLectureDTO(lecturerId, lecture))
                .orElseThrow(() -> new RuntimeException("Lecture not found with ID: " + lectureId));
    }
    
    // ---------------------- lecturer status update methods ----------------------
    /**
     * Updates the status of a lecturer by an admin.
     *
     * @param approvingUserId The ID of the admin approving the status change.
     * @param userId The ID of the lecturer whose status is to be updated.
     * @param status The new status for the lecturer.
     * @return A boolean indicating whether the update was successful.
     */
    @Transactional
    public ResponseLecturerDTO updateLecturerStatusByAdmin(UUID approvingUserId, UUID userId, LecturerStatus status) {
        //TODO - check if the approving user is an admin
        //TODO - check if the userId exists in the database
        //TODO - update user status
        return updateLecturerStatus(userId, status);
    }

    /**
     * Updates the status of a lecturer.
     *
     * @param userId The ID of the lecturer whose status is to be updated.
     * @param status The new status for the lecturer.
     * @return A boolean indicating whether the update was successful.
     */
    @Transactional
    public ResponseLecturerDTO updateLecturerStatus(UUID userId, LecturerStatus status) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found with userId: " + userId));
        lecturer.setStatus(status);
        lecturerRepository.save(lecturer);
        return new ResponseLecturerDTO(lecturer);
    }


}
