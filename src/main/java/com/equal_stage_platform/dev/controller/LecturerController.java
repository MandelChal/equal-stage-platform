package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.service.LecturerService;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

import com.equal_stage_platform.dev.dto.PaginatedResponseDTO;
// import com.equal_stage_platform.dev.dto.ApiResponseDTO;
import com.equal_stage_platform.dev.dto.PaginationRequest;
import com.equal_stage_platform.dev.model.enums.LecturerStatus;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.service.AuthService;
import com.equal_stage_platform.dev.service.JwtService;
import com.equal_stage_platform.dev.service.LectureService;
import com.equal_stage_platform.dev.exception.LecturerException;
import com.equal_stage_platform.dev.exception.LectureException;
import com.equal_stage_platform.dev.exception.AuthException;
import jakarta.validation.Valid;
import com.equal_stage_platform.dev.dto.SearchResultDTO;

@RestController
@RequestMapping("/lecturers")
public class LecturerController {
    // If you want to log errors, uncomment the next line:
    // private static final Logger logger = LoggerFactory.getLogger(LecturerController.class);

    private final LecturerService lecturerService;
    private final LectureService lectureService;
    private final JwtService jwtService;
    private final AuthService authService;
    public LecturerController(LecturerService lecturerService, LectureService lectureService, JwtService jwtService, AuthService authService) {
        this.lecturerService = lecturerService;
        this.lectureService = lectureService;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLecturer(@RequestHeader("Authorization") String token, @Valid @RequestBody CreateLecturerDTO lecturerData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            lecturerData.setUserId(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(lecturerService.createLecturer(lecturerData));
        } catch (LecturerException e) {
            // logger.error("LecturerException while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while creating lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PatchMapping("/admin/{lecturerId}/status/{status}")
    public ResponseEntity<?> updateLecturerStatusByAdmin(@PathVariable UUID userId, @PathVariable LecturerStatus status) {
        return updateLecturerStatus(userId, status, true);
    }

    @PatchMapping("/update/status/{status}")
    public ResponseEntity<?> updateLecturerStatus(@RequestHeader("Authorization") String token, @PathVariable LecturerStatus status) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return updateLecturerStatus(userId, status, false);
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecturer status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private ResponseEntity<?> updateLecturerStatus(UUID userId, LecturerStatus status, boolean isAdmin) {
        try {
            if(status == null || status == LecturerStatus.PENDING) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status provided");
            } 
            return ResponseEntity.ok(lecturerService.updateLecturerStatus(userId, status, isAdmin));
        } catch (LecturerException e) {
            // logger.error("LecturerException while updating lecturer status", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecturer status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/admin/all")
    // @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<?> getAllLecturers() {
        try {
            return ResponseEntity.ok(lecturerService.getAllLecturers());
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching all lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching all lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/all/approved")
    public ResponseEntity<?> getAllApprovedLecturers() {
        try {
            return ResponseEntity.ok(lecturerService.getLecturersByStatus(LecturerStatus.APPROVED));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching approved lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching approved lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("search/id/{userId}")
    public ResponseEntity<?> getLecturerById(@PathVariable UUID userId) {
        return getLecturerById(userId, false);
    }

    @GetMapping("/admin/search/id/{userId}")
    public ResponseEntity<?> getLecturerByIdAdmin(@PathVariable UUID userId) {
        return getLecturerById(userId, true);
    }
    
    private ResponseEntity<?> getLecturerById(UUID userId, boolean isAdmin) {
        try {
            return ResponseEntity.ok(lecturerService.getLecturerById(userId, isAdmin));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching lecturer by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecturer by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/lectures/{lecturerId}/all")
    public ResponseEntity<?> getLecturesByLecturerId(@PathVariable UUID lecturerId) {
        try {
            return ResponseEntity.ok(lecturerService.getLecturesByLecturerId(lecturerId));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching lectures by lecturer ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lectures by lecturer ID", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lectures by lecturer ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/lectures/{lecturerId}/{lectureId}")
    public ResponseEntity<?> getLectureById(@PathVariable UUID lecturerId, @PathVariable Long lectureId) {
        try {
            return ResponseEntity.ok(lecturerService.getLectureById(lecturerId, lectureId));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingLecturers() {
        try {
            return ResponseEntity.ok(lecturerService.getLecturersByStatus(LecturerStatus.PENDING));
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching pending lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching pending lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/approve/{lecturerId}")
    // @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<?> approveLecturer(@PathVariable UUID lecturerId) {
        try {
            ResponseLecturerDTO lecturer = lecturerService.updateLecturerStatus(lecturerId, LecturerStatus.APPROVED, true);
            if(authService.getUserRole(lecturerId) != Role.ADMIN) {
                authService.changeRole(lecturerId, Role.LECTURER);
            }
            return ResponseEntity.ok(lecturer);
        } catch (LecturerException e) {
            // logger.error("LecturerException while approving lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while approving lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/reject/{lecturerId}")
    // @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<?> rejectLecturer(@PathVariable UUID lecturerId) {
        try {
            return ResponseEntity.ok(lecturerService.updateLecturerStatus(lecturerId, LecturerStatus.REJECTED, true));
        } catch (LecturerException e) {
            // logger.error("LecturerException while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<?> searchLecturersByName(@PathVariable String name) {
        return searchLecturersByName(name, false);
    }

    @GetMapping("/admin/search/name/{name}")
    public ResponseEntity<?> searchLecturersByNameAdmin(@PathVariable String name) {
        return searchLecturersByName(name, true);
    }

    private ResponseEntity<?> searchLecturersByName(String name, boolean isAdmin) {
        try {
            return ResponseEntity.ok(lecturerService.searchLecturersByName(name, isAdmin));
        } catch (LecturerException e) {
            // logger.error("LecturerException while searching lecturers by name", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while searching lecturers by name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/del/{userId}")
    // @PreAuthorize("hasRole('ADMIN')") // endpoint for admin to delete any lecturer
    public ResponseEntity<?> deleteLecturer(@PathVariable UUID userId) {
        return deleteLecturer(userId, true);
    }

    @DeleteMapping("del/self")
    // @PreAuthorize("hasRole('LECTURER')") // endpoint for lecturer to delete their own profile
    public ResponseEntity<?> deleteLecturer(@RequestHeader("Authorization") String token) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return deleteLecturer(userId, false);
        } catch (Exception e) {
            // logger.error("Unexpected error while rejecting lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private ResponseEntity<?> deleteLecturer(UUID userId, boolean isAdmin) {
        try {
            return ResponseEntity.ok(lecturerService.deleteLecturer(userId));
        } catch (LecturerException e) {
            // logger.error("LecturerException while deleting lecturer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while deleting lecturer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedLecturersByStatus(@RequestBody PaginationRequest request) {
        try {
            PaginatedResponseDTO<ResponseLecturerDTO> paginated = lecturerService.getPaginatedLecturers(LecturerStatus.APPROVED ,request.getPageNum(), request.getPageSize());
            return ResponseEntity.ok(paginated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchLecturesAndLecturers(@PathVariable String name) {
        try {
            SearchResultDTO result = SearchResultDTO.builder()
                .lecturers(lecturerService.searchLecturersByNamePrefix(name))
                .lectures(lectureService.searchLecturesByNamePrefix(name))
                .build();
            return ResponseEntity.ok(result);
        } catch (LecturerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (LectureException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}