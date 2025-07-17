package com.equal_stage_platform.dev.fake;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.github.javafaker.Faker;

@Service
public class LecturerFakerService {

    private final Faker faker = new Faker(new Locale("en")); 
    private final Random random = new Random();

    /**
     * Creates a fake lecturer with PENDING status (following the flow requirement)
     * Lecturers need to be approved by admin before they can create lectures
     */
    public Lecturer generateFakeLecturer() {
        return generateFakeLecturer(LecturerStatus.PENDING);
    }

    /**
     * Creates a fake lecturer with specified status
     * @param status The desired lecturer status
     */
    public Lecturer generateFakeLecturer(LecturerStatus status) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        String specialty = faker.job().title();
        int experience = faker.number().numberBetween(3, 18);
        String bio = String.format(
            "%s is a %s with %d years of experience in %s. Passionate about sharing knowledge and mentoring teams.",
            firstName, specialty, experience, getRandomTechStack()
        );

        String city = faker.address().cityName();
        String email = (firstName + "." + lastName + "." + System.currentTimeMillis() 
                       + "@" + faker.internet().domainName()).toLowerCase();
        String phone = generateIsraeliPhone();
        String imageUrl = "https://picsum.photos/400/400?random=" + faker.random().nextInt(10000);

        Lecturer lecturer = Lecturer.builder()
            .userId(UUID.randomUUID())
            .firstName(firstName)
            .lastName(lastName)
            .bio(bio)
            .city(city)
            .email(email)
            .phone(phone)
            .imageUrl(imageUrl)
            .workingArea(getRandomWorkingArea())
            .status(status) // Use the provided status
            .createdAt(LocalDateTime.now())
            .lastUpdatedAt(LocalDateTime.now())
            .build();
        
        System.out.println("👨‍🏫 Created lecturer: " + firstName + " " + lastName + " with status: " + status);
        
