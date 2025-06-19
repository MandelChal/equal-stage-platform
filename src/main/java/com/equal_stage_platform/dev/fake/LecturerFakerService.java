package com.equal_stage_platform.dev.fake;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.model.Lecturer;
import com.github.javafaker.Faker;


@Service
public class LecturerFakerService {

    private final Faker faker = new Faker(new Locale("he")); // אפשר לשים he אבל לא יציב
    private final AtomicLong userIdCounter = new AtomicLong(1); // ליצור userId ידני

    public Lecturer generateFakeLecturer() {
        // שם פרטי ומשפחה
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        // ביוגרפיה מקצועית
        String specialty = faker.job().title(); // למשל: "Backend Developer"
        int experience = faker.number().numberBetween(3, 18);
        String bio = String.format(
            "%s is a %s with %d years of experience in %s. Passionate about sharing knowledge and mentoring teams.",
            firstName, specialty, experience, getRandomTechStack()
        );

        // עיר, מייל, טלפון
        String city = faker.address().cityName();
        String email = (firstName + "." + lastName + "@" + faker.internet().domainName()).toLowerCase();
        String phone = generateIsraeliPhone();

        // תמונה רנדומלית
        String imageUrl = "https://picsum.photos/400/400?random=" + faker.random().nextInt(1000);

        // יצירת Lecturer
        Lecturer lecturer = new Lecturer(
            firstName,
            lastName,
            bio,
            city,
            email,
            phone,
            imageUrl
        );

        lecturer.setUserId(userIdCounter.getAndIncrement()); // חשוב אם אין לך AUTO_INCREMENT
        // lecturer.setCreatedAt(LocalDateTime.now());

        return lecturer;
    }

    private String generateIsraeliPhone() {
        String[] prefixes = {"050", "052", "053", "054", "055", "058"};
        String prefix = prefixes[faker.random().nextInt(prefixes.length)];
        String number = String.format("%07d", faker.number().numberBetween(1000000, 9999999));
        return prefix + "-" + number;
    }

    private String getRandomTechStack() {
        String[] stacks = {
            "Java & Spring Boot", "Python & Django", "JavaScript & React",
            "C# & .NET", "Go & Microservices", "Flutter & Dart",
            "Node.js & Express", "Kotlin & Android", "AWS & Docker"
        };
        return stacks[faker.random().nextInt(stacks.length)];
    }
}
