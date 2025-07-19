package com.equal_stage_platform.dev.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.fake.LectureFakerService;
import com.equal_stage_platform.dev.fake.LecturerFakerService;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import net.datafaker.Faker;

@Service
public class AdvancedLectureFakerService {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LecturerFakerService lecturerFakerService;

    @Autowired
    private LectureFakerService lectureFakerService;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // ========== Flow-Aware Data Creation ==========

    /**
     * Creates fake data following the proper flow:
     * 1. Creates lecturers in PENDING status
     * 2. Approves them (simulating admin approval)
     * 3. Creates lectures with approved lecturers only
     */
    public void createFakeData() {
        createFakeData(25, 60);
    }

    public void createFakeData(int lecturerCount, int lectureCount) {
        System.out.println("🚀 Creating fake data following proper flow...");
        
        // Step 1: Create lecturers in PENDING status
        List<Lecturer> lecturers = createFakeLecturers(lecturerCount);
        System.out.println("✅ Created " + lecturers.size() + " lecturers in PENDING status");
        
        // Step 2: Approve all lecturers (simulating admin approval)
        lecturers = approveAllLecturers(lecturers);
        System.out.println("✅ Approved all " + lecturers.size() + " lecturers");
        
        // Step 3: Create lectures with approved lecturers only
        List<Lecture> lectures = createFakeLectures(lectureCount);
        System.out.println("✅ Created " + lectures.size() + " lectures with approved lecturers");
        
        linkLecturersToLectures(lecturers, lectures);
        
        lecturerRepository.saveAll(lecturers);
        lectureRepository.saveAll(lectures);
        
        System.out.println("🎉 Fake data creation complete!");
    }