        return lecturer;
    }

    /**
     * Creates a fake lecturer that is pre-approved (for admin use)
     * This bypasses the normal approval flow
     */
    public Lecturer generateApprovedFakeLecturer() {
        return generateFakeLecturer(LecturerStatus.APPROVED);
    }

    /**
     * Creates a fake lecturer in FREEZE status
     * These lecturers are not visible in searches
     */
    public Lecturer generateFrozenFakeLecturer() {
        return generateFakeLecturer(LecturerStatus.FREEZE);
    }

    /**
     * Creates a fake lecturer with Israeli-specific details
     */
    public Lecturer generateIsraeliFakeLecturer() {
        return generateIsraeliFakeLecturer(LecturerStatus.PENDING);
    }

    /**
     * Creates a fake lecturer with Israeli-specific details and specified status
     */
    public Lecturer generateIsraeliFakeLecturer(LecturerStatus status) {
        String[] israeliFirstNames = {
            "David", "Michael", "Daniel", "Amit", "Yossi", "Eyal", "Avi", "Roni", "Tal", "Idan",
            "Sarah", "Rachel", "Miriam", "Tamar", "Noa", "Maya", "Hila", "Yael", "Shira", "Liat"
        };
        
        String[] israeliLastNames = {
            "Cohen", "Levy", "Miller", "Mizrahi", "Peretz", "Biton", "Dahan", "Avraham", 
            "Friedman", "Malka", "Azulay", "Katz", "Yaakov", "David", "Moshe", "Goldberg"
        };

        String firstName = israeliFirstNames[random.nextInt(israeliFirstNames.length)];
        String lastName = israeliLastNames[random.nextInt(israeliLastNames.length)];

        String[] israeliCities = {
            "Tel Aviv", "Jerusalem", "Haifa", "Beer Sheva", "Netanya", "Ashdod", 
            "Petah Tikva", "Ramat Gan", "Rishon LeZion", "Herzliya"
        };

        String[] techSpecialties = {
            "Full Stack Developer", "Backend Engineer", "Frontend Developer", 
            "DevOps Engineer", "Data Scientist", "Cybersecurity Expert",
            "Mobile Developer", "AI/ML Engineer", "Cloud Architect", "Product Manager"
        };

        String specialty = techSpecialties[random.nextInt(techSpecialties.length)];
        int experience = faker.number().numberBetween(2, 20);
        
        String bio = String.format(
            "%s is a %s with %d years of experience in the Israeli tech industry. " +
            "Specialized in %s and passionate about mentoring the next generation of developers.",
            firstName, specialty, experience, getRandomTechStack()
        );

        String city = israeliCities[random.nextInt(israeliCities.length)];
        String email = (firstName + "." + lastName + "." + System.currentTimeMillis() 
                       + "@" + getRandomIsraeliDomain()).toLowerCase();
        String phone = generateIsraeliPhone();
        String imageUrl = "https://picsum.photos/400/400?random=" + faker.random().nextInt(10000);

        Lecturer lecturer = Lecturer.builder()
            .userId(UUID.randomUUID())
            .firstName(firstName)
            .lastName(lastName)
            .bio(bio)
            .city(city)
            .email(email)
            .phone(phone)
            .imageUrl(imageUrl)
            .workingArea(getRandomWorkingArea())
            .status(status)
            .createdAt(LocalDateTime.now())
            .lastUpdatedAt(LocalDateTime.now())
            .build();
        
        System.out.println("🇮🇱 Created Israeli lecturer: " + firstName + " " + lastName + " with status: " + status);
        
        return lecturer;
    }

    /**
     * Creates multiple fake lecturers with different statuses
     */
    public List<Lecturer> generateMultipleLecturers(int count, LecturerStatus status) {
        return IntStream.range(0, count)
            .mapToObj(i -> generateFakeLecturer(status))
            .collect(Collectors.toList());
    }

    /**
     * Creates mixed status lecturers (for testing)
     */
    public List<Lecturer> generateMixedStatusLecturers(int totalCount) {
        List<Lecturer> lecturers = new java.util.ArrayList<>();
        
        // 60% pending, 30% approved, 10% frozen
        int pendingCount = (int) (totalCount * 0.6);
        int approvedCount = (int) (totalCount * 0.3);
        int frozenCount = totalCount - pendingCount - approvedCount;
        
        for (int i = 0; i < pendingCount; i++) {
            lecturers.add(generateFakeLecturer(LecturerStatus.PENDING));
        }
        
        for (int i = 0; i < approvedCount; i++) {
            lecturers.add(generateFakeLecturer(LecturerStatus.APPROVED));
        }
        
        for (int i = 0; i < frozenCount; i++) {
            lecturers.add(generateFakeLecturer(LecturerStatus.FREEZE));
        }
        
        // Shuffle the list
        Collections.shuffle(lecturers);
        
        System.out.println("👥 Created " + totalCount + " lecturers: " + 
                          pendingCount + " pending, " + approvedCount + " approved, " + frozenCount + " frozen");
        
        return lecturers;
    }

    private String generateIsraeliPhone() {
        String[] prefixes = {"050", "052", "053", "054", "055", "058"};
        String prefix = prefixes[faker.random().nextInt(prefixes.length)];
        long timestamp = System.currentTimeMillis() % 10000000;
        return prefix + String.format("%07d", timestamp);
    }

    private String getRandomIsraeliDomain() {
        String[] domains = {
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "walla.co.il", "012.net.il", "bezeqint.net", "example.co.il"
        };
        return domains[random.nextInt(domains.length)];
    }

    private String getRandomTechStack() {
        String[] stacks = {
            "Java & Spring Boot", "Python & Django", "JavaScript & React",
            "C# & .NET", "Go & Microservices", "Flutter & Dart",
            "Node.js & Express", "Kotlin & Android", "AWS & Docker",
            "Vue.js & TypeScript", "Ruby on Rails", "PHP & Laravel",
            "Machine Learning & AI", "DevOps & CI/CD", "Cybersecurity",
            "Data Science & Analytics", "Mobile Development", "Cloud Computing",
            "Angular & TypeScript", "React Native", "Kubernetes & Docker",
            "MongoDB & NoSQL", "PostgreSQL & SQL", "Redis & Caching",
            "Apache Kafka", "Elasticsearch", "GraphQL & APIs"
        };
        return stacks[faker.random().nextInt(stacks.length)];
    }

    private Area getRandomWorkingArea() {
        Area[] areas = Area.values();
        return areas[faker.random().nextInt(areas.length)];
    }

    /**
     * Creates a lecturer with specific details (for testing)
     */
    public Lecturer createLecturerWithDetails(String firstName, String lastName, 
                                            String email, LecturerStatus status) {
        String specialty = faker.job().title();
        int experience = faker.number().numberBetween(3, 18);
        String bio = String.format(
            "%s is a %s with %d years of experience. Created for specific testing purposes.",
            firstName, specialty, experience
        );

        String city = faker.address().cityName();
        String phone = generateIsraeliPhone();
        String imageUrl = "https://picsum.photos/400/400?random=" + faker.random().nextInt(10000);

        Lecturer lecturer = Lecturer.builder()
            .userId(UUID.randomUUID())
            .firstName(firstName)
            .lastName(lastName)
            .bio(bio)
            .city(city)
            .email(email)
            .phone(phone)
            .imageUrl(imageUrl)
            .workingArea(getRandomWorkingArea())
            .status(status)
            .createdAt(LocalDateTime.now())
            .lastUpdatedAt(LocalDateTime.now())
            .build();
        
        System.out.println("🎯 Created specific lecturer: " + firstName + " " + lastName + " (" + email + ") with status: " + status);
        
        return lecturer;
    }

    /**
     * Validates lecturer data before creation
     */
    public boolean isValidLecturerData(String firstName, String lastName, String email) {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() && 
               email.contains("@") && email.contains(".");
    }

    /**
     * Gets status distribution for logging
     */
    public void logStatusDistribution(List<Lecturer> lecturers) {
        Map<LecturerStatus, Long> statusCount = lecturers.stream()
            .collect(Collectors.groupingBy(
                Lecturer::getStatus,
                Collectors.counting()
            ));
        
        System.out.println("📊 Lecturer Status Distribution:");
        statusCount.forEach((status, count) -> 
            System.out.println("   " + status + ": " + count + " lecturers"));
    }
}