package com.equal_stage_platform.dev.fake;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.service.AuthService;
import com.equal_stage_platform.dev.service.JwtService;
import com.equal_stage_platform.dev.service.LecturerService;
import com.github.javafaker.Faker;

@Service
public class LecturerFakerService {

    @Autowired
    private AuthService authService;

    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private JwtService jwtService;

    private final Faker faker = new Faker(new Locale("en"));
    private final Random random = new Random();

    /**
     * Creates a fake lecturer with PENDING status using existing services
     * Lecturers need to be approved by admin before they can create lectures
     */
    public ResponseLecturerDTO generateFakeLecturer() {
        return generateFakeLecturer(LecturerStatus.PENDING);
    }

    /**
     * Creates a fake lecturer with specified status using existing services
     * 
     * @param status The desired lecturer status
     */
    public ResponseLecturerDTO generateFakeLecturer(LecturerStatus status) {
        try {
            // Step 1: Create user through AuthService
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = generateUniqueEmail(firstName, lastName);
            String password = generateSecurePassword();

            authService.register(email, password);

            // Step 2: Login to get user details
            Map<String, String> loginResult = authService.login(email, password);
            // Extract userId from token or use another method to get userId
            UUID userId = extractUserIdFromLogin(email, password);

            // Step 3: Create lecturer profile through LecturerService
            CreateLecturerDTO lecturerData = createLecturerData(userId, firstName, lastName, email);
            ResponseLecturerDTO lecturer = lecturerService.createLecturer(lecturerData);

            // Step 4: Update status if needed (only if not PENDING)
            if (status != LecturerStatus.PENDING) {
                lecturer = lecturerService.updateLecturerStatus(userId, status, true); // isAdmin = true

                // If approved, change role to LECTURER
                if (status == LecturerStatus.APPROVED) {
                    authService.changeRole(userId, Role.LECTURER);
                }
            }

            System.out.println("Created lecturer: " + firstName + " " + lastName + " with status: " + status);

            return lecturer;

        } catch (Exception e) {
            System.err.println("Error creating lecturer: " + e.getMessage());
            throw new RuntimeException("Failed to create fake lecturer", e);
        }
    }

    /**
     * Creates a fake lecturer that is pre-approved (for admin use)
     * This bypasses the normal approval flow
     */
    public ResponseLecturerDTO generateApprovedFakeLecturer() {
        return generateFakeLecturer(LecturerStatus.APPROVED);
    }

    /**
     * Creates a fake lecturer in FREEZE status
     * These lecturers are not visible in searches
     */
    public ResponseLecturerDTO generateFrozenFakeLecturer() {
        return generateFakeLecturer(LecturerStatus.FREEZE);
    }

    /**
     * Creates a fake lecturer with Israeli-specific details
     */
    public ResponseLecturerDTO generateIsraeliFakeLecturer() {
        return generateIsraeliFakeLecturer(LecturerStatus.PENDING);
    }

    /**
     * Creates a fake lecturer with Israeli-specific details and specified status
     */
    public ResponseLecturerDTO generateIsraeliFakeLecturer(LecturerStatus status) {
        try {
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
            String email = generateUniqueEmail(firstName, lastName);
            String password = generateSecurePassword();

            // Step 1: Create user through AuthService
            authService.register(email, password);
            UUID userId = extractUserIdFromLogin(email, password);

            // Step 2: Create lecturer profile through LecturerService
            CreateLecturerDTO lecturerData = createIsraeliLecturerData(userId, firstName, lastName, email);
            ResponseLecturerDTO lecturer = lecturerService.createLecturer(lecturerData);

            // Step 3: Update status if needed
            if (status != LecturerStatus.PENDING) {
                lecturer = lecturerService.updateLecturerStatus(userId, status, true);

                if (status == LecturerStatus.APPROVED) {
                    authService.changeRole(userId, Role.LECTURER);
                }
            }

            System.out.println(
                    "🇮🇱 Created Israeli lecturer: " + firstName + " " + lastName + " with status: " + status);

            return lecturer;

        } catch (Exception e) {
            System.err.println("Error creating Israeli lecturer: " + e.getMessage());
            throw new RuntimeException("Failed to create Israeli fake lecturer", e);
        }
    }

