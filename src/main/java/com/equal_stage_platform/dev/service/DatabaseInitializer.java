package com.equal_stage_platform.dev.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.fake.LectureFakerService;
import com.equal_stage_platform.dev.fake.LecturerFakerService;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;

@Service
@Transactional
public class DatabaseInitializer {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LecturerFakerService lecturerFakerService;

    @Autowired
    private LectureFakerService lectureFakerService;

    private final Random random = new Random();

    /**
     * יצירת נתונים מזויפים מלאים למסד הנתונים
     * מוחק נתונים קיימים ויוצר מחדש
     */
    public void createFakeData() {
        createFakeData(25, 60); // default: 25 מרצים, 60 הרצאות
    }

    /**
     * יצירת נתונים מזויפים עם כמויות מותאמות אישית
     */
    public void createFakeData(int lecturerCount, int lectureCount) {
        System.out.println("Starting to create fake data...");
        
        // יצירת מרצים
        List<Lecturer> lecturers = createFakeLecturers(lecturerCount);
        System.out.println("Created " + lecturers.size() + " lecturers");
        
        // יצירת הרצאות
        List<Lecture> lectures = createFakeLectures(lectureCount);
        System.out.println("Created " + lectures.size() + " lectures");
        
        // קישור מרצים להרצאות
        linkLecturersToLectures(lecturers, lectures);
        System.out.println("Linked lecturers to lectures");
        
        // שמירה סופית
        lecturerRepository.saveAll(lecturers);
        lectureRepository.saveAll(lectures);
        
        System.out.println("Fake data creation completed!");
        System.out.println("Total lecturers in DB: " + lecturerRepository.count());
        System.out.println("Total lectures in DB: " + lectureRepository.count());
    }

    /**
     * יצירת מרצים מזויפים
     */
    private List<Lecturer> createFakeLecturers(int count) {
        List<Lecturer> lecturers = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            try {
                Lecturer lecturer = lecturerFakerService.generateFakeLecturer();
                lecturers.add(lecturer);
            } catch (Exception e) {
                System.err.println("Error creating lecturer " + i + ": " + e.getMessage());
            }
        }
        
        // שמירת המרצים במסד הנתונים
        return lecturerRepository.saveAll(lecturers);
    }

    /**
     * יצירת הרצאות מזויפות
     */
    private List<Lecture> createFakeLectures(int count) {
        List<Lecture> lectures = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = lectureFakerService.generateFakeLecture();
                lectures.add(lecture);
            } catch (Exception e) {
                System.err.println("Error creating lecture " + i + ": " + e.getMessage());
            }
        }
        
        // שמירת ההרצאות במסד הנתונים
        return lectureRepository.saveAll(lectures);
    }

    /**
     * קישור מרצים להרצאות באופן רנדומלי
     */
    private void linkLecturersToLectures(List<Lecturer> lecturers, List<Lecture> lectures) {
        if (lecturers.isEmpty() || lectures.isEmpty()) {
            System.out.println("Cannot link - no lecturers or lectures available");
            return;
        }

        for (Lecture lecture : lectures) {
            // בחירת מספר רנדומלי של מרצים לכל הרצאה (1-3)
            int numLecturers = random.nextInt(3) + 1;
            
            // בחירת מרצים רנדומליים
            Set<Lecturer> selectedLecturers = random.ints(0, lecturers.size())
                .distinct()
                .limit(numLecturers)
                .mapToObj(lecturers::get)
                .collect(Collectors.toSet());
            
            // קישור המרצים להרצאה
            for (Lecturer lecturer : selectedLecturers) {
                lecture.addLecturer(lecturer);
                lecturer.enrollLecture(lecture);
            }
        }
    }

    /**
     * הוספת מרצים נוספים למסד הנתונים הקיים
     */
    public List<Lecturer> addMoreLecturers(int count) {
        List<Lecturer> newLecturers = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            try {
                Lecturer lecturer = lecturerFakerService.generateFakeLecturer();
                newLecturers.add(lecturer);
            } catch (Exception e) {
                System.err.println("Error creating additional lecturer " + i + ": " + e.getMessage());
            }
        }
        
        return lecturerRepository.saveAll(newLecturers);
    }

    /**
     * הוספת הרצאות נוספות למסד הנתונים הקיים
     */
    public List<Lecture> addMoreLectures(int count) {
        List<Lecture> newLectures = new ArrayList<>();
        List<Lecturer> existingLecturers = lecturerRepository.findAll();
        
        for (int i = 0; i < count; i++) {
            try {
                Lecture lecture = lectureFakerService.generateFakeLecture();
                
                // קישור רנדומלי למרצים קיימים
                if (!existingLecturers.isEmpty()) {
                    int numLecturers = random.nextInt(Math.min(3, existingLecturers.size())) + 1;
                    Set<Lecturer> selectedLecturers = random.ints(0, existingLecturers.size())
                        .distinct()
                        .limit(numLecturers)
                        .mapToObj(existingLecturers::get)
                        .collect(Collectors.toSet());
                    
                    for (Lecturer lecturer : selectedLecturers) {
                        lecture.addLecturer(lecturer);
                        lecturer.enrollLecture(lecture);
                    }
                }
                
                newLectures.add(lecture);
            } catch (Exception e) {
                System.err.println("Error creating additional lecture " + i + ": " + e.getMessage());
            }
        }
        
        List<Lecture> savedLectures = lectureRepository.saveAll(newLectures);
        
        // עדכון המרצים שהתקשרו
        if (!existingLecturers.isEmpty()) {
            lecturerRepository.saveAll(existingLecturers);
        }
        
        return savedLectures;
    }

    /**
     * ניקוי מלא של מסד הנתונים
     */
    public void clearAllData() {
        System.out.println("Clearing all data from database...");
        
        long lecturesBefore = lectureRepository.count();
        long lecturersBefore = lecturerRepository.count();
        
        // מחיקה בסדר הנכון כדי למנוע בעיות עם foreign keys
        lectureRepository.deleteAll();
        lecturerRepository.deleteAll();
        
        System.out.println("Deleted " + lecturesBefore + " lectures and " + lecturersBefore + " lecturers");
    }

    /**
     * יצירת נתונים מזויפים מהירה לטסטים
     */
    public void createQuickTestData() {
        createFakeData(5, 10); // 5 מרצים, 10 הרצאות
    }

    /**
     * יצירת נתונים מזויפים מורחבת
     */
    public void createExtendedData() {
        createFakeData(50, 150); // 50 מרצים, 150 הרצאות
    }
}