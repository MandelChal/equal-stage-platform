package com.equal_stage_platform.dev.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.fake.LectureFakerService;
import com.equal_stage_platform.dev.fake.LecturerFakerService;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.model.enums.Role;

import net.datafaker.Faker;

@Service
public class FakerDataService {

    @Autowired
    private AuthService authService;

    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private LectureService lectureService;

    @Autowired
    private LecturerFakerService lecturerFakerService;

    @Autowired
    private LectureFakerService lectureFakerService;

    @Autowired
    private JwtService jwtService;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // ========== User Registration Methods ==========

    @Transactional
    public Map<String, Object> createFakeUser() {
        String method = "createFakeUser";
        try {
            System.out.println("Creating a new fake user...");

            String firstName = generateHebrewFirstName();
            String lastName = generateHebrewLastName();
            String email = generateUniqueEmail(firstName, lastName);
            String password = generateSecurePassword();
            boolean isAdmin = random.nextBoolean();

            authService.register(email, password);

            Map<String, String> loginResult = authService.login(email, password);
            String token = loginResult.get("token");
            UUID userId = jwtService.extractUserId(token);

            if (isAdmin) {
                authService.changeRole(userId, Role.ADMIN);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("firstName", firstName);
            response.put("lastName", lastName);
            response.put("userId", userId);
            response.put("email", email);
            response.put("password", password);
            response.put("admin", isAdmin);
            response.put("role", isAdmin ? "ADMIN" : "USER");
            response.put("createdAt", LocalDateTime.now());
            response.put("token", token);

            System.out.println("User created: " + firstName + " " + lastName + " (Admin: " + isAdmin + ")");
            return response;

        } catch (Exception e) {
            System.err.println("[" + method + "] Error creating user: " + e.getMessage());
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResponseLecturerDTO createApprovedLecturer() {
        String method = "createApprovedLecturer";
        try {
            System.out.println("Creating a new approved lecturer...");

            ResponseLecturerDTO lecturer = lecturerFakerService.generateApprovedFakeLecturer();

            System.out.println("Approved lecturer created: " + lecturer.getFirstName() + " " + lecturer.getLastName());
            return lecturer;

        } catch (Exception e) {
            System.err.println("[" + method + "] Error creating approved lecturer: " + e.getMessage());
            throw new RuntimeException("Error creating approved lecturer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResponseLecturerDTO createPendingLecturer() {
        String method = "createPendingLecturer";
        try {
            System.out.println("Creating a new lecturer with PENDING status...");

            ResponseLecturerDTO lecturer = lecturerFakerService.generateFakeLecturer();

            System.out.println("Pending lecturer created: " + lecturer.getFirstName() + " " + lecturer.getLastName());
            return lecturer;

        } catch (Exception e) {
            System.err.println("[" + method + "] Error creating pending lecturer: " + e.getMessage());
            throw new RuntimeException("Error creating pending lecturer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Map<String, Object> createLectureWithApprovedLecturer() {
        String method = "createLectureWithApprovedLecturer";
        try {
            System.out.println("Creating a new lecture with an approved lecturer...");

            ResponseLecturerDTO lecturer = lecturerFakerService.generateApprovedFakeLecturer();
            ResponseLectureDTO lecture = lectureFakerService.createLectureForSpecificLecturer(lecturer.getUserId());
            
            long totalLectures = getTotalLecturesCount();

            Map<String, Object> response = new HashMap<>();
            response.put("lecture", lecture);
            response.put("lecturer", lecturer);
            response.put("lecturerCreated", true);
            response.put("totalLecturesInSystem", totalLectures);
            response.put("createdAt", LocalDateTime.now());

            System.out.println("Lecture created: " + lecture.getTitle() + " with a new lecturer");
            return response;

        } catch (Exception e) {
            System.err.println("[" + method + "] Error creating lecture with new lecturer: " + e.getMessage());
            throw new RuntimeException("Error creating lecture: " + e.getMessage(), e);
        }
    }

@Transactional
public Map<String, Object> createLectureWithExistingLecturer() {
    String method = "createLectureWithExistingLecturer";
    try {
        System.out.println("Creating a new lecture with an existing lecturer...");

        List<ResponseLecturerDTO> approvedLecturers = getApprovedLecturers();
        
        if (approvedLecturers.isEmpty()) {
            throw new IllegalStateException("Cannot create lecture: no approved lecturers in the system. Please approve lecturers first.");
        }

        // FIX: Get a random existing lecturer FIRST
        ResponseLecturerDTO selectedLecturer = approvedLecturers.get(random.nextInt(approvedLecturers.size()));
        
        // Then create lecture for that specific lecturer
        ResponseLectureDTO lecture = lectureFakerService.createLectureForSpecificLecturer(selectedLecturer.getUserId());
        
        long totalLectures = getTotalLecturesCount();

        Map<String, Object> response = new HashMap<>();
        response.put("lecture", lecture);
        response.put("lecturer", selectedLecturer);  // Use the selected lecturer directly
        response.put("lecturerCreated", false);
        response.put("totalLecturesInSystem", totalLectures);
        response.put("createdAt", LocalDateTime.now());

        System.out.println("Lecture created: " + lecture.getTitle() + " with existing lecturer: " + 
            selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName());
        return response;

    } catch (Exception e) {
        System.err.println("[" + method + "] Error creating lecture with existing lecturer: " + e.getMessage());
        throw new RuntimeException("Error creating lecture: " + e.getMessage(), e);
    }
}
    @Transactional
    public Map<String, Object> initializeCompleteSystem(int lecturersCount, int lecturesPerLecturer) {
        String method = "initializeCompleteSystem";
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            System.out.println("Initializing complete system: " + lecturersCount + " lecturers, " + 
                lecturesPerLecturer + " lectures per lecturer");

            List<ResponseLecturerDTO> createdLecturers = new ArrayList<>();
            List<ResponseLectureDTO> createdLectures = new ArrayList<>();

            for (int i = 0; i < lecturersCount; i++) {
                try {
                    ResponseLecturerDTO lecturer = lecturerFakerService.generateApprovedFakeLecturer();
                    createdLecturers.add(lecturer);
                    System.out.println("Lecturer " + (i + 1) + "/" + lecturersCount + ": " + 
                        lecturer.getFirstName() + " " + lecturer.getLastName());
                } catch (Exception e) {
                    System.err.println("[" + method + "] Error creating lecturer " + (i + 1) + ": " + e.getMessage());
                }
            }

            for (ResponseLecturerDTO lecturer : createdLecturers) {
                for (int j = 0; j < lecturesPerLecturer; j++) {
                    try {
                        ResponseLectureDTO lecture = lectureFakerService.createLectureForSpecificLecturer(lecturer.getUserId());
                        createdLectures.add(lecture);
                        System.out.println("Lecture " + (j + 1) + "/" + lecturesPerLecturer + 
                            " for lecturer: " + lecturer.getFirstName() + " - " + lecture.getTitle());
                    } catch (Exception e) {
                        System.err.println("[" + method + "] Error creating lecture for lecturer " + lecturer.getFirstName() + ": " + e.getMessage());
                    }
                }
            }

            LocalDateTime endTime = LocalDateTime.now();
            long durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();

            Map<String, Object> response = new HashMap<>();
            response.put("lecturersCreated", createdLecturers.size());
            response.put("lecturesCreated", createdLectures.size());
            response.put("totalLecturersInSystem", (int) getTotalLecturersCount());
            response.put("totalLecturesInSystem", (int) getTotalLecturesCount());
            response.put("createdLecturers", createdLecturers);
            response.put("createdLectures", createdLectures);
            response.put("initStartTime", startTime);
            response.put("initEndTime", endTime);
            response.put("durationInSeconds", durationSeconds);
            response.put("formattedDuration", formatDuration(durationSeconds));

            System.out.println("System initialization completed successfully in " + durationSeconds + " seconds");
            return response;

        } catch (Exception e) {
            System.err.println("[" + method + "] Error initializing system: " + e.getMessage());
            throw new RuntimeException("Error initializing system: " + e.getMessage(), e);
        }
    }

    // Helper methods remain the same, but their errors also log method name if needed
    @Transactional(readOnly = true)
    private List<ResponseLecturerDTO> getApprovedLecturers() {
        String method = "getApprovedLecturers";
        try {
            return lecturerService.getLecturersByStatus(LecturerStatus.APPROVED);
        } catch (Exception e) {
            System.err.println("[" + method + "] Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    private ResponseLecturerDTO findLecturerById(UUID lecturerId) {
        String method = "findLecturerById";
        try {
            return lecturerService.getLecturerById(lecturerId, true);
        } catch (Exception e) {
            System.err.println("[" + method + "] Error: " + e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    private long getTotalLecturersCount() {
        String method = "getTotalLecturersCount";
        try {
            return lecturerService.getAllLecturers().size();
        } catch (Exception e) {
            System.err.println("[" + method + "] Error: " + e.getMessage());
            return 0;
        }
    }

    @Transactional(readOnly = true)
    private long getTotalLecturesCount() {
        String method = "getTotalLecturesCount";
        try {
            return lectureService.getAllLecturesAdmin().size();
        } catch (Exception e) {
            System.err.println("[" + method + "] Error: " + e.getMessage());
            return 0;
        }
    }

    private String formatDuration(long durationInSeconds) {
        if (durationInSeconds < 60) {
            return durationInSeconds + " seconds";
        } else if (durationInSeconds < 3600) {
            long minutes = durationInSeconds / 60;
            long seconds = durationInSeconds % 60;
            return minutes + " minutes and " + seconds + " seconds";
        } else {
            long hours = durationInSeconds / 3600;
            long remainingSeconds = durationInSeconds % 3600;
            long minutes = remainingSeconds / 60;
            long seconds = remainingSeconds % 60;
            return hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
        }
    }

    private String generateHebrewFirstName() {
        String[] hebrewFirstNames = {
            "אברהם", "יצחק", "יעקב", "משה", "אהרן", "דוד", "שלמה", "דניאל", "יונתן", "מיכאל",
            "עמית", "רוני", "תומר", "אלון", "גיל", "רן", "אורי", "איתן", "נועם", "עידו",
            "שרה", "רבקה", "רחל", "לאה", "מרים", "דבורה", "רות", "חנה", "אסתר", "יהודית",
            "נועה", "מיכל", "תמר", "שירה", "רעות", "ליאת", "הילה", "ענת", "טל", "מאיה"
        };
        return hebrewFirstNames[random.nextInt(hebrewFirstNames.length)];
    }

    private String generateHebrewLastName() {
        String[] hebrewLastNames = {
            "כהן", "לוי", "מילר", "מזרחי", "פרץ", "ביטון", "דהן", "אברהם", "פרידמן", "מלכה",
            "אזולאי", "כץ", "יעקב", "דוד", "משה", "גולדברג", "שוורץ", "רוזן", "קליין", "שפירא",
            "אלון", "בר", "זהבי", "שמש", "צור", "אבן", "גבע", "הררי", "ברק", "שלום"
        };
        return hebrewLastNames[random.nextInt(hebrewLastNames.length)];
    }

    private String generateUniqueEmail(String firstName, String lastName) {
        String englishFirst = transliterateToEnglish(firstName);
        String englishLast = transliterateToEnglish(lastName);
        String baseEmail = (englishFirst + "." + englishLast + "." + System.currentTimeMillis()).toLowerCase();
        String domain = getRandomDomain();
        return baseEmail + "@" + domain;
    }

    private String transliterateToEnglish(String hebrew) {
        String[][] mapping = {
            {"א", "a"}, {"ב", "b"}, {"ג", "g"}, {"ד", "d"}, {"ה", "h"}, {"ו", "v"}, {"ז", "z"},
            {"ח", "ch"}, {"ט", "t"}, {"י", "y"}, {"כ", "k"}, {"ל", "l"}, {"מ", "m"}, {"ן", "n"},
            {"נ", "n"}, {"ס", "s"}, {"ע", "a"}, {"פ", "p"}, {"צ", "tz"}, {"ק", "k"}, {"ר", "r"},
            {"ש", "sh"}, {"ת", "t"}, {"ך", "k"}, {"ם", "m"}, {"ף", "f"}, {"ץ", "tz"}
        };
        
        String result = hebrew.toLowerCase();
        for (String[] pair : mapping) {
            result = result.replace(pair[0], pair[1]);
        }
        return result;
    }

    private String getRandomDomain() {
        String[] domains = {
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "walla.co.il", "012.net.il", "example.com"
        };
        return domains[random.nextInt(domains.length)];
    }

    private String generateSecurePassword() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*(),.?\":{}|<>";

        StringBuilder password = new StringBuilder();

        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        String allChars = uppercase + lowercase + digits + special;
        for (int i = 3; i < 12; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return shuffleString(password.toString());
    }

    private String shuffleString(String string) {
        char[] chars = string.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
