// DebugService.java
package com.equal_stage_platform.dev.service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.fake.LectureFakerService;
import com.equal_stage_platform.dev.fake.LecturerFakerService;
import com.equal_stage_platform.dev.model.Lecture;
import com.equal_stage_platform.dev.model.Lecturer;
import com.equal_stage_platform.dev.repository.LectureRepository;
import com.equal_stage_platform.dev.repository.LecturerRepository;

@Service
@Transactional
public class DebugService {

    @Autowired
    private LecturerRepository lecturerRepository;
    
    @Autowired
    private LectureRepository lectureRepository;
    
    @Autowired
    private LecturerService lecturerService;
    
    @Autowired
    private LectureService lectureService;
    
    @Autowired
    private LecturerFakerService lecturerFakerService;
    
    @Autowired
    private LectureFakerService lectureFakerService;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private Environment environment;

    private final Random random = new Random();

    // מצב כללי של המערכת
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("lecturersCount", lecturerRepository.count());
        status.put("lecturesCount", lectureRepository.count());
        status.put("availableLecturesCount", lectureRepository.findByIsAvailableTrue().size());
        status.put("onlineLecturesCount", lectureRepository.findByIsOnlineTrue().size());
        status.put("physicalLecturesCount", lectureRepository.findByIsOnlineFalse().size());
        status.put("citiesCount", lecturerRepository.findAllCities().size());
        status.put("locationsCount", lectureRepository.findAllLocations().size());
        status.put("timestamp", java.time.LocalDateTime.now());
        return status;
    }

    // מידע על חיבור למסד הנתונים
    public Map<String, Object> getDatabaseConnectionInfo() {
        Map<String, Object> info = new HashMap<>();
        try {
            info.put("url", environment.getProperty("spring.datasource.url"));
            info.put("username", environment.getProperty("spring.datasource.username"));
            info.put("driverClassName", environment.getProperty("spring.datasource.driver-class-name"));
            
            try (var connection = dataSource.getConnection()) {
                info.put("isConnectionValid", connection.isValid(5));
                info.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
                info.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            info.put("error", "Cannot connect to database: " + e.getMessage());
        }
        return info;
    }

    // רשימת טבלאות במסד הנתונים
    public List<Map<String, Object>> getDatabaseTables() {
        List<Map<String, Object>> tables = new ArrayList<>();
        try (var connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (rs.next()) {
                Map<String, Object> table = new HashMap<>();
                String tableName = rs.getString("TABLE_NAME");
                table.put("tableName", tableName);
                table.put("tableType", rs.getString("TABLE_TYPE"));
                
                // קבלת עמודות עבור הטבלה
                List<String> columns = new ArrayList<>();
                try (ResultSet columnsRs = metaData.getColumns(null, null, tableName, "%")) {
                    while (columnsRs.next()) {
                        columns.add(columnsRs.getString("COLUMN_NAME") + " (" + columnsRs.getString("TYPE_NAME") + ")");
                    }
                }
                table.put("columns", columns);
                tables.add(table);
            }
        } catch (SQLException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Cannot fetch tables: " + e.getMessage());
            tables.add(error);
        }
        return tables;
    }

    // דוגמאות נתונים מהטבלאות
    public Map<String, Object> getDatabaseData() {
        Map<String, Object> data = new HashMap<>();
        
        // דוגמאות מרצים
        List<Lecturer> sampleLecturers = lecturerRepository.findAll().stream()
                .limit(3)
                .toList();
        data.put("sampleLecturers", sampleLecturers);
        
        // דוגמאות הרצאות
        List<Lecture> sampleLectures = lectureRepository.findAll().stream()
                .limit(3)
                .toList();
        data.put("sampleLectures", sampleLectures);
        
        return data;
    }

    // הגדרות Hibernate
    public Map<String, Object> getHibernateInfo() {
        Map<String, Object> hibernateInfo = new HashMap<>();
        hibernateInfo.put("hibernateDialect", environment.getProperty("spring.jpa.database-platform"));
        hibernateInfo.put("hibernateHbm2ddlAuto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        hibernateInfo.put("hibernateShowSql", environment.getProperty("spring.jpa.show-sql"));
        hibernateInfo.put("hibernateFormatSql", environment.getProperty("spring.jpa.properties.hibernate.format_sql"));
        return hibernateInfo;
    }

    // יצירת מרצה בודד
    public ResponseLecturerDTO createSingleLecturer() {
        Lecturer fakeLecturer = lecturerFakerService.generateFakeLecturer();
        Lecturer savedLecturer = lecturerRepository.save(fakeLecturer);
        return lecturerService.getLecturerById(savedLecturer.getUserId());
    }

    // יצירת הרצאה בודדת
    public ResponseLectureDTO createSingleLecture() {
        Lecture fakeLecture = lectureFakerService.generateFakeLecture();
        
        // הוספת מרצה רנדומלי אם קיימים מרצים
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        if (!allLecturers.isEmpty()) {
            Lecturer randomLecturer = allLecturers.get(random.nextInt(allLecturers.size()));
            Set<Lecturer> lecturers = new HashSet<>();
            lecturers.add(randomLecturer);
            fakeLecture.setLecturers(lecturers);
        }
        
        Lecture savedLecture = lectureRepository.save(fakeLecture);
        return lectureService.getLectureById(savedLecture.getLectureId().longValue());
    }

    // יצירת מספר מרצים
    public List<ResponseLecturerDTO> createMultipleLecturers(int count) {
        List<ResponseLecturerDTO> createdLecturers = new ArrayList<>();
        List<Lecturer> lecturersToSave = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Lecturer fakeLecturer = lecturerFakerService.generateFakeLecturer();
            lecturersToSave.add(fakeLecturer);
        }
        
        // שמירה bulk
        List<Lecturer> savedLecturers = lecturerRepository.saveAll(lecturersToSave);
        
        for (Lecturer lecturer : savedLecturers) {
            createdLecturers.add(lecturerService.getLecturerById(lecturer.getUserId()));
        }
        
        return createdLecturers;
    }

    // יצירת מספר הרצאות
    public List<ResponseLectureDTO> createMultipleLectures(int count) {
        List<ResponseLectureDTO> createdLectures = new ArrayList<>();
        List<Lecture> lecturesToSave = new ArrayList<>();
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        
        for (int i = 0; i < count; i++) {
            Lecture fakeLecture = lectureFakerService.generateFakeLecture();
            
            // הוספת מרצה רנדומלי אם קיימים מרצים
            if (!allLecturers.isEmpty()) {
                Lecturer randomLecturer = allLecturers.get(random.nextInt(allLecturers.size()));
                Set<Lecturer> lecturers = new HashSet<>();
                lecturers.add(randomLecturer);
                fakeLecture.setLecturers(lecturers);
            }
            
            lecturesToSave.add(fakeLecture);
        }
        
        // שמירה bulk
        List<Lecture> savedLectures = lectureRepository.saveAll(lecturesToSave);
        
        for (Lecture lecture : savedLectures) {
            createdLectures.add(lectureService.getLectureById(lecture.getLectureId().longValue()));
        }
        
        return createdLectures;
    }

    // יצירת מערכת מלאה
    public Map<String, Object> createFullSystem(int lecturerCount, int lectureCount) {
        Map<String, Object> result = new HashMap<>();
        
        // יצירת מרצים
        List<ResponseLecturerDTO> lecturers = createMultipleLecturers(lecturerCount);
        
        // יצירת הרצאות
        List<ResponseLectureDTO> lectures = createMultipleLectures(lectureCount);
        
        result.put("createdLecturers", lecturers);
        result.put("createdLectures", lectures);
        result.put("lecturersCount", lecturers.size());
        result.put("lecturesCount", lectures.size());
        result.put("timestamp", java.time.LocalDateTime.now());
        
        return result;
    }

    // מחיקת כל המרצים
    public Map<String, Object> clearAllLecturers() {
        long count = lecturerRepository.count();
        lecturerRepository.deleteAll();
        Map<String, Object> result = new HashMap<>();
        result.put("deletedLecturersCount", count);
        result.put("timestamp", java.time.LocalDateTime.now());
        return result;
    }

    // מחיקת כל ההרצאות
    public Map<String, Object> clearAllLectures() {
        long count = lectureRepository.count();
        lectureRepository.deleteAll();
        Map<String, Object> result = new HashMap<>();
        result.put("deletedLecturesCount", count);
        result.put("timestamp", java.time.LocalDateTime.now());
        return result;
    }

    // מחיקה כללית
    public Map<String, Object> clearAll() {
        long lecturersCount = lecturerRepository.count();
        long lecturesCount = lectureRepository.count();
        
        // מחיקת הרצאות קודם (foreign key constraints)
        lectureRepository.deleteAll();
        lecturerRepository.deleteAll();
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedLecturersCount", lecturersCount);
        result.put("deletedLecturesCount", lecturesCount);
        result.put("timestamp", java.time.LocalDateTime.now());
        return result;
    }
}