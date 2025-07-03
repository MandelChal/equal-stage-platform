package com.equal_stage_platform.dev.service;

import com.equal_stage_platform.dev.fake.LectureFakerService;
import com.equal_stage_platform.dev.fake.LecturerFakerService;
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

    @Autowired
    private LecturerFakerService lecturerFakerService;

    @Autowired
    private LectureFakerService lectureFakerService;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // ========== Basic Data Creation ==========

    public void createFakeData() {
        createFakeData(25, 60); // default: 25 lecturers, 60 lectures
    }

    public void createFakeData(int lecturerCount, int lectureCount) {
        System.out.println("Starting to create fake data...");
        
        List<Lecturer> lecturers = createFakeLecturers(lecturerCount);
        System.out.println("Created " + lecturers.size() + " lecturers");
        
        List<Lecture> lectures = createFakeLectures(lectureCount);
        System.out.println("Created " + lectures.size() + " lectures");
        
        linkLecturersToLectures(lecturers, lectures);
        System.out.println("Linked lecturers to lectures");
        
        lecturerRepository.saveAll(lecturers);
        lectureRepository.saveAll(lectures);
        
        System.out.println("Fake data creation completed!");
    }

    public Map<String, Object> createSingleLecturer() {
        Map<String, Object> result = new HashMap<>();
        try {
            Lecturer lecturer = lecturerFakerService.generateFakeLecturer();
            Lecturer saved = lecturerRepository.save(lecturer);
            
            result.put("success", true);
            result.put("lecturer", saved);
            result.put("total_lecturers", lecturerRepository.count());
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
                lecturers.add(lecturerFakerService.generateFakeLecturer());
            }
            
            List<Lecturer> saved = lecturerRepository.saveAll(lecturers);
            
            result.put("success", true);
            result.put("created_count", saved.size());
            result.put("lecturers", saved);
            result.put("total_lecturers", lecturerRepository.count());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> createSingleLecture() {
        Map<String, Object> result = new HashMap<>();
        try {
            Lecture lecture = lectureFakerService.generateFakeLecture();
            
            // Add random lecturers if available
            List<Lecturer> availableLecturers = lecturerRepository.findAll();
            if (!availableLecturers.isEmpty()) {
                Set<Lecturer> lecturers = selectRandomLecturers(availableLecturers);
                lecture.setLecturers(lecturers);
            }
            
            Lecture saved = lectureRepository.save(lecture);
            
            result.put("success", true);
            result.put("lecture", saved);
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
            List<Lecture> lectures = lectureFakerService.createFakeLectures(count);
            
            result.put("success", true);
            result.put("created_count", lectures.size());
            result.put("lectures", lectures);
            result.put("total_lectures", lectureRepository.count());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    // ========== Advanced System Creation ==========

    public Map<String, Object> createCompleteSystemWithRelations(int lecturerCount, int lectureCount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Lecturer> lecturers = createSpecializedLecturers(lecturerCount);
            List<Lecture> lectures = createMatchedLecturesToLecturers(lectureCount, lecturers);
            createAdvancedManyToManyRelations(lectures, lecturers);
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

    public Map<String, Object> createIsraeliTechLectures(int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> lectures = lectureFakerService.createIsraeliTechLectures(count);
            
            result.put("success", true);
            result.put("created_count", lectures.size());
            result.put("lectures", lectures);
            result.put("total_lectures", lectureRepository.count());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> generateUpcomingLectures(int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> lectures = lectureFakerService.generateUpcomingLectures(count);
            
            result.put("success", true);
            result.put("created_count", lectures.size());
            result.put("lectures", lectures);
            result.put("total_lectures", lectureRepository.count());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> generateSingleLectureWithLecturers() {
        Map<String, Object> result = new HashMap<>();
        try {
            Lecture lecture = lectureFakerService.generateSingleLectureWithLecturers();
            
            result.put("success", true);
            result.put("lecture", lecture);
            result.put("attached_lecturers", lecture.getLecturers().size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> createNewManyToManyRelations(int newRelations) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> lecturers = lecturerRepository.findAll();
            
            if (lectures.isEmpty() || lecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "No lectures or lecturers found. Please create some first.");
                return result;
            }
            
            int relationsCreated = 0;
            
            for (int i = 0; i < newRelations; i++) {
                Lecture randomLecture = lectures.get(random.nextInt(lectures.size()));
                Lecturer randomLecturer = lecturers.get(random.nextInt(lecturers.size()));
                
                if (!randomLecture.getLecturers().contains(randomLecturer)) {
                    randomLecture.getLecturers().add(randomLecturer);
                    lectureRepository.save(randomLecture);
                    relationsCreated++;
                }
            }
            
            result.put("success", true);
            result.put("message", "Created " + relationsCreated + " new relations");
            result.put("relations_created", relationsCreated);
            result.put("total_relations_now", countAllRelations());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error creating new relations: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> createSuperLecture() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            
            if (allLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "No lecturers available");
                return result;
            }
            
            Lecture superLecture = new Lecture();
            superLecture.setTitle("כנס המומחים הגדול - כל המרצים ביחד!");
            superLecture.setDescription(
                "אירוע מיוחד ויחיד מסוגו - כל המומחים שלנו ביחד במקום אחד!\n\n" +
                "זוהי הזדמנות נדירה לשמוע מכל המרצים המובילים שלנו בנושאים מגוונים:\n" +
                "• טכנולוגיות חדשניות\n" +
                "• מגמות בתעשייה\n" +
                "• חוויות מהשטח\n" +
                "• Q&A פתוח עם כל המומחים\n\n" +
                "האירוע יכלול הרצאות קצרות, פאנלים, ונטוורקינג.\n" +
                "מתאים לכל המתעניינים בטכנולוגיה!"
            );
            superLecture.setPrice(500);
            superLecture.setIsOnline(false);
            superLecture.setLocation("תל אביב - אולם הכנסים הגדול, מרכז עזריאלי");
            superLecture.setIsAvailable(true);
            superLecture.setImageUrl("https://picsum.photos/800/400?random=2000");
            superLecture.setCreatedAt(LocalDateTime.now());
            
            LocalDateTime startTime = LocalDateTime.now().plusDays(30).withHour(9).withMinute(0);
            superLecture.setStartTime(startTime);
            superLecture.setEndTime(startTime.plusHours(8));
            
            Set<Lecturer> allLecturersSet = new HashSet<>(allLecturers);
            superLecture.setLecturers(allLecturersSet);
            
            Lecture savedLecture = lectureRepository.save(superLecture);
            
            result.put("success", true);
            result.put("message", "Super lecture created with all " + allLecturers.size() + " lecturers!");
            result.put("super_lecture", savedLecture);
            result.put("lecturers_count", allLecturers.size());
            result.put("total_relations_now", countAllRelations());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error creating super lecture: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> fixOrphanLectures() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> allLectures = lectureRepository.findAll();
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            
            if (allLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "No lecturers available to assign");
                return result;
            }
            
            List<Lecture> orphanLectures = allLectures.stream()
                .filter(lecture -> lecture.getLecturers() == null || lecture.getLecturers().isEmpty())
                .collect(Collectors.toList());
            
            int fixed = 0;
            
            for (Lecture lecture : orphanLectures) {
                Lecturer randomLecturer = allLecturers.get(random.nextInt(allLecturers.size()));
                Set<Lecturer> lecturers = new HashSet<>();
                lecturers.add(randomLecturer);
                
                if (random.nextDouble() < 0.3 && allLecturers.size() > 1) {
                    Lecturer secondLecturer;
                    do {
                        secondLecturer = allLecturers.get(random.nextInt(allLecturers.size()));
                    } while (secondLecturer.equals(randomLecturer));
                    lecturers.add(secondLecturer);
                }
                
                lecture.setLecturers(lecturers);
                lectureRepository.save(lecture);
                fixed++;
            }
            
            result.put("success", true);
            result.put("message", "Fixed " + fixed + " orphan lectures");
            result.put("lectures_fixed", fixed);
            result.put("total_relations_now", countAllRelations());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error fixing orphan lectures: " + e.getMessage());
        }
        return result;
    }

    // ========== Analysis Methods ==========

    public Map<String, Object> analyzeManyToManyRelations() {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> lecturers = lecturerRepository.findAll();
            
            Map<String, Long> lecturersPerLecture = lectures.stream()
                .collect(Collectors.groupingBy(
                    lecture -> String.valueOf(lecture.getLecturers().size()) + " מרצים",
                    Collectors.counting()
                ));
            
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

    public Map<String, Object> findLonelyLecturers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            
            List<Lecturer> lonelyLecturers = allLecturers.stream()
                .filter(lecturer -> lectures.stream()
                    .noneMatch(lecture -> lecture.getLecturers().contains(lecturer)))
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("lonely_lecturers_count", lonelyLecturers.size());
            result.put("lonely_lecturers", lonelyLecturers);
            result.put("total_lecturers", allLecturers.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error finding lonely lecturers: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> findOrphanLectures() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> allLectures = lectureRepository.findAll();
            
            List<Lecture> orphanLectures = allLectures.stream()
                .filter(lecture -> lecture.getLecturers() == null || lecture.getLecturers().isEmpty())
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("orphan_lectures_count", orphanLectures.size());
            result.put("orphan_lectures", orphanLectures);
            result.put("total_lectures", allLectures.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error finding orphan lectures: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getRelationsMatrix() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            List<Lecturer> lecturers = lecturerRepository.findAll();
            
            List<Map<String, Object>> matrix = new ArrayList<>();
            
            for (Lecturer lecturer : lecturers) {
                Map<String, Object> lecturerData = new HashMap<>();
                lecturerData.put("lecturer_id", lecturer.getUserId());
                lecturerData.put("lecturer_name", lecturer.getFirstName() + " " + lecturer.getLastName());
                lecturerData.put("lecturer_email", lecturer.getEmail());
                
                List<Map<String, Object>> lecturesData = lectures.stream()
                    .filter(lecture -> lecture.getLecturers().contains(lecturer))
                    .map(lecture -> {
                        Map<String, Object> lectureInfo = new HashMap<>();
                        lectureInfo.put("lecture_id", lecture.getLectureId());
                        lectureInfo.put("lecture_title", lecture.getTitle());
                        lectureInfo.put("lecture_price", lecture.getPrice());
                        lectureInfo.put("start_time", lecture.getStartTime());
                        lectureInfo.put("other_lecturers_count", lecture.getLecturers().size() - 1);
                        return lectureInfo;
                    })
                    .collect(Collectors.toList());
                
                lecturerData.put("lectures", lecturesData);
                lecturerData.put("lectures_count", lecturesData.size());
                matrix.add(lecturerData);
            }
            
            result.put("success", true);
            result.put("relations_matrix", matrix);
            result.put("total_lecturers", lecturers.size());
            result.put("total_lectures", lectures.size());
            result.put("total_relations", countAllRelations());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error generating relations matrix: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> cleanDuplicateRelations() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Lecture> lectures = lectureRepository.findAll();
            int cleanedRelations = 0;
            
            for (Lecture lecture : lectures) {
                Set<Lecturer> uniqueLecturers = new HashSet<>(lecture.getLecturers());
                if (uniqueLecturers.size() != lecture.getLecturers().size()) {
                    lecture.setLecturers(uniqueLecturers);
                    lectureRepository.save(lecture);
                    cleanedRelations++;
                }
            }
            
            result.put("success", true);
            result.put("message", "Cleaned duplicate relations");
            result.put("lectures_cleaned", cleanedRelations);
            result.put("total_relations_now", countAllRelations());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error cleaning duplicate relations: " + e.getMessage());
        }
        return result;
    }

    // ========== Helper Methods ==========

    private List<Lecturer> createFakeLecturers(int count) {
        List<Lecturer> lecturers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                lecturers.add(lecturerFakerService.generateFakeLecturer());
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
        if (lecturers.isEmpty() || lectures.isEmpty()) return;

        for (Lecture lecture : lectures) {
            int numLecturers = random.nextInt(3) + 1;
            Set<Lecturer> selectedLecturers = random.ints(0, lecturers.size())
                .distinct()
                .limit(numLecturers)
                .mapToObj(lecturers::get)
                .collect(Collectors.toSet());
            
            for (Lecturer lecturer : selectedLecturers) {
                lecture.addLecturer(lecturer);
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

    private int countAllRelations() {
        return lectureRepository.findAll().stream()
            .mapToInt(lecture -> lecture.getLecturers().size())
            .sum();
    }

    // Additional methods for specialized lecturers and lectures (simplified versions)
    private List<Lecturer> createSpecializedLecturers(int count) {
        return createFakeLecturers(count);
    }

    private List<Lecture> createMatchedLecturesToLecturers(int count, List<Lecturer> lecturers) {
        return createFakeLectures(count);
    }

    private void createAdvancedManyToManyRelations(List<Lecture> lectures, List<Lecturer> lecturers) {
        linkLecturersToLectures(lecturers, lectures);
    }

    private List<Lecture> createGroupLectures(int count, List<Lecturer> lecturers) {
        List<Lecture> groupLectures = new ArrayList<>();
        
        String[] groupTopics = {
            "פאנל מומחים - עתיד הטכנולוגיה",
            "דיון קבוצתי - AI בעולם העבודה", 
            "סדנה משותפת - Full Stack Development"
        };
        
        for (int i = 0; i < count && i < groupTopics.length; i++) {
            try {
                Lecture lecture = new Lecture();
                lecture.setTitle(groupTopics[i]);
                lecture.setDescription("מפגש מיוחד עם מספר מומחים מהתעשייה");
                lecture.setPrice(faker.number().numberBetween(100, 200));
                
                LocalDateTime startTime = LocalDateTime.now().plusDays(faker.number().numberBetween(7, 60));
                lecture.setStartTime(startTime);
                lecture.setEndTime(startTime.plusHours(3));
                
                lecture.setIsOnline(random.nextBoolean());
                lecture.setLocation(lecture.getIsOnline() ? "Zoom Webinar" : "תל אביב - אולם כנסים");
                lecture.setIsAvailable(true);
                lecture.setImageUrl("https://picsum.photos/800/400?random=" + faker.number().numberBetween(1000, 1999));
                lecture.setCreatedAt(LocalDateTime.now());
                
                // Add multiple lecturers
                Set<Lecturer> groupLecturers = new HashSet<>();
                int numberOfLecturers = faker.number().numberBetween(2, Math.min(5, lecturers.size() + 1));
                
                while (groupLecturers.size() < numberOfLecturers && groupLecturers.size() < lecturers.size()) {
                    groupLecturers.add(lecturers.get(random.nextInt(lecturers.size())));
                }
                
                lecture.setLecturers(groupLecturers);
                groupLectures.add(lecture);
                
            } catch (Exception e) {
                System.err.println("Error creating group lecture " + i + ": " + e.getMessage());
            }
        }
        
        return lectureRepository.saveAll(groupLectures);
    }
}