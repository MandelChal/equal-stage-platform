package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.equal_stage_platform.dev.dto.CreateLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.service.LecturerService;

import java.util.List;
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
import com.equal_stage_platform.dev.dto.UpdateLecturerDTO;
import com.equal_stage_platform.dev.model.enums.Area;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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

    @Operation(summary = "Create a new lecturer", description = "Creates a new lecturer profile. Access: Only users with roles USER or ADMIN.")
    @ApiResponse(responseCode = "201", description = "Lecturer created successfully", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/create")
    public ResponseEntity<?> createLecturer(@RequestHeader("Authorization") String token, @Valid @RequestBody CreateLecturerDTO lecturerData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.status(HttpStatus.CREATED).body(lecturerService.createLecturer(userId, lecturerData));
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


    @Operation(summary = "Update lecturer status by admin", description = "Updates the status of a lecturer by admin. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer status updated", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PatchMapping("/admin/{lecturerId}/status/{status}")
    public ResponseEntity<?> updateLecturerStatusByAdmin(@PathVariable UUID userId, @PathVariable LecturerStatus status) {
        return updateLecturerStatus(userId, status, true);
    }

    @Operation(summary = "Update lecturer status", description = "Updates the status of the authenticated lecturer. Access: Only users with roles LECTURER or ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer status updated", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Get all lecturers (admin)", description = "Retrieves all lecturers. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "List of lecturers", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Get all approved lecturers", description = "Retrieves all approved lecturers. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of approved lecturers", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Get lecturer by ID", description = "Retrieves a lecturer by user ID. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Lecturer found", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecturer not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("search/id/{userId}")
    public ResponseEntity<?> getLecturerById(@PathVariable UUID userId) {
        return getLecturerById(userId, false);
    }

    @Operation(summary = "Get lecturer by ID (admin)", description = "Retrieves a lecturer by user ID for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer found", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecturer not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Get all lectures by lecturer ID", description = "Retrieves all lectures for a given lecturer. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of lectures", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecturer or lectures not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/{lecturerId}/lectures/all")
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

    @Operation(summary = "Get lecture by lecturer and lecture ID", description = "Retrieves a lecture by lecturer and lecture ID. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Lecture found", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecture not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/{lecturerId}/lectures/{lectureId}")
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

    @Operation(summary = "Get all pending lecturers (admin)", description = "Retrieves all pending lecturers. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "List of pending lecturers", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Approve lecturer (admin)", description = "Approves a lecturer. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer approved", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Reject lecturer (admin)", description = "Rejects a lecturer. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer rejected", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Search lecturers by name", description = "Searches for lecturers by name. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Lecturer(s) found", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecturer not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/search/name/{name}")
    public ResponseEntity<?> searchLecturersByName(@PathVariable String name) {
        return searchLecturersByName(name, false);
    }

    @Operation(summary = "Search lecturers by name (admin)", description = "Searches for lecturers by name for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer(s) found", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecturer not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Delete lecturer (admin)", description = "Deletes a lecturer by admin. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer deleted", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/admin/del/{userId}")
    // @PreAuthorize("hasRole('ADMIN')") // endpoint for admin to delete any lecturer
    public ResponseEntity<?> deleteLecturer(@PathVariable UUID userId) {
        return deleteLecturer(userId, true);
    }

    @Operation(summary = "Delete own lecturer profile", description = "Deletes the authenticated lecturer's own profile. Access: Only users with roles LECTURER or ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer deleted", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @Operation(summary = "Get paginated lecturers", description = "Returns a paginated list of approved lecturers. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Paginated lecturers", content = @Content(schema = @Schema(implementation = PaginatedResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedLecturers(@RequestBody PaginationRequest request) {
        boolean isAdmin = false; // Default to false for public access
        return getPaginatedLecturers(request, isAdmin);
    }


    @Operation(summary = "Get paginated lecturers (admin)", description = "Returns a paginated list of lecturers for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Paginated lecturers", content = @Content(schema = @Schema(implementation = PaginatedResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/admin/paginated")
    public ResponseEntity<?> getPaginatedLecturersAdmin(@RequestBody PaginationRequest request) {
        boolean isAdmin = true;
        return getPaginatedLecturers(request, isAdmin);
    } 

    private ResponseEntity<?> getPaginatedLecturers(PaginationRequest request, boolean isAdmin) {
        try {
            PaginatedResponseDTO<ResponseLecturerDTO> paginated = lecturerService.getPaginatedLecturers(request.getPageNum(), request.getPageSize(), isAdmin);
            return ResponseEntity.ok(paginated);
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching paginated lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Search lectures and lecturers by name", description = "Searches for lectures and lecturers by name. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Search results", content = @Content(schema = @Schema(implementation = SearchResultDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
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

    @PatchMapping("/update")
    @Operation(summary = "Update lecturer profile", description = "Updates the authenticated lecturer's profile. Access: Only users with roles LECTURER or ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecturer profile updated", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> updateLecturer(@RequestHeader("Authorization") String token, @Valid @RequestBody UpdateLecturerDTO updateData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.ok(lecturerService.updateLecturer(userId, updateData));
        } catch (LecturerException e) {
            // logger.error("LecturerException while updating lecturer profile", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while updating lecturer profile", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecturer profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/filter") // how such URL should look like? /lecturers/filter?targetAudience=...&topic=...&workingArea=...&rank=...
    @Operation(summary = "Filter lecturers by Target audience/Topic/Working area/Rank(future feature)", 
               description = "Access: Public (no authentication required). Example URL: /lecturers/filter?targetAudiences=1,2&topics=3,4&workingAreas=NORTH,CENTER")
    @ApiResponse(responseCode = "200", description = "Filtered lecturers", content = @Content(schema = @Schema(implementation = ResponseLecturerDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> filterLecturers(@RequestParam(required = false) List<Long> targetAudiences,
                                             @RequestParam(required = false) List<Long> topics,
                                             @RequestParam(required = false) List<Area> workingAreas){
                                            //  @RequestParam(required = false) Double rank) {
        try {
            return ResponseEntity.ok(lecturerService.filterLecturers(targetAudiences, topics, workingAreas));
        } catch (LecturerException e) {
            // logger.error("LecturerException while filtering lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while filtering lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());    
        }
    }

    @GetMapping("paginated/filter")
    @Operation(summary = "Get paginated filtered lecturers", description = "Returns a paginated list of filtered lecturers. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Paginated filtered lecturers", content = @Content(schema = @Schema(implementation = PaginatedResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> getPaginatedFilteredLecturers(@RequestBody PaginationRequest request,
                                                           @RequestParam(required = false) List<Long> targetAudiences,
                                                           @RequestParam(required = false) List<Long> topics,
                                                           @RequestParam(required = false) List<Area> workingAreas) {
        try {
            PaginatedResponseDTO<ResponseLecturerDTO> paginated = lecturerService.filterLecturersPaginated(
                request.getPageNum(), request.getPageSize(), targetAudiences, topics, workingAreas);
            return ResponseEntity.ok(paginated);
        } catch (LecturerException e) {
            // logger.error("LecturerException while fetching paginated filtered lecturers", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching paginated filtered lecturers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}