    /**
     * Creates multiple fake lecturers with different statuses using existing
     * services
     */
    public List<ResponseLecturerDTO> generateMultipleLecturers(int count, LecturerStatus status) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateFakeLecturer(status))
                .collect(Collectors.toList());
    }

    /**
     * Creates mixed status lecturers (for testing) using existing services
     */
    public List<ResponseLecturerDTO> generateMixedStatusLecturers(int totalCount) {
        List<ResponseLecturerDTO> lecturers = new java.util.ArrayList<>();

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

        System.out.println("Created " + totalCount + " lecturers: " +
                pendingCount + " pending, " + approvedCount + " approved, " + frozenCount + " frozen");

        return lecturers;
    }

    /**
     * Creates a lecturer with specific details (for testing) using existing
     * services
     */
    public ResponseLecturerDTO createLecturerWithDetails(String firstName, String lastName,
            String email, LecturerStatus status) {
        try {
            String password = generateSecurePassword();

            // Step 1: Create user through AuthService
            authService.register(email, password);
            UUID userId = extractUserIdFromLogin(email, password);

            // Step 2: Create lecturer profile through LecturerService
            CreateLecturerDTO lecturerData = createLecturerData(userId, firstName, lastName, email);
            ResponseLecturerDTO lecturer = lecturerService.createLecturer(lecturerData);

            // Step 3: Update status if needed
            if (status != LecturerStatus.PENDING) {
                lecturer = lecturerService.updateLecturerStatus(userId, status, true);

                if (status == LecturerStatus.APPROVED) {
                    authService.changeRole(userId, Role.LECTURER);
                }
            }

            System.out.println("Created specific lecturer: " + firstName + " " + lastName + " (" + email
                    + ") with status: " + status);

            return lecturer;

        } catch (Exception e) {
            System.err.println("Error creating specific lecturer: " + e.getMessage());
            throw new RuntimeException("Failed to create specific lecturer", e);
        }
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
    public void logStatusDistribution(List<ResponseLecturerDTO> lecturers) {
        Map<LecturerStatus, Long> statusCount = lecturers.stream()
                .collect(Collectors.groupingBy(
                        ResponseLecturerDTO::getStatus,
                        Collectors.counting()));

        System.out.println("Lecturer Status Distribution:");
        statusCount.forEach((status, count) -> System.out.println("   " + status + ": " + count + " lecturers"));
    }

    // Helper methods

    private CreateLecturerDTO createLecturerData(UUID userId, String firstName, String lastName, String email) {
        String specialty = faker.job().title();
        int experience = faker.number().numberBetween(3, 18);
        String bio = String.format(
                "%s is a %s with %d years of experience in %s. Passionate about sharing knowledge and mentoring teams.",
                firstName, specialty, experience, getRandomTechStack());

        String city = faker.address().cityName();
        String phone = generateIsraeliPhone();
        String imageUrl = "https://thispersondoesnotexist.com";

        return new CreateLecturerDTO(
                userId,
                firstName,
                lastName,
                bio,
                city,
                email,
                phone,
                imageUrl,
                getRandomWorkingArea());
    }

    private CreateLecturerDTO createIsraeliLecturerData(UUID userId, String firstName, String lastName, String email) {
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
                firstName, specialty, experience, getRandomTechStack());

        String city = israeliCities[random.nextInt(israeliCities.length)];
        String phone = generateIsraeliPhone();
        String imageUrl = "https://thispersondoesnotexist.com";

        return new CreateLecturerDTO(
                userId,
                firstName,
                lastName,
                bio,
                city,
                email,
                phone,
                imageUrl,
                getRandomWorkingArea());
    }

    private String generateUniqueEmail(String firstName, String lastName) {
        String baseEmail = (firstName + "." + lastName + "." + System.currentTimeMillis()).toLowerCase();
        String domain = getRandomIsraeliDomain();
        return baseEmail + "@" + domain;
    }

    private String generateSecurePassword() {
        // Generate password that meets system requirements (12+ chars, uppercase,
        // lowercase, special)
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*(),.?\":{}|<>";

        StringBuilder password = new StringBuilder();

        // Ensure at least one of each required character type
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill to 12 characters
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

    private UUID extractUserIdFromLogin(String email, String password) {
        try {
            Map<String, String> loginResult = authService.login(email, password);
            String token = loginResult.get("token");
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract user ID after login", e);
        }
    }

    private String generateIsraeliPhone() {
        String[] prefixes = { "050", "052", "053", "054", "055", "058" };
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
}