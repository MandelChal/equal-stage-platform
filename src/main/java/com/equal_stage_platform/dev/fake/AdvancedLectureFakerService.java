package com.equal_stage_platform.dev.fake;

import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvancedLectureFakerService {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    /**
     * יוצר מערכת מלאה של מרצים והרצאות עם יחסים רבים לרבים מתקדמים
     */
    @Transactional
    public Map<String, Object> createCompleteSystemWithRelations(int lecturerCount, int lectureCount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. יצור מרצים עם התמחויות ספציפיות
            List<Lecturer> lecturers = createSpecializedLecturers(lecturerCount);
            
            // 2. יצור הרצאות עם התאמות למרצים
            List<Lecture> lectures = createMatchedLecturesToLecturers(lectureCount, lecturers);
            
            // 3. יצור יחסים מתקדמים (מרצה עיקרי + מרצים משניים)
            createAdvancedManyToManyRelations(lectures, lecturers);
            
            // 4. יצור הרצאות קבוצתיות (מספר מרצים)
            List<Lecture> groupLectures = createGroupLectures(3, lecturers);
            
            result.put("success", true);
            result.put("lecturers_created", lecturers.size());
            result.put("lectures_created", lectures.size());
            result.put("group_lectures_created", groupLectures.size());
            result.put("total_relations", countAllRelations());
            result.put("lecturers", lecturers);
            result.put("lectures", lectures);
            result.put("group_lectures", groupLectures);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * יוצר מרצים עם התמחויות ספציפיות
     */
    private List<Lecturer> createSpecializedLecturers(int count) {
        List<Lecturer> lecturers = new ArrayList<>();
        
        // התמחויות טכנולוגיות
        String[][] specializations = {
            {"Frontend Development", "React, Vue.js, Angular, JavaScript ES6+", "Web Development"},
            {"Backend Development", "Node.js, Spring Boot, Python Django", "Server Side"},
            {"Data Science", "Python, R, Machine Learning, Statistics", "Data Analytics"},
            {"DevOps Engineering", "Docker, Kubernetes, CI/CD, AWS", "Infrastructure"},
            {"Mobile Development", "React Native, Flutter, iOS, Android", "Mobile Apps"},
            {"UI/UX Design", "Figma, Adobe XD, User Research, Prototyping", "Design"},
            {"Cybersecurity", "Ethical Hacking, Network Security, CISSP", "Security"},
            {"Cloud Architecture", "AWS, Azure, Google Cloud, Microservices", "Cloud Computing"},
            {"Machine Learning", "TensorFlow, PyTorch, Deep Learning, AI", "Artificial Intelligence"},
            {"Database Administration", "PostgreSQL, MongoDB, Redis, SQL Optimization", "Database Management"}
        };
        
        String[] hebrewFirstNames = {
            "אבי", "דני", "יוסי", "מיכל", "שרה", "רונית", "עמית", "נועה", 
            "תומר", "הילה", "אור", "מיה", "רועי", "טל", "שי", "ליאור", "גל", "ענת"
        };
        
        String[] hebrewLastNames = {
            "כהן", "לוי", "אברהם", "דוד", "מילר", "יוסף", "חיים", "בן דוד",
            "אשכנזי", "ספרדי", "אהרון", "יעקב", "מרדכי", "גולדברג", "רוזן", "פרידמן"
        };
        
        Set<String> usedEmails = new HashSet<>();
        Set<String> usedPhones = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            try {
                Lecturer lecturer = new Lecturer();
                
                // שם עברי או אנגלי
                if (random.nextBoolean()) {
                    lecturer.setFirstName(hebrewFirstNames[random.nextInt(hebrewFirstNames.length)]);
                    lecturer.setLastName(hebrewLastNames[random.nextInt(hebrewLastNames.length)]);
                } else {
                    lecturer.setFirstName(faker.name().firstName());
                    lecturer.setLastName(faker.name().lastName());
                }
                
                // התמחות ספציפית
                String[] specialization = specializations[i % specializations.length];
                
                // מייל יחיד עם התמחות
                String email;
                do {
                    String domain = specialization[2].toLowerCase().replace(" ", "").replace("&", "");
                    email = lecturer.getFirstName().toLowerCase() + "." + 
                           lecturer.getLastName().toLowerCase() + 
                           faker.number().numberBetween(1, 99) + "@" + domain + ".tech";
                } while (usedEmails.contains(email));
                usedEmails.add(email);
                lecturer.setEmail(email);
                
                // טלפון ישראלי יחיד
                String phone;
                do {
                    String[] prefixes = {"050", "052", "054", "058"};
                    phone = prefixes[random.nextInt(prefixes.length)] + 
                           faker.number().numberBetween(1000000, 9999999);
                } while (usedPhones.contains(phone));
                usedPhones.add(phone);
                lecturer.setPhone(phone);
                
                // עיר ישראלית
                String[] cities = {"תל אביב", "חיפה", "ירושלים", "באר שבע", "נתניה", "רמת גן"};
                lecturer.setCity(cities[random.nextInt(cities.length)]);
                
                // ביו עם התמחות
                String bio = String.format(
                    "מומחה ב%s עם %d שנות ניסיון.\n" +
                    "התמחויות: %s\n" +
                    "מעביר הרצאות וסדנאות בתחום %s ברמה הגבוהה ביותר.\n" +
                    "בעל ניסיון רב בהדרכה וחונכות של מפתחים צעירים.",
                    specialization[0],
                    faker.number().numberBetween(3, 15),
                    specialization[1],
                    specialization[2]
                );
                lecturer.setBio(bio);
                
                // תמונה
                String gender = random.nextBoolean() ? "men" : "women";
                lecturer.setImageUrl("https://randomuser.me/api/portraits/" + 
                                   gender + "/" + faker.number().numberBetween(1, 99) + ".jpg");
                
                lecturer.setCreatedAt(LocalDateTime.now());
                
                lecturers.add(lecturer);
                
            } catch (Exception e) {
                System.err.println("Error creating specialized lecturer " + i + ": " + e.getMessage());
            }
        }
        
        return lecturerRepository.saveAll(lecturers);
    }

    /**
     * יוצר הרצאות שמתאימות להתמחויות המרצים
     */
    private List<Lecture> createMatchedLecturesToLecturers(int count, List<Lecturer> lecturers) {
        List<Lecture> lectures = new ArrayList<>();
        
        // נושאים לפי התמחויות
        Map<String, List<String[]>> topicsBySpecialization = new HashMap<>();
        
        topicsBySpecialization.put("Frontend", Arrays.asList(
            new String[]{"React Hooks למתקדמים", "ניהול State ב-React עם Redux", "250"},
            new String[]{"Vue.js 3 ו-Composition API", "מבוא לVue.js החדש", "200"},
            new String[]{"Angular Advanced", "Angular עם TypeScript", "280"}
        ));
        
        topicsBySpecialization.put("Backend", Arrays.asList(
            new String[]{"Spring Boot מתקדם", "מיקרו-סרוויסים עם Spring", "300"},
            new String[]{"Node.js ו-Express", "פיתוח API מתקדם", "250"},
            new String[]{"Python Django", "פיתוח web עם Django", "220"}
        ));
        
        topicsBySpecialization.put("Data", Arrays.asList(
            new String[]{"Machine Learning עם Python", "מבוא למידת מכונה", "350"},
            new String[]{"Data Analysis עם Pandas", "ניתוח נתונים מתקדם", "280"},
            new String[]{"Deep Learning", "רשתות נוירונים", "400"}
        ));
        
        topicsBySpecialization.put("DevOps", Arrays.asList(
            new String[]{"Docker ו-Kubernetes", "קונטיינריזציה מתקדמת", "320"},
            new String[]{"CI/CD עם Jenkins", "אוטומציה מתקדמת", "290"},
            new String[]{"AWS Cloud Architecture", "תשתיות ענן", "380"}
        ));
        
        // רשימה כללית של נושאים
        List<String[]> allTopics = new ArrayList<>();
        topicsBySpecialization.values().forEach(allTopics::addAll);
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = new Lecture();
                
                // בחר נושא אקראי
                String[] topic = allTopics.get(random.nextInt(allTopics.size()));
                lecture.setTitle(topic[0]);
                
                // תיאור מפורט
                lecture.setDescription(generateDetailedDescription(topic[0], topic[1]));
                lecture.setPrice(Integer.parseInt(topic[2]));
                
                // זמנים עתידיים
                LocalDateTime startTime = generateFutureDateTime();
                lecture.setStartTime(startTime);
                lecture.setEndTime(startTime.plusHours(faker.number().numberBetween(2, 4)));
                
                // מיקום
                boolean isOnline = random.nextBoolean();
                lecture.setIsOnline(isOnline);
                lecture.setLocation(isOnline ? "הרצאה מקוונת - Zoom" : 
                                   "תל אביב - מרכז הייטק עזריאלי " + faker.number().numberBetween(1, 10));
                
                lecture.setIsAvailable(true);
                lecture.setImageUrl("https://picsum.photos/600/400?random=" + 
                                  faker.number().numberBetween(100, 999));
                lecture.setCreatedAt(LocalDateTime.now());
                
                lectures.add(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating matched lecture " + i + ": " + e.getMessage());
            }
        }
        
        return lectureRepository.saveAll(lectures);
    }

    /**
     * יוצר יחסים מתקדמים רבים לרבים עם מרצה עיקרי ומרצים משניים
     */
    @Transactional
    public void createAdvancedManyToManyRelations(List<Lecture> lectures, List<Lecturer> lecturers) {
        for (Lecture lecture : lectures) {
            try {
                Set<Lecturer> lecturersForLecture = new HashSet<>();
                
                // מרצה עיקרי - בחר לפי התאמה
                Lecturer primaryLecturer = findBestMatchLecturer(lecture, lecturers);
                lecturersForLecture.add(primaryLecturer);
                
                // מרצים משניים - 30% סיכוי להוסיף מרצה נוסף
                if (random.nextDouble() < 0.3 && lecturers.size() > 1) {
                    List<Lecturer> remainingLecturers = lecturers.stream()
                        .filter(l -> !l.equals(primaryLecturer))
                        .collect(Collectors.toList());
                    
                    if (!remainingLecturers.isEmpty()) {
                        Lecturer secondaryLecturer = remainingLecturers.get(
                            random.nextInt(remainingLecturers.size())
                        );
                        lecturersForLecture.add(secondaryLecturer);
                    }
                }
                
                // 10% סיכוי למרצה שלישי (הרצאה גדולה)
                if (random.nextDouble() < 0.1 && lecturers.size() > 2 && lecturersForLecture.size() == 2) {
                    List<Lecturer> remainingLecturers = lecturers.stream()
                        .filter(l -> !lecturersForLecture.contains(l))
                        .collect(Collectors.toList());
                    
                    if (!remainingLecturers.isEmpty()) {
                        Lecturer thirdLecturer = remainingLecturers.get(
                            random.nextInt(remainingLecturers.size())
                        );
                        lecturersForLecture.add(thirdLecturer);
                    }
                }
                
                lecture.setLecturers(lecturersForLecture);
                lectureRepository.save(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating relations for lecture " + lecture.getTitle() + ": " + e.getMessage());
            }
        }
    }

    /**
     * מוצא את המרצה הכי מתאים להרצאה לפי נושא
     */
    private Lecturer findBestMatchLecturer(Lecture lecture, List<Lecturer> lecturers) {
        String lectureTitle = lecture.getTitle().toLowerCase();
        
        // נסה למצוא התאמה לפי ביו המרצה
        for (Lecturer lecturer : lecturers) {
            String lecturerBio = lecturer.getBio().toLowerCase();
            
            if ((lectureTitle.contains("react") && lecturerBio.contains("react")) ||
                (lectureTitle.contains("python") && lecturerBio.contains("python")) ||
                (lectureTitle.contains("data") && lecturerBio.contains("data")) ||
                (lectureTitle.contains("devops") && lecturerBio.contains("devops")) ||
                (lectureTitle.contains("spring") && lecturerBio.contains("spring")) ||
                (lectureTitle.contains("docker") && lecturerBio.contains("docker"))) {
                return lecturer;
            }
        }
        
        // אם לא נמצאה התאמה ספציפית, בחר אקראי
        return lecturers.get(random.nextInt(lecturers.size()));
    }

    /**
     * יוצר הרצאות קבוצתיות עם מספר מרצים
     */
    private List<Lecture> createGroupLectures(int count, List<Lecturer> lecturers) {
        List<Lecture> groupLectures = new ArrayList<>();
        
        String[] groupTopics = {
            "פאנל מומחים - עתיד הטכנולוגיה",
            "דיון קבוצתי - AI בעולם העבודה",
            "סדנה משותפת - Full Stack Development",
            "מפגש מומחים - Cybersecurity במגזר הפרטי",
            "פאנל נשים בהייטק",
            "קונפרנס מיני - Cloud Computing"
        };
        
        for (int i = 0; i < count && i < groupTopics.length; i++) {
            try {
                Lecture lecture = new Lecture();
                lecture.setTitle(groupTopics[i]);
                lecture.setDescription(generateGroupLectureDescription(groupTopics[i]));
                lecture.setPrice(faker.number().numberBetween(100, 200)); // הרצאות קבוצתיות זולות יותר
                
                LocalDateTime startTime = generateFutureDateTime();
                lecture.setStartTime(startTime);
                lecture.setEndTime(startTime.plusHours(3)); // הרצאות קבוצתיות ארוכות יותר
                
                lecture.setIsOnline(random.nextBoolean());
                lecture.setLocation(lecture.getIsOnline() ? "Zoom Webinar" : 
                                  "תל אביב - אולם כנסים מרכז עזריאלי");
                lecture.setIsAvailable(true);
                lecture.setImageUrl("https://picsum.photos/800/400?random=" + 
                                  faker.number().numberBetween(1000, 1999));
                lecture.setCreatedAt(LocalDateTime.now());
                
                // הוסף מספר מרצים (2-4)
                Set<Lecturer> groupLecturers = new HashSet<>();
                int numberOfLecturers = faker.number().numberBetween(2, Math.min(5, lecturers.size() + 1));
                
                while (groupLecturers.size() < numberOfLecturers && groupLecturers.size() < lecturers.size()) {
                    Lecturer randomLecturer = lecturers.get(random.nextInt(lecturers.size()));
                    groupLecturers.add(randomLecturer);
                }
                
                lecture.setLecturers(groupLecturers);
                groupLectures.add(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating group lecture " + i + ": " + e.getMessage());
            }
        }
        
        return lectureRepository.saveAll(groupLectures);
    }

    /**
     * סופר את כל היחסים במערכת
     */
    private int countAllRelations() {
        List<Lecture> allLectures = lectureRepository.findAll();
        return allLectures.stream()
            .mapToInt(lecture -> lecture.getLecturers().size())
            .sum();
    }

    /**
     * מנתח יחסים רבים לרבים
     */
    public Map<String, Object> analyzeManyToManyRelations() {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> lecturers = lecturerRepository.findAll();
            
            // ניתוח התפלגות מרצים להרצאות
            Map<String, Long> lecturersPerLecture = lectures.stream()
                .collect(Collectors.groupingBy(
                    lecture -> String.valueOf(lecture.getLecturers().size()) + " מרצים",
                    Collectors.counting()
                ));
            
            // ניתוח התפלגות הרצאות למרצים
            Map<String, Long> lecturesPerLecturer = lecturers.stream()
                .collect(Collectors.groupingBy(
                    lecturer -> {
                        long lectureCount = lectures.stream()
                            .filter(lecture -> lecture.getLecturers().contains(lecturer))
                            .count();
                        return lectureCount + " הרצאות";
                    },
                    Collectors.counting()
                ));
            
            // מרצים הכי פעילים
            List<Map<String, Object>> topLecturers = lecturers.stream()
                .map(lecturer -> {
                    long lectureCount = lectures.stream()
                        .filter(lecture -> lecture.getLecturers().contains(lecturer))
                        .count();
                    Map<String, Object> lecturerInfo = new HashMap<>();
                    lecturerInfo.put("name", lecturer.getFirstName() + " " + lecturer.getLastName());
                    lecturerInfo.put("lecture_count", lectureCount);
                    lecturerInfo.put("email", lecturer.getEmail());
                    return lecturerInfo;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("lecture_count"), (Long) a.get("lecture_count")))
                .limit(5)
                .collect(Collectors.toList());
            
            analysis.put("success", true);
            analysis.put("total_lectures", lectures.size());
            analysis.put("total_lecturers", lecturers.size());
            analysis.put("total_relations", countAllRelations());
            analysis.put("lecturers_per_lecture_distribution", lecturersPerLecture);
            analysis.put("lectures_per_lecturer_distribution", lecturesPerLecturer);
            analysis.put("top_active_lecturers", topLecturers);
            
        } catch (Exception e) {
            analysis.put("success", false);
            analysis.put("message", "Error analyzing relations: " + e.getMessage());
        }
        
        return analysis;
    }

    // Helper methods
    private String generateDetailedDescription(String title, String subtitle) {
        return String.format(
            "%s - %s\n\n" +
            "בהרצאה זו נעמיק בנושא %s באופן מעשי ויישומי. " +
            "ההרצאה מיועדת למפתחים ברמות שונות ותכלול דוגמאות מעשיות, " +
            "תרגולים חיים ופתרון בעיות אמיתיות מהתעשייה.\n\n" +
            "נושאים שיכוסו:\n" +
            "• יסודות וקונספטים מרכזיים\n" +
            "• best practices ומתודולוגיות מתקדמות\n" +
            "• דוגמאות מעשיות ופרויקטים אמיתיים\n" +
            "• Q&A וטיפים מהתעשייה\n\n" +
            "מתאים למפתחים עם ידע בסיסי בתחום.",
            title, subtitle, title.toLowerCase()
        );
    }

    private String generateGroupLectureDescription(String title) {
        return String.format(
            "%s\n\n" +
            "מפגש מיוחד עם מספר מומחים מהתעשייה שיחלקו את הניסיון והידע שלהם. " +
            "האירוע יכלול הרצאות קצרות מכל מומחה, דיון פתוח, ואפשרות לשאלות מהקהל.\n\n" +
            "מבנה האירוע:\n" +
            "• הרצאות קצרות של המומחים (15-20 דקות כל אחד)\n" +
            "• דיון משותף ופאנל\n" +
            "• שאלות ותשובות מהקהל\n" +
            "• נטוורקינג וחילופי רעיונות\n\n" +
            "אירוע מתאים לכל הרמות - הזדמנות מצוינת ללמוד ממומחים ולהכיר אנשים בתחום!",
            title
        );
    }

    private LocalDateTime generateFutureDateTime() {
        int daysFromNow = faker.number().numberBetween(7, 60);
        int hour = faker.number().numberBetween(18, 21); // הרצאות בערב
        int minute = random.nextBoolean() ? 0 : 30;

        return LocalDateTime.now()
                .plusDays(daysFromNow)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
    }
}