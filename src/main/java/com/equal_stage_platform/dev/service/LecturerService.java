package com.equal_stage_platform.dev.service;

// ---- necessary packages ----
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// ---- class imports ----
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.dto.PaginatedResponseDTO;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.exception.LecturerException;
import com.equal_stage_platform.dev.exception.TopicException;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.dto.UpdateLecturerDTO;
import com.equal_stage_platform.dev.model.LecturerTopic;

@Service
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    private final LectureRepository lectureRepository;
    private final AuthService authService;
    private final LecturerTopicService topicService;
    private final MailService mailService;
    public LecturerService(LecturerRepository lecturerRepository, LectureRepository lectureRepository, AuthService authService, LecturerTopicService topicService, MailService mailService) {
        this.lecturerRepository = lecturerRepository;
        this.lectureRepository = lectureRepository;
        this.authService = authService;
        this.topicService = topicService;
        this.mailService = mailService;
    }

    // ---------------------- create / update / retrieve methods ----------------------
    /**
     * Creates a new lecturer in the system.
     *
     * @param lecturerData The data for the new lecturer.
     * @return A ResponseLecturerDTO containing the created lecturer's details.
     */
    @Transactional
    public ResponseLecturerDTO createLecturer(UUID userId, CreateLecturerDTO lecturerData){
        Set<LecturerTopic> topics = getTopicsFromIds(lecturerData.getLecturerTopicsIds());
        // save the lecturer to the database
        Lecturer lecturer = lecturerRepository.save(new Lecturer(userId,lecturerData, topics));
        // return the saved lecturer as a ResponseLecturerDTO
        return new ResponseLecturerDTO(lecturer, lecturer.getLectures());
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
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLectures()))
                .toList();
    }

    /**
     * Retrieves a lecturer by their user ID.
     *
     * @param userId The ID of the lecturer to retrieve.
     * @return A ResponseLecturerDTO containing the lecturer's details.
     */
    @Transactional(readOnly = true)
    public ResponseLecturerDTO getLecturerById(UUID userId, boolean isAdmin) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new LecturerException("Lecturer not found with userId: " + userId));
        if(!isAdmin && lecturer.getStatus() != LecturerStatus.APPROVED) {
            throw new LecturerException("Lecturer not found with userId: " + userId);
        }
        return new ResponseLecturerDTO(lecturer, isAdmin ? lecturer.getLectures() : lecturer.getLecturesByStatus(LectureStatus.ON_AIR));
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
                .orElseThrow(() -> new LecturerException("Lecturer not found with userId: " + userId));
        
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
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
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
                .orElseThrow(() -> new LecturerException("Lecturer not found with userId: " + userId));
        if (lecturer.getStatus() != LecturerStatus.APPROVED) {
            throw new LecturerException("Lecturer with userId: " + userId + " is not approved.");
        }
        return lecturer.getLectures()
                .stream()
                .map(lecture -> new ResponseLectureDTO(lecture))
                .toList();
    }

    /**
     * Retrieves a specific lecture by its ID and the lecturer's user ID.
     * checks if the lecturer is approved and lecture is On_AIR.
     * 
     * @param lecturerId The ID of the lecturer.
     * @param lectureId The ID of the lecture to retrieve.
     * @return A ResponseLectureDTO containing the details of the specified lecture.
     */
    @Transactional(readOnly = true)
    public ResponseLectureDTO getLectureById(UUID lecturerId, Long lectureId) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new LecturerException("Lecturer not found with userId: " + lecturerId));
        if (lecturer.getStatus() != LecturerStatus.APPROVED) {
            throw new LecturerException("Lecturer with userId: " + lecturerId + " is not approved.");
        }
        return lecturer.getLectures()
                .stream()
                .filter(lecture -> lecture.getLectureId().equals(lectureId))
                .filter(lecture -> lecture.getStatus() == LectureStatus.ON_AIR)
                .findFirst()
                .map(lecture -> new ResponseLectureDTO(lecture))
                .orElseThrow(() -> new LecturerException("Lecture not found with ID: " + lectureId));
    }
    /**
     * Updates the status of a lecturer.
     *
     * @param userId The ID of the lecturer whose status is to be updated.
     * @param status The new status for the lecturer.
     * @return A boolean indicating whether the update was successful.
     */
    @Transactional
    public ResponseLecturerDTO updateLecturerStatus(UUID userId, LecturerStatus status, boolean isAdmin, String note) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new LecturerException("Lecturer not found with userId: " + userId));
        if(!isAdmin && lecturer.getStatus().equals(LecturerStatus.PENDING)) {
            // user is no admin and lecturer is pending, so no update allowed
            throw new LecturerException("You are not authorized to update this lecturer's status");
        }
        lecturer.setStatus(status);
        lecturerRepository.save(lecturer);
        if(isAdmin){
            if (status == LecturerStatus.REJECTED && (note == null || note.isEmpty())) {
                throw new LecturerException("Note is required");
            }
            String email = lecturer.getEmail();
            String subject = "עדכון סטאטוס מרצה";
            String text = (status == LecturerStatus.APPROVED ? "אנו שמחים לבשר לך שהפרופיל שלך אושר בהצלחה" : "סטאטוס מרצה: לא מאושר \n --------------------\nהתייחסות החלטה: \n" + note) + "\n\n" + lecturer.LecturerInfoHebrew();
            try{
                mailService.sendMail(email, subject, text);
            }catch(Exception e){
                // logger.error("Error sending email to lecturer", e);
            }
        }
        return new ResponseLecturerDTO(lecturer, isAdmin ? lecturer.getLectures() : lecturer.getLecturesByStatus(LectureStatus.ON_AIR));
    }

    /**
     * Updates an existing lecturer's details.
     *
     * @param userId The ID of the lecturer to update.
     * @param lecturerData The data to update the lecturer with.
     * @return A ResponseLecturerDTO containing the updated lecturer's details.
     */
    @Transactional
    public ResponseLecturerDTO updateLecturer(UUID userId, UpdateLecturerDTO lecturerData) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new LecturerException("Lecturer not found with userId: " + userId));
        updateLecturer(lecturer, lecturerData);
        lecturerRepository.save(lecturer);
        return new ResponseLecturerDTO(lecturer, lecturer.getLectures());
    }

    private void updateLecturer(Lecturer lecturer, UpdateLecturerDTO lecturerData){
        
        if (lecturerData == null) {
            throw new LecturerException("Update data cannot be null");
        }

        if (lecturerData.getStatus() != null){
            LecturerStatus status = LecturerStatus.valueOf(lecturerData.getStatus());
            if (status == LecturerStatus.PENDING) {
                throw new LecturerException("Cannot update status to PENDING");
            } else {
                lecturer.setStatus(status);
            }
        }

        if (lecturerData.getFirstName() != null) {
            lecturer.setFirstName(lecturerData.getFirstName());
            lecturer.setFullName(lecturerData.getFirstName() + " " + lecturer.getLastName());
        }
        if (lecturerData.getLastName() != null) {
            lecturer.setLastName(lecturerData.getLastName());
            lecturer.setFullName(lecturer.getFirstName() + " " + lecturerData.getLastName());
        }
        if (lecturerData.getBio() != null) {
            lecturer.setBio(lecturerData.getBio());
        }
        if (lecturerData.getCity() != null) {
            lecturer.setCity(lecturerData.getCity());
        }
        if (lecturerData.getEmail() != null) {
            lecturer.setEmail(lecturerData.getEmail());
        }
        if (lecturerData.getPhone() != null) {
            lecturer.setPhone(lecturerData.getPhone());
        }
        if (lecturerData.getImageUrl() != null) {
            lecturer.setImageUrl(lecturerData.getImageUrl());
        }
        if (lecturerData.getWorkingAreas() != null) {
            lecturer.setWorkingAreas(lecturerData.getWorkingAreas());
            for (Lecture lecture : lecturer.getLectures()) 
                lecture.initWorkingAreas();
        }
        if (lecturerData.getExternalLinks() != null) {
            lecturer.setExternalLinks(lecturerData.getExternalLinks().stream()
                    .map(link -> new com.equal_stage_platform.dev.model.ExternalLink(link.getUrl(), link.getDescription()))
                    .collect(java.util.stream.Collectors.toSet()));
        }
        if (lecturerData.getVideoLinks() != null) {
            lecturer.setVideoLinks(lecturerData.getVideoLinks().stream()
                    .map(link -> new com.equal_stage_platform.dev.model.ExternalLink(link.getUrl(), link.getDescription()))
                    .collect(java.util.stream.Collectors.toSet()));
        }
        if (lecturerData.getLecturerTopicsIds() != null) {
            Set<LecturerTopic> topics = getTopicsFromIds(lecturerData.getLecturerTopicsIds());
            lecturer.enrollLecturerTopics(topics);
        }
    }

    /**
     * Search lecturers by name
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> searchLecturersByName(String name, boolean isAdmin) {
        List<ResponseLecturerDTO> lecturers = lecturerRepository.findByNameContaining(name).stream()
            .map(lecturer -> new ResponseLecturerDTO(lecturer, isAdmin ? lecturer.getLectures() : lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
            .toList();

        if (!isAdmin) {
            // Filter out lecturers that are not approved if the user is not an admin
            lecturers = lecturers.stream()
                .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
                .toList();
        }
        
        if (lecturers.isEmpty()) {
            throw new LecturerException("There is no Lecturer with Name: " + name);
        }
        
        return lecturers;
    }


    /**
     * Retrieves all lecturers in a specific area.
     *
     * @param area The area to filter lecturers by.
     * @return A list of ResponseLecturerDTO containing details of lecturers in the specified area.
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> getLecturersByAreas(Set<Area> areas) {
        List<Lecturer> lecturers = lecturerRepository.findByWorkingAreasContainingAndStatus(areas, LecturerStatus.APPROVED);
        if (lecturers.isEmpty()) {
            throw new LecturerException("There are no lecturers in the areas: " + areas);
        }
        return lecturers.stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
                .toList();
    }

    /**
     * Returns paginated lecturers
     * 
     * @param pageNum The page number to retrieve.
     * @param pageSize The number of lecturers per page.
     */
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ResponseLecturerDTO> getPaginatedLecturers(int pageNum, int pageSize, boolean isAdmin) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        Page<Lecturer> page = getPagedLecturers(pageRequest, isAdmin);
        List<ResponseLecturerDTO> content = mapLecturersToResponse(page.getContent(), isAdmin);

        if (content.isEmpty()) {
            throw new LecturerException("No lecturers found on page " + pageNum);
        }

        return PaginatedResponseDTO.<ResponseLecturerDTO>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    private Page<Lecturer> getPagedLecturers(PageRequest pageRequest, boolean isAdmin) {
        if (isAdmin) {
            return lecturerRepository.findAll(pageRequest);
        } else {
            return lecturerRepository.findByStatus(LecturerStatus.APPROVED, pageRequest);
        }
    }
    private List<ResponseLecturerDTO> mapLecturersToResponse(List<Lecturer> lecturers, boolean isAdmin) {
        if (isAdmin) {
            return lecturers.stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLectures()))
                .toList();
        } else {
            return lecturers.stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
                .toList();
        }
    }

    /**
     * Deletes a lecturer from the system.
     *
     * @param userId The ID of the lecturer to delete.
     * @return A message indicating the result of the deletion.
     */
    @Transactional
    public String deleteLecturer(UUID userId) {
        Lecturer lecturer = lecturerRepository.findById(userId)
                .orElseThrow(() -> new LecturerException("Lecturer not found with userId: " + userId));
        Set<Lecture> lectures = new HashSet<>(lecturer.getLectures());
        for(Lecture lecture : lectures){
            lecture.removeLecturer(lecturer);
            if(lecture.getLecturerCount() == 0) {
                lectureRepository.delete(lecture);
            } else{
                lecture.initWorkingAreas();
                lectureRepository.save(lecture);
            }
        }
        authService.changeRole(userId, Role.USER);
        lecturerRepository.delete(lecturer);
        return "Lecturer with userId: " + userId + " has been deleted successfully.";       
    }

    /**
     * Retrieves a lecturer by their email address.
     *
     * @param email The email address of the lecturer to retrieve.
     * @return A ResponseLecturerDTO containing the lecturer's details.
     */
    @Transactional(readOnly = true)
    public ResponseLecturerDTO getLecturerByEmail(String email) {
        Lecturer lecturer = lecturerRepository.findByEmail(email)
            .orElseThrow(() -> new LecturerException("Lecturer not found with email: " + email));
        return new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR));
    }

    /**
     * Retrieves a lecturer that name starts with the given prefix.
     * 
     * @param name The prefix of the lecturer's name to search for.
     * @return A list of ResponseLecturerDTO containing details of lecturers whose names start with the given prefix.
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> searchLecturersByNamePrefix(String name) {
        if (name == null) {
            throw new LecturerException("Name prefix cannot be null");
        }
        String prefix = name.toLowerCase();
        return lecturerRepository.findByNameStartingWith(prefix)
            .stream()
            .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
            .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
            .toList();
    }

    /**
     * Filters lecturers based on target audiences, topics, and working areas.
     * Only returns approved lecturers.
     *
     * @param targetAudiences List of target audience IDs to filter by (optional)
     * @param topics List of topic IDs to filter by (optional)
     * @param workingAreas List of working areas to filter by (optional)
     * @return A list of ResponseLecturerDTO containing filtered lecturers
     */
    @Transactional(readOnly = true)
    public List<ResponseLecturerDTO> filterLecturers(List<Long> targetAudiences, 
                                                    List<Long> topics, 
                                                    List<Area> workingAreas,
                                                    Double minRank,
                                                    Double maxRank) {
        // Convert empty lists to null for proper query handling
        List<Long> targetAudienceIds = (targetAudiences != null && targetAudiences.isEmpty()) ? null : targetAudiences;
        List<Long> topicIds = (topics != null && topics.isEmpty()) ? null : topics;
        List<Area> areas = (workingAreas != null && workingAreas.isEmpty()) ? null : workingAreas;

        List<Lecturer> lecturers = lecturerRepository.filterLecturers(
            LecturerStatus.APPROVED, // Only approved lecturers
            targetAudienceIds, 
            topicIds, 
            areas,
            minRank,
            maxRank
        );

        if (lecturers.isEmpty()) {
            throw new LecturerException("No lecturers found matching the specified criteria");
        }

        return lecturers.stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
                .toList();
    }

    /**
     * Filters lecturers based on target audiences, topics, and working areas with pagination.
     * Only returns approved lecturers.
     * @param pageNum The page number to retrieve.
     * @param pageSize The number of lecturers per page.
     * @param targetAudiences List of target audience IDs to filter by (optional)
     * @param topics List of topic IDs to filter by (optional)
     * @param workingAreas List of working areas to filter by (optional)
     * @return A paginated response containing filtered lecturers
     */
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ResponseLecturerDTO> filterLecturersPaginated(int pageNum, 
                                                                              int pageSize,
                                                                              List<Long> targetAudiences, 
                                                                              List<Long> topics, 
                                                                              List<Area> workingAreas,
                                                                              Double minRank,
                                                                              Double maxRank){
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Page<Lecturer> page = lecturerRepository.filterLecturersPageable(
            LecturerStatus.APPROVED, // Only approved lecturers
            targetAudiences, 
            topics, 
            workingAreas,
            minRank,
            maxRank,
            pageRequest
        );

        if (page.isEmpty()) {
            throw new LecturerException("No lecturers found matching the specified criteria on page " + pageNum);
        }

        List<ResponseLecturerDTO> content = page.getContent()
                .stream()
                .map(lecturer -> new ResponseLecturerDTO(lecturer, lecturer.getLecturesByStatus(LectureStatus.ON_AIR)))
                .toList();

        return PaginatedResponseDTO.<ResponseLecturerDTO>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    private Set<LecturerTopic> getTopicsFromIds(Set<Long> topicsIds) {
        try{
            return topicsIds.stream()
                .map(topicService::getTopicById)
                .collect(Collectors.toSet());
        }catch(TopicException e){
            throw new LecturerException(e.getMessage());
        } catch (RuntimeException e) {
            throw new LecturerException("Invalid topics");
        }
    }
}