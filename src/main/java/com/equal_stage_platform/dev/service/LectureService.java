package com.equal_stage_platform.dev.service;

// ---- necessary packages ----
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// ---- class imports ----
import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.dto.PaginatedResponseDTO;
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
    public ResponseLectureDTO getLectureById(Long lectureId, boolean isAdmin) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found with ID: " + lectureId));
        if (!isAdmin){
            if (!hasOnAirStatus(lecture) || !hasApprovedLecturers(lecture)) {
                throw new LectureException("Lecture not found with ID: " + lectureId);
            }
        }
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
        lecture.setUpdatedAt(LocalDateTime.now());
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
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
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
        boolean approved = true;
        boolean online = true;
        List<Lecture> lectures = lectureRepository.findByStatusAndOnlineAndApproved(LectureStatus.ON_AIR, online, approved);
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
    public ResponseLectureDTO getLectureByTitle(String title, boolean isAdmin) {
        Lecture lecture = null;
        if(isAdmin){
            lecture = lectureRepository.findByTitle(title)
                .orElseThrow(() -> new LectureException("Lecture not found with title: " + title));
        }
        else{
            boolean approved = true;
            lecture = lectureRepository.findByTitleAndApproved(title, approved)
                .orElseThrow(() -> new LectureException("Lecture not found with title: " + title));
            if(!hasOnAirStatus(lecture) || !hasApprovedLecturers(lecture))
                throw new LectureException("Lecture not found with title: " + title);
        }
        return new ResponseLectureDTO(lecture);

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
        boolean approved = true;
        boolean online = false;
        List<Lecture> lectures = lectureRepository.findByStatusAndOnlineAndApproved(LectureStatus.ON_AIR, online, approved);
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

    /**
     * Retrieves all lectures in the system that are approved and their Lecturers are approved under the given status.
     * 
     * @param status The status to filter lectures by.
     * @return A list of ResponseLectureDTO containing details of all lectures with the specified status.
     * 
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getLecturesByStatus(LectureStatus status) {
        boolean approved = true;
        List<Lecture> lectures = lectureRepository.findByStatusAndApproved(status, approved);
        return lectures.stream()
                .filter(this::hasApprovedLecturers)
                .map(lecture -> new ResponseLectureDTO(lecture))
                .toList();
    }

    /**
     * Retrieves shuffle and limited version of lectures in the system that are approved and their Lecturers are approved under the given status.
     * 
     * @param status The status to filter lectures by.
     * @param limit The maximum number of lectures to return.
     * @return A list of ResponseLectureDTO containing details of all lectures with the specified status.
     * 
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getRandomLecturesByStatus(LectureStatus status, int limit) {
        boolean approved = true;
        List<Lecture> lectures = lectureRepository.findByStatusAndApproved(status, approved);
        Collections.shuffle(lectures);
        lectures = lectures.stream()
                .filter(this::hasApprovedLecturers)
                .toList();
        if (lectures.size() > limit) {
            lectures = lectures.subList(0, limit);
        }
        return lectures.stream()
                .map(lecture -> new ResponseLectureDTO(lecture))
                .toList();
    }        

    /**
     * Returns paginated lectures (ON_AIR and approved lecturers only)
     */
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ResponseLectureDTO> getPaginatedLectures(int pageNum, int pageSize, boolean isAdmin) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Page<Lecture> page = null;
        if(isAdmin){
            page = lectureRepository.findAll(pageRequest);
        }else{
            boolean approved = true;
            page = lectureRepository.findByStatusAndApproved(LectureStatus.ON_AIR, approved, pageRequest);
        }
        List<ResponseLectureDTO> content = null;
        if(isAdmin){
            content = page.getContent().stream()
                .map(ResponseLectureDTO::new)
                .toList();
        }else{
            content = page.getContent().stream()
                .filter(this::hasApprovedLecturers)
                .map(ResponseLectureDTO::new)
                .toList();
        }
        return PaginatedResponseDTO.<ResponseLectureDTO>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    /*
     * Searches for lectures by title prefix.
     * This method filters lectures that are ON_AIR and have approved lecturers.
     * 
     * @param name The prefix of the lecture title to search for.
     * @return A list of ResponseLectureDTO containing details of lectures that match the search criteria.
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> searchLecturesByNamePrefix(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Search prefix cannot be null");
        }
        String prefix = name.toLowerCase();
        return lectureRepository.findByTitleStartingWith(prefix)
            .stream()
            .filter(lecture -> lecture.isApproved() && lecture.getStatus() == LectureStatus.ON_AIR && hasApprovedLecturers(lecture))
            .map(ResponseLectureDTO::new)
            .toList();
    }

    /*
     * Retrieves all pending lectures that are not yet approved.
     * 
     * @return A list of ResponseLectureDTO containing details of all pending lectures.
     * @throws LectureException if no pending lectures are found.
     */
    @Transactional(readOnly = true)
    public List<ResponseLectureDTO> getPendingLectures(){
        List<Lecture> lectures = lectureRepository.findByApproved(false);
        if (lectures.isEmpty()) {
            throw new LectureException("No pending lectures found");
        }
        return lectures.stream()
                .map(ResponseLectureDTO::new)
                .toList();
    }

    /*
     * Sets the approval status of a lecture.
     * 
     * @param lectureId The ID of the lecture to approve or disapprove.
     * @param approve The approval status to set (true for approved, false for disapproved).
     * 
     * @return A ResponseLectureDTO containing the updated lecture details.
     * @throws LectureException if the lecture is not found.
     */
    @Transactional
    public ResponseLectureDTO setApproveLecture(Long lectureId, boolean approve) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found with ID: " + lectureId));
        lecture.setApproved(approve);
        lecture.setUpdatedAt(LocalDateTime.now());
        lectureRepository.save(lecture);
        return new ResponseLectureDTO(lecture);
    }
}