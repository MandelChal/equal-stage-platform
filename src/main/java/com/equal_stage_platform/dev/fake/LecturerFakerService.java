package com.equal_stage_platform.dev.fake;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.github.javafaker.Faker;

@Service
public class LecturerFakerService {

    private final Faker faker = new Faker(new Locale("en")); 
    

    public Lecturer generateFakeLecturer() {
        // שם פרטי ומשפחה
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        // ביוגרפיה מקצועית
        String specialty = faker.job().title();
        int experience = faker.number().numberBetween(3, 18);
        String bio = String.format(
            "%s is a %s with %d years of experience in %s. Passionate about sharing knowledge and mentoring teams.",
            firstName, specialty, experience, getRandomTechStack()
        );

        String city = faker.address().cityName();
        
        // Email with timestamp to ensure uniqueness
        String email = (firstName + "." + lastName + "." + System.currentTimeMillis() 
                       + "@" + faker.internet().domainName()).toLowerCase();
        
        // Phone with timestamp to ensure uniqueness
        String phone = generateIsraeliPhone();
        String imageUrl = "https://picsum.photos/400/400?random=" + faker.random().nextInt(10000);
        
        // ✅ כל השדות החובה מוגדרים כראוי
        Lecturer lecturer = Lecturer.builder()
            .userId(UUID.randomUUID())               // ✅ UUID חובה
            .firstName(firstName)                    // ✅ 
            .lastName(lastName)                      // ✅ 
            .bio(bio)                               // ✅ 
            .city(city)                             // ✅ 
            .email(email)                           // ✅ unique
            .phone(phone)                           // ✅ unique
            .imageUrl(imageUrl)                     // ✅ 
            .workingArea(getRandomWorkingArea())    // ✅ Area enum מתוקן
            .status(LecturerStatus.APPROVED)        // ✅ Status חובה
            .createdAt(LocalDateTime.now())         // ✅ 
            .lastUpdatedAt(LocalDateTime.now())     // ✅ 
            .build();
        
        return lecturer;
    }

    private String generateIsraeliPhone() {
        String[] prefixes = {"050", "052", "053", "054", "055", "058"};
        String prefix = prefixes[faker.random().nextInt(prefixes.length)];
        // Add timestamp to ensure uniqueness
        long timestamp = System.currentTimeMillis() % 10000000; // Last 7 digits
        return prefix + String.format("%07d", timestamp);
    }

    private String getRandomTechStack() {
        String[] stacks = {
            "Java & Spring Boot", "Python & Django", "JavaScript & React",
            "C# & .NET", "Go & Microservices", "Flutter & Dart",
            "Node.js & Express", "Kotlin & Android", "AWS & Docker",
            "Vue.js & TypeScript", "Ruby on Rails", "PHP & Laravel",
            "Machine Learning & AI", "DevOps & CI/CD", "Cybersecurity",
            "Data Science & Analytics", "Mobile Development", "Cloud Computing"
        };
        return stacks[faker.random().nextInt(stacks.length)];
    }

    // ✅ פונקציה מתוקנת לבחירת תחום עבודה - מתאים לmסד הנתונים
    private Area getRandomWorkingArea() {
        Area[] areas = Area.values(); // CENTER, NORTH, SOUTH, ONLINE_ONLY
        return areas[faker.random().nextInt(areas.length)];
    }
}