package com.equal_stage_platform.dev.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.Set;
import java.util.Optional;
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
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;
import com.github.javafaker.Faker;

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
        createFakeData(25, 60);
    }

    public void createFakeData(int lecturerCount, int lectureCount) {
        List<Lecturer> lecturers = createFakeLecturers(lecturerCount);
        List<Lecture> lectures = createFakeLectures(lectureCount);
        linkLecturersToLectures(lecturers, lectures);
        
        lecturerRepository.saveAll(lecturers);
        lectureRepository.saveAll(lectures);
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
                
                // Update lecturers side of relationship
                for (Lecturer lecturer : lecturers) {
                    lecturer.enrollLecture(lecture);
                }
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

    // ========== NEW: Enhanced Lecture Creation with Existing Lecturers ==========

    /**
     * יוצר הרצאה חדשה עם מרצה ספציפי קיים
     */
    public Map<String, Object> createLectureWithSpecificLecturer(Lecturer lecturer) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // יצירת הרצאה חדשה
            Lecture lecture = lectureFakerService.generateFakeLecture();
            
            // התאמת הנושא לתחום המרצה (אם יש מידע על תחום)
            String expertise = getExpertiseFromLecturer(lecturer);
            if (expertise != null && !expertise.isEmpty()) {
                lecture.setTitle(generateTitleForExpertise(expertise) + " - מאת " + 
                    lecturer.getFirstName() + " " + lecturer.getLastName());
            }
            
            // שמירת ההרצאה
            lecture = lectureRepository.save(lecture);
            
            // יצירת הקשר בין ההרצאה למרצה
            lecture.getLecturers().add(lecturer);
            lecturer.enrollLecture(lecture);
            
            // עדכון בבסיס הנתונים
            lectureRepository.save(lecture);
            lecturerRepository.save(lecturer);
            
            result.put("success", true);
            result.put("lecture", lecture);
            result.put("lecturer", lecturer);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * יוצר מספר הרצאות עם מרצים קיימים
     */
    public Map<String, Object> createMultipleLecturesWithExistingLecturers(int count, List<Lecturer> availableLecturers) {
        Map<String, Object> result = new HashMap<>();
        List<Lecture> createdLectures = new ArrayList<>();
        
        try {
            Random random = new Random();
            
            for (int i = 0; i < count; i++) {
                // בחירת מרצה אקראי מהרשימה
                Lecturer selectedLecturer = availableLecturers.get(random.nextInt(availableLecturers.size()));
                
                // יצירת הרצאה חדשה
                Lecture lecture = lectureFakerService.generateFakeLecture();
                
                // התאמת הנושא לתחום המרצה
                String expertise = getExpertiseFromLecturer(selectedLecturer);
                if (expertise != null && !expertise.isEmpty()) {
                    lecture.setTitle(generateTitleForExpertise(expertise) + 
                        " - חלק " + (i + 1));
                }
                
                // שמירת ההרצאה
                lecture = lectureRepository.save(lecture);
                
                // יצירת הקשר
                lecture.getLecturers().add(selectedLecturer);
                selectedLecturer.enrollLecture(lecture);
                
                // עדכון
                lectureRepository.save(lecture);
                lecturerRepository.save(selectedLecturer);
                
                createdLectures.add(lecture);
            }
            
            result.put("success", true);
            result.put("lectures", createdLectures);
            result.put("count", createdLectures.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * יוצר סדרת הרצאות עבור מרצה ספציפי
     */
    public Map<String, Object> createLectureSeriesForLecturer(Lecturer lecturer, int seriesCount, boolean isWorkshopSeries) {
        Map<String, Object> result = new HashMap<>();
        List<Lecture> createdLectures = new ArrayList<>();
        
        try {
            String seriesTheme = isWorkshopSeries ? "סדנת" : "קורס";
            String expertise = getExpertiseFromLecturer(lecturer);
            String baseTitle = generateTitleForExpertise(expertise);
            
            LocalDateTime startDate = LocalDateTime.now().plusDays(7); // התחלה בעוד שבוע
            
            for (int i = 0; i < seriesCount; i++) {
                Lecture lecture = lectureFakerService.generateFakeLecture();
                
                // הגדרת כותרת סדרתית
                lecture.setTitle(seriesTheme + " " + baseTitle + " - שיעור " + (i + 1) + " מתוך " + seriesCount);
                
                // הגדרת תאריכים סדרתיים (כל שבוע)
                lecture.setCreatedAt(startDate.plusWeeks(i));
                lecture.setUpdatedAt(startDate.plusWeeks(i).plusHours(isWorkshopSeries ? 4 : 2));
                
                // סדנאות בדרך כלל יקרות יותר
                if (isWorkshopSeries) {
                    lecture.setPrice(lecture.getPrice() + 100);
                    lecture.setDescription("סדנה מעשית ב" + expertise + 
                        " - שיעור " + (i + 1) + ". כולל תרגילים מעשיים וליווי אישי.");
                }
                
                // שמירה
                lecture = lectureRepository.save(lecture);
                
                // יצירת קשר
                lecture.getLecturers().add(lecturer);
                lecturer.enrollLecture(lecture);
                
                lectureRepository.save(lecture);
                createdLectures.add(lecture);
            }
            
            // עדכון המרצה
            lecturerRepository.save(lecturer);
            
            result.put("success", true);
            result.put("lectures", createdLectures);
            result.put("series_count", seriesCount);
            result.put("series_type", isWorkshopSeries ? "workshop" : "course");
            result.put("lecturer", lecturer);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * מחזיר רשימת הרצאות מפורטת עם פאגינציה וסטטיסטיקות
     */
    public Map<String, Object> getAllLecturesWithDetails(int page, int size, String sortBy, String sortDirection) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // יצירת Pageable
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            // שליפת הנתונים עם פאגינציה
            Page<Lecture> lecturePage = lectureRepository.findAll(pageable);
            
            // הכנת רשימה מפורטת עם מידע על המרצים
            List<Map<String, Object>> detailedLectures = lecturePage.getContent().stream()
                .map(this::createDetailedLectureInfo)
                .collect(Collectors.toList());
            
            // הכנת מידע פאגינציה
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("current_page", page);
            pagination.put("page_size", size);
            pagination.put("total_pages", lecturePage.getTotalPages());
            pagination.put("total_elements", lecturePage.getTotalElements());
            pagination.put("has_next", lecturePage.hasNext());
            pagination.put("has_previous", lecturePage.hasPrevious());
            
            // הכנת סטטיסטיקות
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

    /**
     * מחזיר הרצאות של מרצה ספציפי
     */
    public Map<String, Object> getLecturesByLecturer(Lecturer lecturer) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // שליפת ההרצאות של המרצה
            List<Lecture> lectures = new ArrayList<>(lecturer.getLectures());
            
            // מיון לפי תאריך
            lectures.sort((l1, l2) -> l1.getCreatedAt().compareTo(l2.getCreatedAt()));
            
            // הכנת מידע מפורט על כל הרצאה
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

    /**
     * מחזיר הרצאות של מרצים לפי שם (חיפוש חלקי)
     */
    public Map<String, Object> getLecturesByLecturerName(String lecturerName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // חיפוש מרצים לפי שם (חיפוש חלקי)
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            List<Lecturer> matchingLecturers = allLecturers.stream()
                .filter(lecturer -> {
                    String fullName = (lecturer.getFirstName() + " " + lecturer.getLastName()).toLowerCase();
                    return fullName.contains(lecturerName.toLowerCase()) ||
                           lecturer.getFirstName().toLowerCase().contains(lecturerName.toLowerCase()) ||
                           lecturer.getLastName().toLowerCase().contains(lecturerName.toLowerCase());
                })
                .collect(Collectors.toList());
            
            if (matchingLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "לא נמצאו מרצים עם השם: " + lecturerName);
                return result;
            }
            
            // אסיפת כל ההרצאות של המרצים המתאימים
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
            
            // מיון כל ההרצאות לפי תאריך
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

    /**
     * יוצר הרצאה פייק למרצה לפי שם (חיפוש חלקי)
     */
    public Map<String, Object> createFakeLectureForLecturerName(String lecturerName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // חיפוש מרצים לפי שם
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            List<Lecturer> matchingLecturers = allLecturers.stream()
                .filter(lecturer -> {
                    String fullName = (lecturer.getFirstName() + " " + lecturer.getLastName()).toLowerCase();
                    return fullName.contains(lecturerName.toLowerCase()) ||
                           lecturer.getFirstName().toLowerCase().contains(lecturerName.toLowerCase()) ||
                           lecturer.getLastName().toLowerCase().contains(lecturerName.toLowerCase());
                })
                .collect(Collectors.toList());
            
            if (matchingLecturers.isEmpty()) {
                result.put("success", false);
                result.put("message", "לא נמצאו מרצים עם השם: " + lecturerName);
                return result;
            }
            
            // בחירת מרצה ראשון מהרשימה (או אקראי)
            Lecturer selectedLecturer = matchingLecturers.get(0);
            if (matchingLecturers.size() > 1) {
                // אם יש מספר מרצים, בחר אקראי
                selectedLecturer = matchingLecturers.get(random.nextInt(matchingLecturers.size()));
            }
            
            // יצירת הרצאה פייק
            var lectureResult = createLectureWithSpecificLecturer(selectedLecturer);
            
            if ((Boolean) lectureResult.get("success")) {
                result.put("success", true);
                result.put("message", "הרצאה פייק נוצרה בהצלחה עבור המרצה: " + 
                    selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", selectedLecturer);
                result.put("matching_lecturers_count", matchingLecturers.size());
                
                if (matchingLecturers.size() > 1) {
                    result.put("note", "נמצאו " + matchingLecturers.size() + " מרצים. נבחר: " + 
                        selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName());
                }
            } else {
                result.put("success", false);
                result.put("message", "שגיאה ביצירת ההרצאה: " + lectureResult.get("error"));
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * יוצר הרצאה פייק עם מרצה אקראי
     */
    public Map<String, Object> createFakeLectureWithRandomLecturer(List<Lecturer> availableLecturers) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (availableLecturers.isEmpty()) {
                result.put("success", false);
                result.put("error", "אין מרצים זמינים");
                return result;
            }
            
            // בחירת מרצה אקראי
            Lecturer randomLecturer = availableLecturers.get(random.nextInt(availableLecturers.size()));
            
            // יצירת הרצאה פייק
            var lectureResult = createLectureWithSpecificLecturer(randomLecturer);
            
            if ((Boolean) lectureResult.get("success")) {
                result.put("success", true);
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", randomLecturer);
                result.put("available_lecturers_count", availableLecturers.size());
            } else {
                result.put("success", false);
                result.put("error", "שגיאה ביצירת ההרצאה: " + lectureResult.get("error"));
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // ========== NEW: Auto-Create Lecturer if Not Exists ==========

    /**
     * יוצר הרצאה עם מרצה - אם המרצה לא קיים, יוצר אותו אוטומטית
     * @param firstName שם פרטי של המרצה (אופציונלי)
     * @param lastName שם משפחה של המרצה (אופציונלי)
     * @param email אימייל של המרצה (אופציונלי)
     * @param createIfNotExists האם ליצור מרצה חדש אם לא קיים (ברירת מחדל: true)
     */
    public Map<String, Object> createLectureWithLecturerOrCreate(
            String firstName, String lastName, String email, Boolean createIfNotExists) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // אם לא סופקו פרטים, צור מרצה פייק חדש
            if ((firstName == null || firstName.trim().isEmpty()) && 
                (lastName == null || lastName.trim().isEmpty()) && 
                (email == null || email.trim().isEmpty())) {
                
                System.out.println("🎯 לא סופקו פרטי מרצה - יוצר מרצה פייק חדש");
                Lecturer newLecturer = lecturerFakerService.generateFakeLecturer();
                newLecturer = lecturerRepository.save(newLecturer);
                
                var lectureResult = createLectureWithSpecificLecturer(newLecturer);
                
                result.put("success", true);
                result.put("action", "created_fake_lecturer_and_lecture");
                result.put("message", "נוצר מרצה פייק חדש והרצאה: " + 
                    newLecturer.getFirstName() + " " + newLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", newLecturer);
                result.put("was_lecturer_created", true);
                
                return result;
            }

            // חיפוש מרצה קיים
            Lecturer existingLecturer = findLecturerByDetails(firstName, lastName, email);
            
            if (existingLecturer != null) {
                // מרצה קיים - צור הרצאה עבורו
                System.out.println("✅ נמצא מרצה קיים: " + existingLecturer.getFirstName() + " " + existingLecturer.getLastName());
                
                var lectureResult = createLectureWithSpecificLecturer(existingLecturer);
                
                result.put("success", true);
                result.put("action", "used_existing_lecturer");
                result.put("message", "נוצרה הרצאה עבור המרצה הקיים: " + 
                    existingLecturer.getFirstName() + " " + existingLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", existingLecturer);
                result.put("was_lecturer_created", false);
                
            } else if (createIfNotExists == null || createIfNotExists) {
                // מרצה לא קיים - צור מרצה חדש
                System.out.println("🆕 מרצה לא קיים - יוצר מרצה חדש");
                
                Lecturer newLecturer = createLecturerFromDetails(firstName, lastName, email);
                newLecturer = lecturerRepository.save(newLecturer);
                
                var lectureResult = createLectureWithSpecificLecturer(newLecturer);
                
                result.put("success", true);
                result.put("action", "created_new_lecturer_and_lecture");
                result.put("message", "נוצר מרצה חדש והרצאה: " + 
                    newLecturer.getFirstName() + " " + newLecturer.getLastName());
                result.put("lecture", lectureResult.get("lecture"));
                result.put("lecturer", newLecturer);
                result.put("was_lecturer_created", true);
                
            } else {
                // מרצה לא קיים ולא רוצים ליצור
                result.put("success", false);
                result.put("action", "lecturer_not_found");
                result.put("message", "מרצה לא נמצא ולא הוגדר ליצור מרצה חדש");
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
            System.err.println("❌ שגיאה ביצירת הרצאה עם מרצה: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * מחפש מרצה קיים לפי פרטים (שם פרטי, שם משפחה, או אימייל)
     */
    private Lecturer findLecturerByDetails(String firstName, String lastName, String email) {
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        
        // חיפוש מדויק לפי אימייל (אם סופק)
        if (email != null && !email.trim().isEmpty()) {
            Optional<Lecturer> byEmail = allLecturers.stream()
                .filter(lecturer -> lecturer.getEmail() != null && 
                        lecturer.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
            
            if (byEmail.isPresent()) {
                System.out.println("🔍 נמצא מרצה לפי אימייל: " + email);
                return byEmail.get();
            }
        }
        
        // חיפוש לפי שם מלא (אם סופקו שני השמות)
        if (firstName != null && !firstName.trim().isEmpty() && 
            lastName != null && !lastName.trim().isEmpty()) {
            
            Optional<Lecturer> byFullName = allLecturers.stream()
                .filter(lecturer -> 
                    lecturer.getFirstName() != null && lecturer.getLastName() != null &&
                    lecturer.getFirstName().equalsIgnoreCase(firstName.trim()) &&
                    lecturer.getLastName().equalsIgnoreCase(lastName.trim()))
                .findFirst();
            
            if (byFullName.isPresent()) {
                System.out.println("🔍 נמצא מרצה לפי שם מלא: " + firstName + " " + lastName);
                return byFullName.get();
            }
        }
        
        // חיפוש חלקי לפי שם פרטי או משפחה
        if (firstName != null && !firstName.trim().isEmpty()) {
            Optional<Lecturer> byFirstName = allLecturers.stream()
                .filter(lecturer -> lecturer.getFirstName() != null &&
                        lecturer.getFirstName().toLowerCase().contains(firstName.trim().toLowerCase()))
                .findFirst();
            
            if (byFirstName.isPresent()) {
                System.out.println("🔍 נמצא מרצה לפי שם פרטי: " + firstName);
                return byFirstName.get();
            }
        }
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            Optional<Lecturer> byLastName = allLecturers.stream()
                .filter(lecturer -> lecturer.getLastName() != null &&
                        lecturer.getLastName().toLowerCase().contains(lastName.trim().toLowerCase()))
                .findFirst();
            
            if (byLastName.isPresent()) {
                System.out.println("🔍 נמצא מרצה לפי שם משפחה: " + lastName);
                return byLastName.get();
            }
        }
        
        System.out.println("❌ לא נמצא מרצה מתאים");
        return null;
    }

    /**
     * יוצר מרצה חדש מפרטים שסופקו (משלים פרטים חסרים באופן אוטומטי)
     */
    private Lecturer createLecturerFromDetails(String firstName, String lastName, String email) {
        // אם לא סופק שם פרטי, צור אקראי
        if (firstName == null || firstName.trim().isEmpty()) {
            firstName = faker.name().firstName();
            System.out.println("🎲 נוצר שם פרטי אקראי: " + firstName);
        }
        
        // אם לא סופק שם משפחה, צור אקראי
        if (lastName == null || lastName.trim().isEmpty()) {
            lastName = faker.name().lastName();
            System.out.println("🎲 נוצר שם משפחה אקראי: " + lastName);
        }
        
        // אם לא סופק אימייל, צור מהשמות
        if (email == null || email.trim().isEmpty()) {
            email = (firstName + "." + lastName + "." + System.currentTimeMillis() 
                    + "@" + faker.internet().domainName()).toLowerCase();
            System.out.println("📧 נוצר אימייל אוטומטי: " + email);
        }
        
        // צור מרצה חדש עם הפרטים
        String specialty = faker.job().title();
        int experience = faker.number().numberBetween(3, 18);
        String bio = String.format(
            "%s is a %s with %d years of experience. Created specifically for lecture integration.",
            firstName, specialty, experience
        );
        
        String city = faker.address().cityName();
        String phone = generateUniqueIsraeliPhone();
        String imageUrl = "https://picsum.photos/400/400?random=" + faker.random().nextInt(10000);
        
        Lecturer lecturer = Lecturer.builder()
            .userId(java.util.UUID.randomUUID())
            .firstName(firstName.trim())
            .lastName(lastName.trim())
            .bio(bio)
            .city(city)
            .email(email.trim().toLowerCase())
            .phone(phone)
            .imageUrl(imageUrl)
            .workingArea(getRandomWorkingArea())
            .status(LecturerStatus.APPROVED)
            .createdAt(LocalDateTime.now())
            .lastUpdatedAt(LocalDateTime.now())
            .build();
        
        System.out.println("👨‍🏫 נוצר מרצה חדש: " + firstName + " " + lastName + " (" + email + ")");
        
        return lecturer;
    }

    /**
     * יוצר מספר טלפון ישראלי ייחודי
     */
    private String generateUniqueIsraeliPhone() {
        String[] prefixes = {"050", "052", "053", "054", "055", "058"};
        String prefix = prefixes[faker.random().nextInt(prefixes.length)];
        long timestamp = System.currentTimeMillis() % 10000000;
        return prefix + String.format("%07d", timestamp);
    }

    /**
     * בוחר תחום עבודה אקראי
     */
    private Area getRandomWorkingArea() {
        Area[] areas = Area.values();
        return areas[faker.random().nextInt(areas.length)];
    }

    /**
     * יוצר מספר הרצאות עם מרצים - יוצר מרצים חדשים אם לא קיימים
     */
    public Map<String, Object> createMultipleLecturesWithAutoCreateLecturers(
            int count, List<String[]> lecturerDetailsList) {
        Map<String, Object> result = new HashMap<>();
        List<Lecture> createdLectures = new ArrayList<>();
        List<Lecturer> createdLecturers = new ArrayList<>();
        List<Lecturer> usedExistingLecturers = new ArrayList<>();
        
        try {
            System.out.println("🎯 יוצר " + count + " הרצאות עם יצירה אוטומטית של מרצים");
            
            for (int i = 0; i < count; i++) {
                String firstName = null;
                String lastName = null;
                String email = null;
                
                // אם סופקה רשימת פרטי מרצים
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
            result.put("message", "נוצרו " + createdLectures.size() + " הרצאות בהצלחה");
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
            System.err.println("❌ שגיאה ביצירת הרצאות מרובות: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * חיפוש וניתוח מרצים לפי פרטים חלקיים
     */
    public Map<String, Object> searchLecturersByDetails(String searchTerm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            String lowerSearchTerm = searchTerm.toLowerCase().trim();
            
            List<Lecturer> matchingLecturers = allLecturers.stream()
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
            result.put("total_lecturers_in_db", allLecturers.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * מחזיר את הסיבה להתאמה בחיפוש
     */
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

    // ========== Existing Advanced System Creation ==========

    public Map<String, Object> createCompleteSystemWithRelations(int lecturerCount, int lectureCount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("Starting complete system creation...");
            
            // Step 1: Create lecturers
            List<Lecturer> lecturers = new ArrayList<>();
            for (int i = 0; i < lecturerCount; i++) {
                Lecturer lecturer = lecturerFakerService.generateFakeLecturer();
                lecturers.add(lecturer);
            }
            lecturers = lecturerRepository.saveAll(lecturers);
            System.out.println("Created " + lecturers.size() + " lecturers");
            
            // Step 2: Create lectures
            List<Lecture> lectures = new ArrayList<>();
            for (int i = 0; i < lectureCount; i++) {
                Lecture lecture = lectureFakerService.generateFakeLecture();
                lectures.add(lecture);
            }
            lectures = lectureRepository.saveAll(lectures);
            System.out.println("Created " + lectures.size() + " lectures");
            
            // Step 3: Create relationships
            int totalRelations = 0;
            for (Lecture lecture : lectures) {
                Set<Lecturer> selectedLecturers = selectRandomLecturers(lecturers);
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
            
            System.out.println("Created " + totalRelations + " relationships");
            
            result.put("success", true);
            result.put("lecturers_created", lecturers.size());
            result.put("lectures_created", lectures.size());
            result.put("total_relations", totalRelations);
            result.put("lecturers", lecturers);
            result.put("lectures", lectures);
            
        } catch (Exception e) {
            System.err.println("Error in createCompleteSystemWithRelations: " + e.getMessage());
            e.printStackTrace();
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
                    randomLecturer.enrollLecture(randomLecture);
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
                "זוהי הזדמנות נדירה לשמוע מכל המרצים המובילים שלנו."
            );
            superLecture.setPrice(500);
            superLecture.setDuration(480); // 8 hours
            superLecture.setOnline(false);
            superLecture.setImageUrl("https://picsum.photos/800/400?random=2000");
            superLecture.setCreatedAt(LocalDateTime.now());
            superLecture.setUpdatedAt(LocalDateTime.now()); // ✅ Important!
            superLecture.setStatus(LectureStatus.ON_AIR);
            
            Set<Lecturer> allLecturersSet = new HashSet<>(allLecturers);
            superLecture.setLecturers(allLecturersSet);
            
            // Update lecturer side
            for (Lecturer lecturer : allLecturers) {
                lecturer.enrollLecture(superLecture);
            }
            
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
                
                // Update lecturer side
                for (Lecturer lecturer1 : lecturers) {
                    lecturer1.enrollLecture(lecture);
                }
                
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

    // ========== NEW: Helper Methods for Enhanced Features ==========

    /**
     * יוצר מידע מפורט על הרצאה
     */
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
        
        // מידע על המרצים
        List<Map<String, Object>> lecturersInfo = lecture.getLecturers().stream()
            .map(this::createBasicLecturerInfo)
            .collect(Collectors.toList());
        info.put("lecturers", lecturersInfo);
        info.put("lecturers_count", lecturersInfo.size());
        
        // מידע נוסף
        info.put("duration_minutes", lecture.getDuration());
        
        return info;
    }

    /**
     * יוצר מידע בסיסי על מרצה
     */
    private Map<String, Object> createBasicLecturerInfo(Lecturer lecturer) {
        Map<String, Object> info = new HashMap<>();
        
        info.put("id", lecturer.getUserId());
        info.put("name", lecturer.getFirstName() + " " + lecturer.getLastName());
        info.put("email", lecturer.getEmail());
        info.put("expertise", getExpertiseFromLecturer(lecturer));
        
        return info;
    }

    /**
     * יוצר מידע מפורט על מרצה
     */
    private Map<String, Object> createDetailedLecturerInfo(Lecturer lecturer) {
        Map<String, Object> info = createBasicLecturerInfo(lecturer);
        
        info.put("total_lectures", lecturer.getLectures().size());
        info.put("upcoming_lectures", lecturer.getLectures().stream()
            .mapToInt(lecture -> lecture.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)) ? 1 : 0)
            .sum());
        
        return info;
    }

    /**
     * מייצר כותרת הרצאה על בסיס תחום מומחיות
     */
    private String generateTitleForExpertise(String expertise) {
        if (expertise == null || expertise.isEmpty()) {
            expertise = "Technology"; // default
        }
        
        Map<String, List<String>> expertiseTitles = Map.of(
            "Technology", List.of("חדשנות טכנולוגית", "מגמות בטכנולוגיה", "עתיד הטכנולוגיה"),
            "AI", List.of("בינה מלאכותית למתחילים", "יישומי AI בעסקים", "עתיד ה-AI"),
            "Data Science", List.of("מדע הנתונים בפועל", "ניתוח נתונים מתקדם", "ביג דאטה למנהלים"),
            "Cybersecurity", List.of("אבטחת מידע מתקדמת", "הגנה בעולם הדיגיטלי", "אתיקה באבטחה"),
            "Cloud", List.of("מחשוב ענן למתחילים", "ארכיטקטורת ענן", "ביטחון בענן"),
            "Programming", List.of("תכנות מתקדם", "פיתוח אפליקציות", "הקוד הנקי"),
            "Management", List.of("ניהול צוותים", "מנהיגות בעידן הדיגיטלי", "ניהול פרויקטים")
        );
        
        List<String> titles = expertiseTitles.get(expertise);
        if (titles != null && !titles.isEmpty()) {
            Random random = new Random();
            return titles.get(random.nextInt(titles.size()));
        }
        
        return "הרצאה ב" + expertise;
    }

    /**
     * מייצר סטטיסטיקות על ההרצאות
     */
    private Map<String, Object> generateLectureStatistics() {
        List<Lecture> allLectures = lectureRepository.findAll();
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        
        // סטטיסטיקות בסיסיות
        stats.put("total_lectures", allLectures.size());
        stats.put("total_lecturers", allLecturers.size());
        
        // סטטיסטיקות הרצאות
        long onlineLectures = allLectures.stream().filter(Lecture::isOnline).count();
        stats.put("online_lectures", onlineLectures);
        stats.put("physical_lectures", allLectures.size() - onlineLectures);
        
        // סטטיסטיקות זמן
        long recentLectures = allLectures.stream()
            .filter(lecture -> lecture.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)))
            .count();
        stats.put("recent_lectures", recentLectures);
        stats.put("older_lectures", allLectures.size() - recentLectures);
        
        // סטטיסטיקות מחיר
        OptionalDouble avgPrice = allLectures.stream().mapToInt(Lecture::getPrice).average();
        stats.put("average_price", avgPrice.isPresent() ? Math.round(avgPrice.getAsDouble() * 100.0) / 100.0 : 0);
        
        IntSummaryStatistics priceStats = allLectures.stream()
            .mapToInt(Lecture::getPrice)
            .summaryStatistics();
        stats.put("min_price", priceStats.getMin());
        stats.put("max_price", priceStats.getMax());
        
        // סטטיסטיקות מרצים
        long lecturersWithLectures = allLecturers.stream()
            .filter(lecturer -> !lecturer.getLectures().isEmpty())
            .count();
        stats.put("active_lecturers", lecturersWithLectures);
        stats.put("inactive_lecturers", allLecturers.size() - lecturersWithLectures);
        
        return stats;
    }

    /**
     * מחלץ תחום מומחיות מהמרצה
     */
    private String getExpertiseFromLecturer(Lecturer lecturer) {
        // זה יתלה במבנה שלך של Lecturer
        // אם יש שדה expertise, השתמש בו
        // אחרת, נסה לנחש לפי שם או אימייל
        
        if (lecturer.getEmail() != null) {
            String email = lecturer.getEmail().toLowerCase();
            if (email.contains("ai") || email.contains("ml")) return "AI";
            if (email.contains("data")) return "Data Science";
            if (email.contains("security") || email.contains("cyber")) return "Cybersecurity";
            if (email.contains("cloud")) return "Cloud";
            if (email.contains("dev") || email.contains("prog")) return "Programming";
            if (email.contains("manage")) return "Management";
        }
        
        // default
        return "Technology";
    }

    // ========== Existing Helper Methods ==========

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

    private int countAllRelations() {
        return lectureRepository.findAll().stream()
            .mapToInt(lecture -> lecture.getLecturers().size())
            .sum();
    }
}