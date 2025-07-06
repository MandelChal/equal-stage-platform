package com.equal_stage_platform.dev.service;

// ---- necessary packages ----
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// ---- class imports ----
import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.equal_stage_platform.dev.repository.UserRepository;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.exception.LectureException;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LecturerRepository lecturerRepository;
    private final UserRepository userRepository;
    public LectureService(LectureRepository lectureRepository, LecturerRepository lecturerRepository, UserRepository userRepository) {
        this.lecturerRepository = lecturerRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }
    // ---------------------- create / update / retrieve methods ----------------------
    /**
     * Creates a new lecture in the system.
     *
     * @param lectureData The data for the new lecture.
     * @return A LectureDTO containing the created lecture's details.
     */
    @Transactional
    public ResponseLectureDTO createLecture(CreateLectureDTO lectureData) {
        // search for the lecturer by userId
        UUID userID = lectureData.getUserId();
        Lecturer lecturer = lecturerRepository.findById(userID)
                .orElseThrow(() -> new LectureException("Lecturer not found with ID: " + userID));
        if (lecturer.getStatus() != LecturerStatus.APPROVED) {
            throw new LectureException("Lecturer is not approved");
        }
        Lecture lecture = lectureRepository.save(new Lecture(lectureData));
        lecturer.enrollLecture(lecture);
        return new ResponseLectureDTO(lecture);
    }

    /**
     * Retrieves a lecture by its ID.
     *
     * @param lectureId The ID of the lecture to retrieve.
     * @return A ResponseLectureDTO containing the lecture's details.
     */
    @Transactional(readOnly = true)
    public ResponseLectureDTO getLectureById(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found with ID: " + lectureId));
        return new ResponseLectureDTO(lecture);
    }

    /**
     * Updates an existing lecture status.
     *
     * @param lectureId The ID of the lecture to update.
     * @param lecturerId The ID of the lecturer performing the update.
     * @param status The new status for the lecture.
     * @return A boolean indicating whether the update was successful.
     */
    @Transactional
    public ResponseLectureDTO updateLectureStatus(UUID userId, Long lectureId, LectureStatus status) {
        // check if lectureId is owned by userId
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found with ID: " + lectureId));
        if (!lecture.getLecturers().stream().anyMatch(lecturer -> lecturer.getUserId().equals(userId))) {
            throw new LectureException("You are not authorized to update this lecture");
        }
        lecture.setStatus(status);
        lectureRepository.save(lecture);
        return new ResponseLectureDTO(lecture);
    }

    /**
     * Retrieves all lecturers by its lectureID.
     * 
     * @param lectureId The ID of the lecture to retrieve lecturers for.
     * @return A list of ResponseLecturerDTO containing details of all lecturers associated with the lecture.
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> getLecturersByLectureId(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found with ID: " + lectureId));
        return lecture.getLecturers().stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer))
                .toList();
    }

    /**
     * Retrieves all lectures in the system that are OnAir and their Lecturers ia Approved.
     *
     * @return A list of ResponseLectureDTO containing details of all lectures.
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getAllLectures() {
        List<Lecture> lectures =  lectureRepository.findByStatus(LectureStatus.ON_AIR);
        if (lectures.isEmpty()) {
            throw new LectureException("No lectures found with status ON_AIR");
        }
        return lectures.stream()
                .filter(this::hasApprovedLecturers)
                .map(lecture -> new ResponseLectureDTO(lecture))
                .toList();
    }

    /**
     * Retrieves all lectures in the system.
     *
     * @return A list of ResponseLectureDTO containing details of all lectures.
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getAllLecturesAdmin() {
        return lectureRepository.findAll()
                .stream()
                .map(lecture -> new ResponseLectureDTO(lecture))
                .toList();
    }

    /**
     * Retrieves all lectures in the system that are OnAir and their Lecturers ia Approved **and they are Online**
     *
     * @return A list of ResponseLectureDTO containing details of all lectures.
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getAllOnlineLectures() {
        List<Lecture> lectures = lectureRepository.findByStatusAndOnline(LectureStatus.ON_AIR, true);
        if (lectures.isEmpty()) {
            throw new LectureException("No online lectures found with status ON_AIR");
        }
        return lectures.stream()
                .filter(this::hasApprovedLecturers)
                .map(lecture -> new ResponseLectureDTO(lecture))
                .toList();
    }

    /**
     * Searches for a lecture by Title.
     * Retrieves a lecture iff their Lecturers ia Approved and lecture status is OnAir
     *
     * @return A list of ResponseLectureDTO containing details of all lectures.
     */
    @Transactional(readOnly = true)
    public ResponseLectureDTO getLectureByTitle(String title) {
        Lecture lecture = lectureRepository.findByTitle(title)
            .orElseThrow(() -> new LectureException("Lecture not found with title: " + title));
        if(hasOnAirStatus(lecture) && hasApprovedLecturers(lecture))
            return new ResponseLectureDTO(lecture);
        throw new LectureException("Lecture not found with title: " + title);
    }

    private boolean hasOnAirStatus(Lecture lecture){
        return lecture.getStatus() == LectureStatus.ON_AIR;
    }
    
    private boolean hasApprovedLecturers(Lecture lecture) {
        return !lecture.getLecturers().isEmpty() &&
            lecture.getLecturers().stream()
                .allMatch(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED);
    }

    /**
     * Retrieves all physical lectures in the system that are OnAir and their Lecturers ia Approved.
     *
     * @return A list of ResponseLectureDTO containing details of all physical lectures.
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getPhysicalLectures() {
        List<Lecture> lectures = lectureRepository.findByStatusAndOnline(LectureStatus.ON_AIR, false);
        if (lectures.isEmpty()) {
            throw new LectureException("No Physical lectures found with status ON_AIR");
        }
        return lectures.stream()
                .filter(this::hasApprovedLecturers)
                .map(lecture -> new ResponseLectureDTO(lecture))
                .toList();
    }

    /**
     * Deletes a lecture by its ID.
     *
     * @param userId The ID of the user requesting the deletion.
     * @param lectureId The ID of the lecture to delete.
     * @return A message indicating the result of the deletion.
     */
    @Transactional
    public String deleteLecture(UUID userId, Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found with ID: " + lectureId));
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new LectureException("User not found with ID: " + userId));
        if (!user.isAdmin()) {
            Lecturer lecturer = lecturerRepository.findById(userId)
                    .orElseThrow(() -> new LectureException("Lecturer not found with ID: " + userId));
    
            if (!lecture.searchLecturer(lecturer)) {
                throw new LectureException("You are not authorized to delete this lecture");
            }
        }
        lecture.removeAllLecturers();
        lectureRepository.delete(lecture);
        return "Lecture deleted successfully";
    }
}