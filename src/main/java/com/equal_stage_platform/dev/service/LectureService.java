package com.equal_stage_platform.dev.service;

// ---- necessary packages ----
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// ---- class imports ----
import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.LectureStatus;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.service.LecturerService;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }
    // ---------------------- create / update / retrieve methods ----------------------
    /**
     * Creates a new lecture in the system.
     *
     * @param lectureData The data for the new lecture.
     * @return A LectureDTO containing the created lecture's details.
     */
    public ResponseLectureDTO createLecture(CreateLectureDTO lectureData) {
        Lecturer lecturer = getLecturerEntityById(lectureData.getUserId());
        Lecture lecture = lectureRepository.save(new Lecture(lectureData, lecturer));
        // link the lecture to the lecturer
        return new ResponseLectureDTO(lectureData.getUserId(), lecture);
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
                .orElseThrow(() -> new RuntimeException("Lecture not found with ID: " + lectureId));
        return new ResponseLectureDTO(lecture.getLecturers().stream()
                .findFirst()
                .map(lecturer -> lecturer.getUserId())
                .orElse(null), lecture);
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
    public boolean updateLectureStatus(Long lectureId, Long lecturerId, LectureStatus status) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found with ID: " + lectureId));
        if (lecture.getLecturers().stream().noneMatch(lecturer -> lecturer.getUserId().equals(lecturerId))) {
            throw new RuntimeException("Lecturer with ID " + lecturerId + " is not associated with this lecture.");
        }
        lecture.setStatus(status);
        lectureRepository.save(lecture);
        return true;
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
                .orElseThrow(() -> new RuntimeException("Lecture not found with ID: " + lectureId));
        return lecture.getLecturers().stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer))
                .toList();
    }


}
