package com.equal_stage_platform.dev.controller;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.equal_stage_platform.dev.config.fakerHebNames;
import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.LoginRequest;
import com.equal_stage_platform.dev.dto.RegisterRequest;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.dto.fakerUserDTO;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;

@RestController
@RequestMapping("/faker")
@RequiredArgsConstructor
public class FakerController {
    private final AuthController authController;
    private final LecturerController lecturerController;
    private final LectureController lectureController;

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String SPECIAL = "!@#$%^&*(),.?\":{}|<>";
    private static final String ALL = UPPER + LOWER + SPECIAL;

    @PostMapping("/register")
    public ResponseEntity<?> register(){
        String firstName = generateHebFirstName();
        String lastName = generateHebLastName();
        String email = generateEmail(firstName, lastName);
        String password = generatePassword();
        return authController.register(new RegisterRequest(email, password));
    }

    private ResponseEntity<?> register(String email, String password) {
        return authController.register(new RegisterRequest(email, password));
    }

    private ResponseEntity<?> login(String email, String password) {
        return authController.login(new LoginRequest(email, password));
    }

    private ResponseEntity<?> logout(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        return authController.logout(token, body);
    }

    @PostMapping("/create-lecturer")
    public ResponseEntity<?> createLecturer(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        String city = generateCity();
        CreateLecturerDTO lecturerData = new CreateLecturerDTO(
                body.get("firstName"),
                body.get("lastName"),
                generateBio(body.get("firstName"), body.get("lastName")),
                city, 
                body.get("email"),
                generatePhone(),
                generateImageUrl(),
                getAreaByCity(city));
        return lecturerController.createLecturer(token, lecturerData);
    }

    @PostMapping("/create-lecture")
    public ResponseEntity<?> createLecture(@RequestHeader("Authorization") String token) {
        String title = generateTitle();
        CreateLectureDTO lectureData = new CreateLectureDTO(
                title,
                generateDescription(title),
                generateDuration(),
                generatePrice(),
                LectureStatus.ON_AIR,
                generateOnline(),
                generateImageUrlLecture(title));
        return lectureController.createLecture(token, lectureData);
    }

    private ResponseEntity<?> approveLecturer(UUID userId) {
        return lecturerController.approveLecturer(userId);
    }

    @PostMapping("/initSystem")
    public ResponseEntity<?> initSystem(@RequestBody Map<String, String> body) {
        int lecturersCount = Integer.parseInt(body.getOrDefault("lecturersCount", "10"));
        int lecturesPerLecturer = Integer.parseInt(body.getOrDefault("lecturesPerLecturer", "3"));

        Set<fakerUserDTO> users = new HashSet<>();

        for(int i = 0; i < lecturersCount; i++) {
            String firstName = generateHebFirstName();
            String lastName = generateHebLastName();
            String email = generateEmail(firstName, lastName);
            String password = generatePassword();

            register(email, password);

            ResponseEntity<?> loginResponse = login(email, password);
            String token = loginResponse.getBody() instanceof Map ? ((Map<String, String>) loginResponse.getBody()).get("token") : null;
            String refreshToken = loginResponse.getBody() instanceof Map ? ((Map<String, String>) loginResponse.getBody()).get("refresh") : null;
            if (token == null) {
                continue; // Skip to the next iteration if login fails
            }

            if (i==0){
                // Register the first lecturer as an admin
                ResponseEntity<?> registerAdminResponse = authController.setupFirstAdmin(token);
                if (!registerAdminResponse.getStatusCode().is2xxSuccessful()) {
                    continue; // Skip to the next iteration if admin registration fails
                }
            }

            ResponseEntity<?> ans = createLecturer(token, Map.of(
                                                                "firstName", firstName,
                                                                "lastName", lastName,
                                                                "email", email
                                                            ));
            if (!ans.getStatusCode().is2xxSuccessful()) {
                continue; // Skip to the next iteration if lecturer creation fails
            }
            UUID userId = ans.getBody() instanceof ResponseLecturerDTO ? ((ResponseLecturerDTO) ans.getBody()).getUserId() : null;
            if (userId == null) {
                continue; // Skip to the next iteration if userId is null
            }

            ResponseEntity<?> approveResponse = approveLecturer(userId);
            
            if (!approveResponse.getStatusCode().is2xxSuccessful()) {
                continue; // Skip to the next iteration if lecturer approval fails
            }

            // Create lectures for the lecturer
            for(int j = 0; j < lecturesPerLecturer; j++) {
                createLecture(token);
            }

            logout(token, Map.of("refresh", refreshToken));
            
            users.add(new fakerUserDTO(firstName, lastName, email, password, userId));
        }
        return ResponseEntity.ok(users);
    }
        
    // == private methods ==
    private String generatePassword() {
        int deaultLength = 12; // Default password length
        ArrayList<Character> passwordArray = new ArrayList<>();
        // Ensure at least one character from each category is included
        passwordArray.add(UPPER.charAt((int) (Math.random() * UPPER.length())));
        passwordArray.add(LOWER.charAt((int) (Math.random() * LOWER.length())));
        passwordArray.add(SPECIAL.charAt((int) (Math.random() * SPECIAL.length())));
        // Fill the rest of the password with random characters from all categories
        for (int i = 3; i < deaultLength; i++) {
            passwordArray.add(ALL.charAt((int) (Math.random() * ALL.length())));
        }

        // Shuffle the password to ensure randomness
        java.util.Collections.shuffle(passwordArray);

        // Convert the ArrayList to a String
        StringBuilder password = new StringBuilder();
        for (Character c : passwordArray) {
            password.append(c);
        }

        return password.toString();
    }
    private String generateEmail(String firstName, String lastName) {
        return fakerHebNames.heb2eng_firstName(firstName).toLowerCase() +
                fakerHebNames.heb2eng_lastName(lastName) + "@gmail.com";
    }
    private String generateHebFirstName() {
        return fakerHebNames.randomHebrewFirstName();
    }
    private String generateHebLastName() {
        return fakerHebNames.randomHebrewLastName();
    }
    private String generateBio(String firstName, String lastName) {
        return "שלום! שמי " + firstName + " " + lastName + ". אני מרצה בפלטפורמת Equal Stage.";
    }
    private String generateCity() {
        return fakerHebNames.randomCity();
    }
    private String generatePhone() {
        return "05" + (int) (Math.random() * 10000000);
    }
    private String generateImageUrl() {
        return "https://example.com/images/" + generateHebFirstName().toLowerCase() + "_" + generateHebLastName().toLowerCase() + ".jpg";
    }
    private Area getAreaByCity(String city) {
        return fakerHebNames.city2area(city);
    }
    private String generateTitle() {
        return fakerHebNames.randomTopic();
    }
    private String generateDescription(String title) {
        return "ההרצאה " + title + " עוסקת בנושאים מרתקים ומעניינים בתחום הידע שלי. בהרצאה זו, אשתף אתכם בתובנות ובממצאים שצברתי במהלך השנים.";
    }
    private Integer generateDuration() {
        return (int) (Math.random() * 120 + 30); // Random duration between 30 and 150 minutes
    }
    private Integer generatePrice() {
        return (int) (Math.random() * 100 + 50); // Random price between 50 and 150
    }
    private boolean generateOnline() {
        return Math.random() < 0.3; // 30% chance of being online
    }
    private String generateImageUrlLecture(String title) {
        return "https://example.com/images/lecture_" + title + ".jpg";
    }
        
}