    /**
     * Creates a complete system with proper flow
     */
    public Map<String, Object> createCompleteSystemWithRelations(int lecturerCount, int lectureCount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("🚀 Starting complete system creation following proper flow...");
            
            // Step 1: Create lecturers in PENDING status
            List<Lecturer> lecturers = new ArrayList<>();
            for (int i = 0; i < lecturerCount; i++) {
                Lecturer lecturer = lecturerFakerService.generateFakeLecturer(); // PENDING by default
                lecturers.add(lecturer);
            }
            lecturers = lecturerRepository.saveAll(lecturers);
            System.out.println("✅ Created " + lecturers.size() + " lecturers in PENDING status");
            
            // Step 2: Approve all lecturers (simulating admin approval)
            lecturers = approveAllLecturers(lecturers);
            System.out.println("✅ Approved all " + lecturers.size() + " lecturers");
            
            // Step 3: Create lectures with approved lecturers only
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            if (approvedLecturers.isEmpty()) {
                throw new IllegalStateException("No approved lecturers available for lecture creation");
            }
            
            List<Lecture> lectures = new ArrayList<>();
            for (int i = 0; i < lectureCount; i++) {
                Lecture lecture = lectureFakerService.generateFakeLecture();
                lectures.add(lecture);
            }
            lectures = lectureRepository.saveAll(lectures);
            System.out.println("✅ Created " + lectures.size() + " lectures");
            
            // Step 4: Create relationships
            int totalRelations = 0;
            for (Lecture lecture : lectures) {
                Set<Lecturer> selectedLecturers = selectRandomLecturers(approvedLecturers);
                lecture.setLecturers(selectedLecturers);
                
                // Update both sides of the relationship
                for (Lecturer lecturer : selectedLecturers) {
                    lecturer.enrollLecture(lecture);
                }
                
                totalRelations += selectedLecturers.size();
            }
            
            // Save all relationships
            lectureRepository.saveAll(lectures);
            lecturerRepository.saveAll(lecturers);
            
            System.out.println("✅ Created " + totalRelations + " relationships");
            
            result.put("success", true);
            result.put("lecturers_created", lecturers.size());
            result.put("lectures_created", lectures.size());
            result.put("total_relations", totalRelations);
            result.put("approved_lecturers", approvedLecturers.size());
            // result.put("lecturers", lecturers);
            // result.put("lectures", lectures);
            
        } catch (Exception e) {
            System.err.println("❌ Error in createCompleteSystemWithRelations: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        
        return result;
    }

    // ========== Lecturer Management ==========

    public Map<String, Object> createSingleLecturer() {
        Map<String, Object> result = new HashMap<>();
        try {
            Lecturer lecturer = lecturerFakerService.generateFakeLecturer(); // PENDING by default
            Lecturer saved = lecturerRepository.save(lecturer);
            
            result.put("success", true);
            result.put("lecturer", saved);
            result.put("status", saved.getStatus());
            result.put("total_lecturers", lecturerRepository.count());
            result.put("message", "Lecturer created in PENDING status. Requires admin approval to create lectures.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> createMultipleLecturers(int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> lecturers = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                lecturers.add(lecturerFakerService.generateFakeLecturer()); // PENDING by default
            }
            
            List<Lecturer> saved = lecturerRepository.saveAll(lecturers);
            
            result.put("success", true);
            result.put("created_count", saved.size());
            result.put("lecturers", saved);
            result.put("total_lecturers", lecturerRepository.count());
            result.put("message", "Lecturers created in PENDING status. They need admin approval to create lectures.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    /**
     * Gets pending lecturers (admin function)
     */
    public Map<String, Object> getPendingLecturers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> pendingLecturers = lecturerRepository.findAll().stream()
                .filter(lecturer -> lecturer.getStatus() == LecturerStatus.PENDING)
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("pending_lecturers", pendingLecturers);
            result.put("pending_count", pendingLecturers.size());
            result.put("total_lecturers", lecturerRepository.count());
            
            System.out.println("📋 Found " + pendingLecturers.size() + " pending lecturers out of " + lecturerRepository.count() + " total");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
            System.err.println("❌ Error getting pending lecturers: " + e.getMessage());
        }
        return result;
    }

    /**
     * Approves all pending lecturers (admin function)
     */
    public Map<String, Object> approveAllPendingLecturers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> pendingLecturers = lecturerRepository.findAll().stream()
                .filter(lecturer -> lecturer.getStatus() == LecturerStatus.PENDING)
                .collect(Collectors.toList());
            
            if (pendingLecturers.isEmpty()) {
                result.put("success", true);
                result.put("message", "No pending lecturers to approve");
                result.put("approved_count", 0);
                return result;
            }
            
            pendingLecturers.forEach(lecturer -> {
                lecturer.setStatus(LecturerStatus.APPROVED);
                lecturer.setLastUpdatedAt(LocalDateTime.now());
            });
            
            List<Lecturer> approved = lecturerRepository.saveAll(pendingLecturers);
            
            result.put("success", true);
            result.put("approved_count", approved.size());
            result.put("approved_lecturers", approved);
            result.put("message", "Approved " + approved.size() + " lecturers");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    // ========== Lecture Creation (Flow-Aware) ==========

    public Map<String, Object> createSingleLecture() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            
            if (approvedLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "Cannot create lecture: No APPROVED lecturers available. Please approve some lecturers first.");
                return result;
            }
            
            Lecture lecture = lectureFakerService.generateFakeLecture();
            
            // Add random approved lecturers
            Set<Lecturer> lecturers = selectRandomLecturers(approvedLecturers);
            lecture.setLecturers(lecturers);
            
            // Update lecturers side of relationship
            for (Lecturer lecturer : lecturers) {
                lecturer.enrollLecture(lecture);
            }
            
            Lecture saved = lectureRepository.save(lecture);
            
            result.put("success", true);
            result.put("lecture", saved);
            result.put("assigned_lecturers", lecturers.size());
            result.put("total_lectures", lectureRepository.count());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> createMultipleLectures(int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            
            if (approvedLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "Cannot create lectures: No APPROVED lecturers available. Please approve some lecturers first.");
                return result;
            }
            
            List<Lecture> lectures = lectureFakerService.createLecturesWithLecturers(count);
            
            result.put("success", true);
            result.put("created_count", lectures.size());
            result.put("lectures", lectures);
            result.put("total_lectures", lectureRepository.count());
            result.put("used_approved_lecturers", approvedLecturers.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    // ========== Enhanced Lecture Creation with Existing Lecturers ==========

    public Map<String, Object> createLectureWithSpecificLecturer(Lecturer lecturer) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Check if lecturer is approved
            if (lecturer.getStatus() != LecturerStatus.APPROVED) {
                result.put("success", false);
                result.put("message", "Cannot create lecture: Lecturer is not APPROVED. Current status: " + lecturer.getStatus());
                return result;
            }
            
            Lecture lecture = lectureFakerService.generateFakeLecture();
            
            String expertise = getExpertiseFromLecturer(lecturer);
            if (expertise != null && !expertise.isEmpty()) {
                lecture.setTitle(generateTitleForExpertise(expertise) + " - by " + 
                    lecturer.getFirstName() + " " + lecturer.getLastName());
            }
            
            lecture = lectureRepository.save(lecture);
            
            lecture.getLecturers().add(lecturer);
            lecturer.enrollLecture(lecture);
            
            lectureRepository.save(lecture);
            lecturerRepository.save(lecturer);
            
            result.put("success", true);
            result.put("lecture", lecture);
            result.put("lecturer", lecturer);
            result.put("lecturer_status", lecturer.getStatus());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    public Map<String, Object> createMultipleLecturesWithExistingLecturers(int count, List<Lecturer> availableLecturers) {
        Map<String, Object> result = new HashMap<>();
        List<Lecture> createdLectures = new ArrayList<>();
        
        try {
            // Filter only approved lecturers
            List<Lecturer> approvedLecturers = availableLecturers.stream()
                .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
                .collect(Collectors.toList());
            
            if (approvedLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "Cannot create lectures: No APPROVED lecturers available from the provided list.");
                return result;
            }
            
            Random random = new Random();
            
            for (int i = 0; i < count; i++) {
                Lecturer selectedLecturer = approvedLecturers.get(random.nextInt(approvedLecturers.size()));
                
                Lecture lecture = lectureFakerService.generateFakeLecture();
                
                String expertise = getExpertiseFromLecturer(selectedLecturer);
                if (expertise != null && !expertise.isEmpty()) {
                    lecture.setTitle(generateTitleForExpertise(expertise) + 
                        " - Part " + (i + 1));
                }
                
                lecture = lectureRepository.save(lecture);
                
                lecture.getLecturers().add(selectedLecturer);
                selectedLecturer.enrollLecture(lecture);
                
                lectureRepository.save(lecture);
                lecturerRepository.save(selectedLecturer);
                
                createdLectures.add(lecture);
            }
            
            result.put("success", true);
            result.put("lectures", createdLectures);
            result.put("count", createdLectures.size());
            result.put("approved_lecturers_used", approvedLecturers.size());
            result.put("total_lecturers_available", availableLecturers.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // ========== Auto-Create Lecturer with Flow Support ==========

    public Map<String, Object> createLectureWithLecturerOrCreate(
            String firstName, String lastName, String email, Boolean createIfNotExists) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if ((firstName == null || firstName.trim().isEmpty()) && 
                (lastName == null || lastName.trim().isEmpty()) && 
                (email == null || email.trim().isEmpty())) {
                
                System.out.println("🎯 Creating new fake lecturer...");
                Lecturer newLecturer = lecturerFakerService.generateFakeLecturer(); // PENDING by default
                newLecturer = lecturerRepository.save(newLecturer);
                
                // Auto-approve for demo purposes (in real app, admin would approve)
                newLecturer.setStatus(LecturerStatus.APPROVED);
                newLecturer.setLastUpdatedAt(LocalDateTime.now());
                newLecturer = lecturerRepository.save(newLecturer);
                
                var lectureResult = createLectureWithSpecificLecturer(newLecturer);
                
                result.put("success", true);
                result.put("action", "created_fake_lecturer_and_lecture");
                result.put("message", "Created new fake lecturer and lecture: " + 
                    newLecturer.getFirstName() + " " + newLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", newLecturer);
                result.put("was_lecturer_created", true);
                result.put("lecturer_status", "APPROVED (auto-approved for demo)");
                
                return result;
            }

            Lecturer existingLecturer = findLecturerByDetails(firstName, lastName, email);
            
            if (existingLecturer != null) {
                // Check if lecturer is approved
                if (existingLecturer.getStatus() != LecturerStatus.APPROVED) {
                    result.put("success", false);
                    result.put("action", "lecturer_not_approved");
                    result.put("message", "Lecturer found but not approved. Current status: " + existingLecturer.getStatus());
                    result.put("lecturer", existingLecturer);
                    return result;
                }
                
                System.out.println("✅ Found existing approved lecturer: " + existingLecturer.getFirstName() + " " + existingLecturer.getLastName());
                
                var lectureResult = createLectureWithSpecificLecturer(existingLecturer);
                
                result.put("success", true);
                result.put("action", "used_existing_lecturer");
                result.put("message", "Created lecture for existing approved lecturer: " + 
                    existingLecturer.getFirstName() + " " + existingLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", existingLecturer);
                result.put("was_lecturer_created", false);
                result.put("lecturer_status", existingLecturer.getStatus());
                
            } else if (createIfNotExists == null || createIfNotExists) {
                System.out.println("🆕 Creating new lecturer...");
                
                Lecturer newLecturer = createLecturerFromDetails(firstName, lastName, email);
                newLecturer = lecturerRepository.save(newLecturer);
                
                // Auto-approve for demo purposes (in real app, admin would approve)
                newLecturer.setStatus(LecturerStatus.APPROVED);
                newLecturer.setLastUpdatedAt(LocalDateTime.now());
                newLecturer = lecturerRepository.save(newLecturer);
                
                var lectureResult = createLectureWithSpecificLecturer(newLecturer);
                
                result.put("success", true);
                result.put("action", "created_new_lecturer_and_lecture");
                result.put("message", "Created new lecturer and lecture: " + 
                    newLecturer.getFirstName() + " " + newLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", newLecturer);
                result.put("was_lecturer_created", true);
                result.put("lecturer_status", "APPROVED (auto-approved for demo)");
                
            } else {
                result.put("success", false);
                result.put("action", "lecturer_not_found");
                result.put("message", "Lecturer not found and createIfNotExists is false");
                result.put("search_details", Map.of(
                    "firstName", firstName != null ? firstName : "",
                    "lastName", lastName != null ? lastName : "",
                    "email", email != null ? email : ""
                ));
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("action", "error");
            result.put("error", e.getMessage());
            System.err.println("❌ Error creating lecture with lecturer: " + e.getMessage());
        }
        
        return result;
    }

    // ========== Search Methods (Flow-Aware) ==========

    public Map<String, Object> searchLecturersByDetails(String searchTerm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Only search among approved lecturers (following the flow)
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            String lowerSearchTerm = searchTerm.toLowerCase().trim();
            
            List<Lecturer> matchingLecturers = approvedLecturers.stream()
                .filter(lecturer -> {
                    String fullName = (lecturer.getFirstName() + " " + lecturer.getLastName()).toLowerCase();
                    String email = lecturer.getEmail() != null ? lecturer.getEmail().toLowerCase() : "";
                    
                    return fullName.contains(lowerSearchTerm) ||
                           lecturer.getFirstName().toLowerCase().contains(lowerSearchTerm) ||
                           lecturer.getLastName().toLowerCase().contains(lowerSearchTerm) ||
                           email.contains(lowerSearchTerm);
                })
                .collect(Collectors.toList());
            
            List<Map<String, Object>> detailedResults = matchingLecturers.stream()
                .map(lecturer -> {
                    Map<String, Object> info = createDetailedLecturerInfo(lecturer);
                    info.put("match_reason", getMatchReason(lecturer, lowerSearchTerm));
                    return info;
                })
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("search_term", searchTerm);
            result.put("matches_found", matchingLecturers.size());
            result.put("matching_lecturers", detailedResults);
            result.put("total_approved_lecturers", approvedLecturers.size());
            result.put("total_lecturers_in_db", lecturerRepository.count());
            result.put("search_scope", "APPROVED lecturers only");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // ========== Helper Methods ==========

    private List<Lecturer> getApprovedLecturers() {
        return lecturerRepository.findAll().stream()
            .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
            .collect(Collectors.toList());
    }

    private List<Lecturer> approveAllLecturers(List<Lecturer> lecturers) {
        lecturers.forEach(lecturer -> {
            lecturer.setStatus(LecturerStatus.APPROVED);
            lecturer.setLastUpdatedAt(LocalDateTime.now());
        });
        return lecturerRepository.saveAll(lecturers);
    }

    private Lecturer findLecturerByDetails(String firstName, String lastName, String email) {
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        
        if (email != null && !email.trim().isEmpty()) {
            Optional<Lecturer> byEmail = allLecturers.stream()
                .filter(lecturer -> lecturer.getEmail() != null && 
                        lecturer.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
            
            if (byEmail.isPresent()) {
                System.out.println("🔍 Found lecturer by email: " + email);
                return byEmail.get();
            }
        }
        
        if (firstName != null && !firstName.trim().isEmpty() && 
            lastName != null && !lastName.trim().isEmpty()) {
            
            Optional<Lecturer> byFullName = allLecturers.stream()
                .filter(lecturer -> 
                    lecturer.getFirstName() != null && lecturer.getLastName() != null &&
                    lecturer.getFirstName().equalsIgnoreCase(firstName.trim()) &&
                    lecturer.getLastName().equalsIgnoreCase(lastName.trim()))
                .findFirst();
            
            if (byFullName.isPresent()) {
                System.out.println("🔍 Found lecturer by full name: " + firstName + " " + lastName);
                return byFullName.get();
            }
        }
        
        return null;
    }

    private Lecturer createLecturerFromDetails(String firstName, String lastName, String email) {
        if (firstName == null || firstName.trim().isEmpty()) {
            firstName = faker.name().firstName();
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            lastName = faker.name().lastName();
        }
        if (email == null || email.trim().isEmpty()) {
            email = (firstName + "." + lastName + "." + System.currentTimeMillis() 
                    + "@" + faker.internet().domainName()).toLowerCase();
        }
        
        String specialty = faker.job().title();
        int experience = faker.number().numberBetween(3, 18);
        String bio = String.format(
            "%s is a %s with %d years of experience. Created for lecture integration.",
            firstName, specialty, experience
        );
        
        Lecturer lecturer = Lecturer.builder()
            .userId(UUID.randomUUID())
            .firstName(firstName.trim())
            .lastName(lastName.trim())
            .bio(bio)
            .city(faker.address().cityName())
            .email(email.trim().toLowerCase())
            .phone(generateUniqueIsraeliPhone())
            .imageUrl("https://picsum.photos/400/400?random=" + faker.random().nextInt(10000))
            .workingArea(getRandomWorkingArea())
            .status(LecturerStatus.PENDING) // Important: Start with PENDING
            .createdAt(LocalDateTime.now())
            .lastUpdatedAt(LocalDateTime.now())
            .build();
        
        return lecturer;
    }

    private String generateUniqueIsraeliPhone() {
        String[] prefixes = {"050", "052", "053", "054", "055", "058"};
        String prefix = prefixes[faker.random().nextInt(prefixes.length)];
        long timestamp = System.currentTimeMillis() % 10000000;
        return prefix + String.format("%07d", timestamp);
    }

    private Area getRandomWorkingArea() {
        Area[] areas = Area.values();
        return areas[faker.random().nextInt(areas.length)];
    }

    // ========== Existing Methods (Updated for Flow) ==========

    public Map<String, Object> createIsraeliTechLectures(int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            
            if (approvedLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "Cannot create Israeli tech lectures: No APPROVED lecturers available.");
                return result;
            }
            
            List<Lecture> lectures = lectureFakerService.createIsraeliTechLectures(count);
            
            result.put("success", true);
            result.put("created_count", lectures.size());
            result.put("lectures", lectures);
            result.put("total_lectures", lectureRepository.count());
            result.put("approved_lecturers_used", approvedLecturers.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> generateUpcomingLectures(int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            
            if (approvedLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "Cannot create upcoming lectures: No APPROVED lecturers available.");
                return result;
            }
            
            List<Lecture> lectures = lectureFakerService.generateUpcomingLectures(count);
            
            result.put("success", true);
            result.put("created_count", lectures.size());
            result.put("lectures", lectures);
            result.put("total_lectures", lectureRepository.count());
            result.put("approved_lecturers_used", approvedLecturers.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> generateSingleLectureWithLecturers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            
            if (approvedLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "Cannot create lecture: No APPROVED lecturers available.");
                return result;
            }
            
            Lecture lecture = lectureFakerService.generateLectureWithLecturers();
            
            result.put("success", true);
            result.put("lecture", lecture);
            result.put("attached_lecturers", lecture.getLecturers().size());
            result.put("approved_lecturers_available", approvedLecturers.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    // ========== Complete Implementation of Missing Methods ==========
    
    public Map<String, Object> createMultipleLecturesWithAutoCreateLecturers(int count, List<String[]> lecturerDetailsList) {
        Map<String, Object> result = new HashMap<>();
        List<Lecture> createdLectures = new ArrayList<>();
        List<Lecturer> createdLecturers = new ArrayList<>();
        List<Lecturer> usedExistingLecturers = new ArrayList<>();
        
        try {
            System.out.println("🎯 Creating " + count + " lectures with auto-create lecturers following flow...");
            
            for (int i = 0; i < count; i++) {
                String firstName = null;
                String lastName = null;
                String email = null;
             
                if (lecturerDetailsList != null && !lecturerDetailsList.isEmpty()) {
                    int lecturerIndex = i % lecturerDetailsList.size();
                    String[] details = lecturerDetailsList.get(lecturerIndex);
                    
                    if (details.length > 0) firstName = details[0];
                    if (details.length > 1) lastName = details[1];
                    if (details.length > 2) email = details[2];
                }
                
                var lectureResult = createLectureWithLecturerOrCreate(firstName, lastName, email, true);
                
                if ((Boolean) lectureResult.get("success")) {
                    createdLectures.add((Lecture) lectureResult.get("lecture"));
                    
                    Lecturer lecturer = (Lecturer) lectureResult.get("lecturer");
                    if ((Boolean) lectureResult.get("was_lecturer_created")) {
                        createdLecturers.add(lecturer);
                    } else {
                        usedExistingLecturers.add(lecturer);
                    }
                }
            }
            
            result.put("success", true);
            result.put("message", "Created " + createdLectures.size() + " lectures successfully");
            result.put("lectures", createdLectures);
            result.put("lectures_count", createdLectures.size());
            result.put("new_lecturers", createdLecturers);
            result.put("new_lecturers_count", createdLecturers.size());
            result.put("existing_lecturers_used", usedExistingLecturers);
            result.put("existing_lecturers_count", usedExistingLecturers.size());
            result.put("total_lectures_in_db", lectureRepository.count());
            result.put("total_lecturers_in_db", lecturerRepository.count());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            System.err.println("❌ Error creating multiple lectures: " + e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> getAllLecturesWithDetails(int page, int size, String sortBy, String sortDirection) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<Lecture> lecturePage = lectureRepository.findAll(pageable);
            
            List<Map<String, Object>> detailedLectures = lecturePage.getContent().stream()
                .map(this::createDetailedLectureInfo)
                .collect(Collectors.toList());
            
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("current_page", page);
            pagination.put("page_size", size);
            pagination.put("total_pages", lecturePage.getTotalPages());
            pagination.put("total_elements", lecturePage.getTotalElements());
            pagination.put("has_next", lecturePage.hasNext());
            pagination.put("has_previous", lecturePage.hasPrevious());
            
            Map<String, Object> statistics = generateLectureStatistics();
            
            result.put("success", true);
            result.put("lectures", detailedLectures);
            result.put("pagination", pagination);
            result.put("statistics", statistics);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> getLecturesByLecturer(Lecturer lecturer) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Lecture> lectures = new ArrayList<>(lecturer.getLectures());
            lectures.sort((l1, l2) -> l1.getCreatedAt().compareTo(l2.getCreatedAt()));
            
            List<Map<String, Object>> detailedLectures = lectures.stream()
                .map(this::createDetailedLectureInfo)
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("lectures", detailedLectures);
            result.put("count", lectures.size());
            result.put("lecturer_info", createDetailedLecturerInfo(lecturer));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> getLecturesByLecturerName(String lecturerName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            List<Lecturer> matchingLecturers = approvedLecturers.stream()
                .filter(lecturer -> {
                    String fullName = (lecturer.getFirstName() + " " + lecturer.getLastName()).toLowerCase();
                    return fullName.contains(lecturerName.toLowerCase()) ||
                           lecturer.getFirstName().toLowerCase().contains(lecturerName.toLowerCase()) ||
                           lecturer.getLastName().toLowerCase().contains(lecturerName.toLowerCase());
                })
                .collect(Collectors.toList());
            
            if (matchingLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "No approved lecturers found with name: " + lecturerName);
                return result;
            }
            
            List<Lecture> allMatchingLectures = new ArrayList<>();
            Map<String, Object> lecturesByLecturer = new HashMap<>();
            
            for (Lecturer lecturer : matchingLecturers) {
                List<Lecture> lecturerLectures = new ArrayList<>(lecturer.getLectures());
                lecturerLectures.sort((l1, l2) -> l1.getCreatedAt().compareTo(l2.getCreatedAt()));
                
                List<Map<String, Object>> detailedLectures = lecturerLectures.stream()
                    .map(this::createDetailedLectureInfo)
                    .collect(Collectors.toList());
                
                lecturesByLecturer.put(lecturer.getFirstName() + " " + lecturer.getLastName(), 
                    Map.of(
                        "lecturer_info", createDetailedLecturerInfo(lecturer),
                        "lectures", detailedLectures,
                        "count", lecturerLectures.size()
                    ));
                
                allMatchingLectures.addAll(lecturerLectures);
            }
            
            allMatchingLectures.sort((l1, l2) -> l1.getCreatedAt().compareTo(l2.getCreatedAt()));
            
            List<Map<String, Object>> allDetailedLectures = allMatchingLectures.stream()
                .map(this::createDetailedLectureInfo)
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("matching_lecturers", matchingLecturers.stream()
                .map(this::createDetailedLecturerInfo)
                .collect(Collectors.toList()));
            result.put("lectures", allDetailedLectures);
            result.put("count", allMatchingLectures.size());
            result.put("total_lectures", allMatchingLectures.size());
            result.put("lectures_by_lecturer", lecturesByLecturer);
            result.put("lecturers_found", matchingLecturers.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> createFakeLectureForLecturerName(String lecturerName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Lecturer> approvedLecturers = getApprovedLecturers();
            List<Lecturer> matchingLecturers = approvedLecturers.stream()
                .filter(lecturer -> {
                    String fullName = (lecturer.getFirstName() + " " + lecturer.getLastName()).toLowerCase();
                    return fullName.contains(lecturerName.toLowerCase()) ||
                           lecturer.getFirstName().toLowerCase().contains(lecturerName.toLowerCase()) ||
                           lecturer.getLastName().toLowerCase().contains(lecturerName.toLowerCase());
                })
                .collect(Collectors.toList());
            
            if (matchingLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "No approved lecturers found with name: " + lecturerName);
                return result;
            }
            
            Lecturer selectedLecturer = matchingLecturers.get(0);
            if (matchingLecturers.size() > 1) {
                selectedLecturer = matchingLecturers.get(random.nextInt(matchingLecturers.size()));
            }
            
            var lectureResult = createLectureWithSpecificLecturer(selectedLecturer);
            
            if ((Boolean) lectureResult.get("success")) {
                result.put("success", true);
                result.put("message", "Created fake lecture for approved lecturer: " + 
                    selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", selectedLecturer);
                result.put("matching_lecturers_count", matchingLecturers.size());
                
                if (matchingLecturers.size() > 1) {
                    result.put("note", "Found " + matchingLecturers.size() + " approved lecturers. Selected: " + 
                        selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName());
                }
            } else {
                result.put("success", false);
                result.put("message", "Error creating lecture: " + lectureResult.get("error"));
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> createFakeLectureWithRandomLecturer(List<Lecturer> availableLecturers) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Lecturer> approvedLecturers = availableLecturers.stream()
                .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
                .collect(Collectors.toList());
            
            if (approvedLecturers.isEmpty()) {
                result.put("success", false);
                result.put("error", "No approved lecturers available from the provided list");
                return result;
            }
            
            Lecturer randomLecturer = approvedLecturers.get(random.nextInt(approvedLecturers.size()));
            
            var lectureResult = createLectureWithSpecificLecturer(randomLecturer);
            
            if ((Boolean) lectureResult.get("success")) {
                result.put("success", true);
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", randomLecturer);
                result.put("approved_lecturers_count", approvedLecturers.size());
                result.put("total_lecturers_available", availableLecturers.size());
            } else {
                result.put("success", false);
                result.put("error", "Error creating lecture: " + lectureResult.get("error"));
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> createLectureSeriesForLecturer(Lecturer lecturer, int seriesCount, boolean isWorkshopSeries) {
        Map<String, Object> result = new HashMap<>();
        List<Lecture> createdLectures = new ArrayList<>();
        
        try {
            if (lecturer.getStatus() != LecturerStatus.APPROVED) {
                result.put("success", false);
                result.put("message", "Cannot create lecture series: Lecturer is not approved. Status: " + lecturer.getStatus());
                return result;
            }
            
            String seriesTheme = isWorkshopSeries ? "Workshop" : "Course";
            String expertise = getExpertiseFromLecturer(lecturer);
            String baseTitle = generateTitleForExpertise(expertise);
            
            LocalDateTime startDate = LocalDateTime.now().plusDays(7);
            
            for (int i = 0; i < seriesCount; i++) {
                Lecture lecture = lectureFakerService.generateFakeLecture();
                
                lecture.setTitle(seriesTheme + " " + baseTitle + " - Session " + (i + 1) + " of " + seriesCount);
                lecture.setCreatedAt(startDate.plusWeeks(i));
                lecture.setUpdatedAt(startDate.plusWeeks(i).plusHours(isWorkshopSeries ? 4 : 2));
                
                if (isWorkshopSeries) {
                    lecture.setPrice(lecture.getPrice() + 100);
                    lecture.setDescription("Practical workshop in " + expertise + 
                        " - Session " + (i + 1) + ". Includes hands-on exercises and personal mentoring.");
                }
                
                lecture = lectureRepository.save(lecture);
                
                lecture.getLecturers().add(lecturer);
                lecturer.enrollLecture(lecture);
                
                lectureRepository.save(lecture);
                createdLectures.add(lecture);
            }
            
            lecturerRepository.save(lecturer);
            
            result.put("success", true);
            result.put("lectures", createdLectures);
            result.put("series_count", seriesCount);
            result.put("series_type", isWorkshopSeries ? "workshop" : "course");
            result.put("lecturer", lecturer);
            result.put("lecturer_status", lecturer.getStatus());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // ========== Helper Methods for Detailed Info ==========
    
    private Map<String, Object> createDetailedLectureInfo(Lecture lecture) {
        Map<String, Object> info = new HashMap<>();
        
        info.put("id", lecture.getLectureId());
        info.put("title", lecture.getTitle());
        info.put("description", lecture.getDescription());
        info.put("created_at", lecture.getCreatedAt());
        info.put("updated_at", lecture.getUpdatedAt());
        info.put("price", lecture.getPrice());
        info.put("is_online", lecture.isOnline());
        info.put("duration", lecture.getDuration());
        info.put("status", lecture.getStatus());
        
        List<Map<String, Object>> lecturersInfo = lecture.getLecturers().stream()
            .map(this::createBasicLecturerInfo)
            .collect(Collectors.toList());
        info.put("lecturers", lecturersInfo);
        info.put("lecturers_count", lecturersInfo.size());
        info.put("duration_minutes", lecture.getDuration());
        
        return info;
    }
    
    private Map<String, Object> createBasicLecturerInfo(Lecturer lecturer) {
        Map<String, Object> info = new HashMap<>();
        
        info.put("id", lecturer.getUserId());
        info.put("name", lecturer.getFirstName() + " " + lecturer.getLastName());
        info.put("email", lecturer.getEmail());
        info.put("status", lecturer.getStatus());
        info.put("expertise", getExpertiseFromLecturer(lecturer));
        
        return info;
    }
    
    private Map<String, Object> createDetailedLecturerInfo(Lecturer lecturer) {
        Map<String, Object> info = createBasicLecturerInfo(lecturer);
        
        info.put("total_lectures", lecturer.getLectures().size());
        info.put("upcoming_lectures", lecturer.getLectures().stream()
            .mapToInt(lecture -> lecture.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)) ? 1 : 0)
            .sum());
        
        return info;
    }
    
    private Map<String, Object> generateLectureStatistics() {
        List<Lecture> allLectures = lectureRepository.findAll();
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total_lectures", allLectures.size());
        stats.put("total_lecturers", allLecturers.size());

        long onlineLectures = allLectures.stream().filter(Lecture::isOnline).count();
        stats.put("online_lectures", onlineLectures);
        stats.put("physical_lectures", allLectures.size() - onlineLectures);
        
        long recentLectures = allLectures.stream()
            .filter(lecture -> lecture.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)))
            .count();
        stats.put("recent_lectures", recentLectures);
        stats.put("older_lectures", allLectures.size() - recentLectures);
    
        OptionalDouble avgPrice = allLectures.stream().mapToInt(Lecture::getPrice).average();
        stats.put("average_price", avgPrice.isPresent() ? Math.round(avgPrice.getAsDouble() * 100.0) / 100.0 : 0);
        
        IntSummaryStatistics priceStats = allLectures.stream()
            .mapToInt(Lecture::getPrice)
            .summaryStatistics();
        stats.put("min_price", priceStats.getMin());
        stats.put("max_price", priceStats.getMax());
        
        long lecturersWithLectures = allLecturers.stream()
            .filter(lecturer -> !lecturer.getLectures().isEmpty())
            .count();
        stats.put("active_lecturers", lecturersWithLectures);
        stats.put("inactive_lecturers", allLecturers.size() - lecturersWithLectures);
        
        return stats;
    }

    // ========== Other existing methods remain the same but with flow awareness ==========
    
    private List<Lecturer> createFakeLecturers(int count) {
        List<Lecturer> lecturers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                lecturers.add(lecturerFakerService.generateFakeLecturer()); // PENDING by default
            } catch (Exception e) {
                System.err.println("Error creating lecturer " + i + ": " + e.getMessage());
            }
        }
        return lecturerRepository.saveAll(lecturers);
    }

    private List<Lecture> createFakeLectures(int count) {
        List<Lecture> lectures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                lectures.add(lectureFakerService.generateFakeLecture());
            } catch (Exception e) {
                System.err.println("Error creating lecture " + i + ": " + e.getMessage());
            }
        }
        return lectureRepository.saveAll(lectures);
    }

    private void linkLecturersToLectures(List<Lecturer> lecturers, List<Lecture> lectures) {
        // Only use approved lecturers
        List<Lecturer> approvedLecturers = lecturers.stream()
            .filter(lecturer -> lecturer.getStatus() == LecturerStatus.APPROVED)
            .collect(Collectors.toList());
            
        if (approvedLecturers.isEmpty() || lectures.isEmpty()) return;

        for (Lecture lecture : lectures) {
            int numLecturers = random.nextInt(3) + 1;
            Set<Lecturer> selectedLecturers = random.ints(0, approvedLecturers.size())
                .distinct()
                .limit(numLecturers)
                .mapToObj(approvedLecturers::get)
                .collect(Collectors.toSet());
            
            lecture.setLecturers(selectedLecturers);
            
            for (Lecturer lecturer : selectedLecturers) {
                lecturer.enrollLecture(lecture);
            }
        }
    }

    private Set<Lecturer> selectRandomLecturers(List<Lecturer> availableLecturers) {
        Set<Lecturer> selected = new HashSet<>();
        int numberOfLecturers = faker.number().numberBetween(1, Math.min(4, availableLecturers.size() + 1));
        
        while (selected.size() < numberOfLecturers && selected.size() < availableLecturers.size()) {
            selected.add(availableLecturers.get(random.nextInt(availableLecturers.size())));
        }
        return selected;
    }

    private String getExpertiseFromLecturer(Lecturer lecturer) {
        if (lecturer.getEmail() != null) {
            String email = lecturer.getEmail().toLowerCase();
            if (email.contains("ai") || email.contains("ml")) return "AI";
            if (email.contains("data")) return "Data Science";
            if (email.contains("security") || email.contains("cyber")) return "Cybersecurity";
            if (email.contains("cloud")) return "Cloud";
            if (email.contains("dev") || email.contains("prog")) return "Programming";
            if (email.contains("manage")) return "Management";
        }
        return "Technology";
    }

    private String generateTitleForExpertise(String expertise) {
        if (expertise == null || expertise.isEmpty()) {
            expertise = "Technology";
        }
        
        Map<String, List<String>> expertiseTitles = Map.of(
            "Technology", List.of("Technological Innovation", "Technology Trends", "Future of Technology"),
            "AI", List.of("AI for Beginners", "AI Applications in Business", "Future of AI"),
            "Data Science", List.of("Data Science in Practice", "Advanced Data Analysis", "Big Data for Managers"),
            "Cybersecurity", List.of("Advanced Information Security", "Digital World Protection", "Ethics in Security"),
            "Cloud", List.of("Cloud Computing for Beginners", "Cloud Architecture", "Cloud Security"),
            "Programming", List.of("Advanced Programming", "Application Development", "Clean Code"),
            "Management", List.of("Team Management", "Digital Age Leadership", "Project Management")
        );
        
        List<String> titles = expertiseTitles.get(expertise);
        if (titles != null && !titles.isEmpty()) {
            return titles.get(random.nextInt(titles.size()));
        }
        
        return "Lecture in " + expertise;
    }

    private String getMatchReason(Lecturer lecturer, String searchTerm) {
        if (lecturer.getFirstName().toLowerCase().contains(searchTerm)) {
            return "first_name_match";
        }
        if (lecturer.getLastName().toLowerCase().contains(searchTerm)) {
            return "last_name_match";
        }
        if (lecturer.getEmail() != null && lecturer.getEmail().toLowerCase().contains(searchTerm)) {
            return "email_match";
        }
        return "full_name_match";
    }
}