// package com.equal_stage_platform.dev.fake;

// import java.time.LocalDateTime;
// import java.util.Locale;

// import org.springframework.stereotype.Service;

// import com.equal_stage_platform.dev.model.Lecturer;
// import com.github.javafaker.Faker;

// @Service
// public class LecturerFakerService {

//     private final Faker faker = new Faker(new Locale("en")); // שונה ל-en לייצוב
//     // הסר את AtomicLong userIdCounter - לא צריך!

//     public Lecturer generateFakeLecturer() {
//         // שם פרטי ומשפחה
//         String firstName = faker.name().firstName();
//         String lastName = faker.name().lastName();

//         // ביוגרפיה מקצועית
//         String specialty = faker.job().title();
//         int experience = faker.number().numberBetween(3, 18);
//         String bio = String.format(
//             "%s is a %s with %d years of experience in %s. Passionate about sharing knowledge and mentoring teams.",
//             firstName, specialty, experience, getRandomTechStack()
//         );

//         // עיר, מייל, טלפון
//         String city = faker.address().cityName();
        
//         // צור email יותר ייחודי
//         String email = (firstName + "." + lastName + "." + faker.random().nextInt(1000, 9999) 
//                        + "@" + faker.internet().domainName()).toLowerCase();
        
//         String phone = generateIsraeliPhone();

//         // תמונה רנדומלית
//         String imageUrl = "https://picsum.photos/400/400?random=" + faker.random().nextInt(10000);

//         // יצירת Lecturer עם Builder או Constructor - ללא setUserId!
//         Lecturer lecturer = Lecturer.builder()
//             .firstName(firstName)
//             .lastName(lastName)
//             .bio(bio)
//             .city(city)
//             .email(email)
//             .phone(phone)
//             .imageUrl(imageUrl)
//             .createdAt(LocalDateTime.now())
//             .build();

//         // ❌ אל תעשה: lecturer.setUserId() - הדטבייס יעשה זאת אוטומטית!
        
//         return lecturer;
//     }

//     private String generateIsraeliPhone() {
//         String[] prefixes = {"050", "052", "053", "054", "055", "058"};
//         String prefix = prefixes[faker.random().nextInt(prefixes.length)];
//         // צור מספר יותר ייחודי
//         String number = String.format("%07d", faker.number().numberBetween(1000000, 9999999));
//         return prefix + "-" + number;
//     }

//     private String getRandomTechStack() {
//         String[] stacks = {
//             "Java & Spring Boot", "Python & Django", "JavaScript & React",
//             "C# & .NET", "Go & Microservices", "Flutter & Dart",
//             "Node.js & Express", "Kotlin & Android", "AWS & Docker",
//             "Vue.js & TypeScript", "Ruby on Rails", "PHP & Laravel"
//         };
//         return stacks[faker.random().nextInt(stacks.length)];
//     }
// }