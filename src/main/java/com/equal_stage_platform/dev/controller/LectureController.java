
package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateLectureDTO;
import com.equal_stage_platform.dev.service.LectureService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.service.JwtService;
import com.equal_stage_platform.dev.exception.LectureException;
import com.equal_stage_platform.dev.exception.AuthException;
import com.equal_stage_platform.dev.dto.PaginatedResponseDTO;
// import com.equal_stage_platform.dev.dto.ApiResponseDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.UpdateLectureDTO;
import com.equal_stage_platform.dev.dto.PaginationRequest;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;
    private final JwtService jwtService;
    // private static final Logger logger = LoggerFactory.getLogger(LectureController.class);
    public LectureController(LectureService lectureService, JwtService jwtService) {
        this.lectureService = lectureService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Create a new lecture", description = "Creates a new lecture for the authenticated lecturer. Access: Only users with roles LECTURER or ADMIN.")
    @ApiResponse(responseCode = "201", description = "Lecture created successfully", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden: trying to create lecture with invalid data or lecturer not exist", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/create")
    // @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')") // Only lecturers and admins can create lectures
    public ResponseEntity<?> createLecture(@RequestHeader("Authorization") String token, @Valid @RequestBody CreateLectureDTO lectureData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            // logger.info("Attempting to create lecture for user ID: {}", userId);
            ResponseEntity<?> response = ResponseEntity.status(HttpStatus.CREATED).body(lectureService.createLecture(userId, lectureData));
            // logger.info("Lecture created successfully for user ID: {}", userId);
            return response;
        } catch (LectureException e) {
            // logger.error("LectureException while creating lecture for user ID: {}", jwtService.extractUserId(token.replace("Bearer ", "")), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while creating lecture for user ID: {}", jwtService.extractUserId(token.replace("Bearer ", "")), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while creating lecture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Update lecture status", description = "Updates the status of a lecture by ID for the authenticated lecturer. Access: Only users with roles LECTURER or ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecture status updated", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PatchMapping("/update/{lectureId}/status/{status}")
    // @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')") // Only lecturers and admins can create lectures
    public ResponseEntity<?> updateLectureStatus(@RequestHeader("Authorization") String token, @PathVariable Long lectureId, @PathVariable LectureStatus status) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.ok(lectureService.updateLectureStatus(userId, lectureId, status));
        } catch (LectureException e) {
            // logger.error("LectureException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Get lecture by ID", description = "Retrieves a lecture by its ID. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Lecture found", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecture not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/{lectureId}")
    public ResponseEntity<?> getLectureById(@PathVariable Long lectureId) {
        return getLectureByIdAdmin(lectureId, false);
    }

    @Operation(summary = "Get lecture by ID (admin)", description = "Retrieves a lecture by its ID for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecture found", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecture not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/admin/{lectureId}")
    public ResponseEntity<?> getLectureByIdAdmin(@PathVariable Long lectureId) {
        return getLectureByIdAdmin(lectureId, true);
    }

    private ResponseEntity<?> getLectureByIdAdmin(Long lectureId, boolean isAdmin) {
        try {
            return ResponseEntity.ok(lectureService.getLectureById(lectureId, isAdmin));
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Get online lectures", description = "Retrieves all online lectures. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of online lectures", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "No online lectures found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/all/isOnline")
    public ResponseEntity<?> getOnlineLectures() {
        try {
            return ResponseEntity.ok(lectureService.getAllOnlineLectures());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Get top lectures", description = "Retrieves a list of top lectures by count. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of top lectures", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "No top lectures found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/topLectures/{count}")
    public ResponseEntity<?> getTopLectures(@PathVariable int count) {
        try {
            return ResponseEntity.ok(lectureService.getRandomLecturesByStatus(LectureStatus.ON_AIR, count));
        } catch (LectureException e) {
            // logger.error("LectureException while fetching top lectures", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching top lectures", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Search lecture by title", description = "Searches for lectures by title. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Lecture(s) found", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecture not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/search/{lectureTitle}")
    public ResponseEntity<?> searchLectureByTitle(@PathVariable String lectureTitle) {
        return searchLectureByTitle(lectureTitle, false);
    }

    @Operation(summary = "Search lecture by title (admin)", description = "Searches for lectures by title for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecture(s) found", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecture not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/admin/search/{lectureTitle}")
    public ResponseEntity<?> searchLectureByTitleAdmin(@PathVariable String lectureTitle) {
        return searchLectureByTitle(lectureTitle, true);
    }

    private ResponseEntity<?> searchLectureByTitle(String lectureTitle, boolean isAdmin) {
        try {
            return ResponseEntity.ok(lectureService.getLectureByTitle(lectureTitle, isAdmin));
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Get physical lectures", description = "Retrieves all physical lectures. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of physical lectures", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "No physical lectures found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/physical")
    public ResponseEntity<?> getPhysicalLectures() {
        try {
            return ResponseEntity.ok(lectureService.getPhysicalLectures());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching lecture by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    
    @Operation(summary = "Get paginated lectures", description = "Returns a paginated list of lectures based on the provided pagination parameters. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Paginated lectures - Only lectures that has approved lecturers and status ON_AIR",content = @Content(mediaType = "application/json",schema = @Schema(implementation = PaginatedResponseDTO.class)))
    @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content(mediaType = "application/json",schema = @Schema(implementation = String.class)))
    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedLectures(@RequestBody PaginationRequest paginationRequest) {
        boolean isAdmin = false; // Default to false for public access
        return getPaginatedLectures(paginationRequest, isAdmin);
    }

    @Operation(summary = "Get all lectures for admin", description = "Returns a paginated list of all lectures for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Paginated lectures - all lectures", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/admin/paginated")
    // @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<?> getPaginatedLecturesForAdmin(@RequestBody PaginationRequest paginationRequest) {
        boolean isAdmin = true;
        return getPaginatedLectures(paginationRequest, isAdmin);
    }

    private ResponseEntity<?> getPaginatedLectures(PaginationRequest paginationRequest, boolean isAdmin) {
        try {
            PaginatedResponseDTO<ResponseLectureDTO> paginated = lectureService.getPaginatedLectures(paginationRequest.getPageNum(), paginationRequest.getPageSize(), isAdmin);
            return ResponseEntity.ok(paginated);
        } catch (LectureException e) {
            // logger.error("LectureException while fetching paginated lectures", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching paginated lectures", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Delete a lecture", description = "Deletes a lecture by ID for the authenticated lecturer or admin. Access: Only users with roles LECTURER or ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecture deleted", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/del/{lectureId}")
    // @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')") // Only lecturers and admins can delete lectures
    public ResponseEntity<?> deleteLecture(@RequestHeader("Authorization") String token, @PathVariable Long lectureId) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.ok(lectureService.deleteLecture(userId, lectureId)); 
        } catch (LectureException e) {
            // logger.error("LectureException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecture status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/admin/pending")
    @Operation(summary = "Get pending lectures", description = "Retrieves all pending lectures for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "List of pending lectures", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "No pending lectures found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> getPendingLectures() {
        try {
            return ResponseEntity.ok(lectureService.getPendingLectures());
        } catch (LectureException e) {
            // logger.error("LectureException while fetching pending lectures", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while fetching pending lectures", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PatchMapping("/admin/approve/{lectureId}")
    @Operation(summary = "Approve a lecture", description = "Approves a lecture by ID for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecture approved", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecture not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> approveLecture(@PathVariable Long lectureId, @RequestBody Map<String, String> requestBody) {
        try {
            boolean isApproved = true;
            String note = requestBody.get("note");
            return ResponseEntity.ok(lectureService.setApproveLecture(lectureId, isApproved, note));
        } catch (LectureException e) {
            // logger.error("LectureException while approving lecture", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while approving lecture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PatchMapping("/admin/reject/{lectureId}")
    @Operation(summary = "Reject a lecture", description = "Rejects a lecture by ID for admin users. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecture rejected", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "404", description = "Lecture not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> rejectLecture(@PathVariable Long lectureId, @RequestBody Map<String, String> requestBody) {
        try {
            boolean isApproved = false;
            String note = requestBody.get("note");
            return ResponseEntity.ok(lectureService.setApproveLecture(lectureId, isApproved, note));
        } catch (LectureException e) {
            // logger.error("LectureException while rejecting lecture", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while rejecting lecture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PatchMapping("/update/{lectureId}")
    @Operation(summary = "Update lecture details", description = "Updates the details of a lecture by ID for the authenticated lecturer. Access: Only users with roles LECTURER or ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lecture updated successfully", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden: trying to update lecture of another lecturer", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> updateLecture(@RequestHeader("Authorization") String token, @PathVariable Long lectureId, @Valid @RequestBody UpdateLectureDTO lectureData) {
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            // logger.info("Attempting to update lecture for user ID: {}", userId);
            ResponseEntity<?> response = ResponseEntity.ok(lectureService.updateLecture(userId, lectureId, lectureData));
            // logger.info("Lecture updated successfully for user ID: {}", userId);
            return response;
        } catch (LectureException e) {
            // logger.error("LectureException while updating lecture for user ID: {}", jwtService.extractUserId(token.replace("Bearer ", "")), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (AuthException e) {
            // logger.error("AuthException while updating lecture for user ID: {}", jwtService.extractUserId(token.replace("Bearer ", "")), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while updating lecture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filter lectures by Target audiences/Topics/Working areas/Min Rank/Max Rank", 
               description = "Access: Public (no authentication required). Example URL: /lectures/filter?targetAudiences=1,2&topics=3,4&workingAreas=NORTH,CENTER&minRank=1.5&maxRank=5.0")
    @ApiResponse(responseCode = "200", description = "Filtered lectures", content = @Content(schema = @Schema(implementation = ResponseLectureDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> filterLectures(@RequestParam(required = false) List<Long> targetAudiences,
                                            @RequestParam(required = false) List<Long> topics,
                                            @RequestParam(required = false) List<Area> workingAreas,
                                            @RequestParam(required = false) Double minRank,
                                            @RequestParam(required = false) Double maxRank) {
        try {
            return ResponseEntity.ok(lectureService.filterLectures(targetAudiences, topics, workingAreas, minRank, maxRank));
        } catch (LectureException e) {
            // logger.error("LectureException while filtering lectures", e);
            if (e.getMessage().contains("price")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid price range: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while filtering lectures", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/paginated/filter") 
    @Operation(summary = "Get paginated filtered lectures by Target audiences/Topics/Working areas/Min Rank/Max Rank", 
               description = "Returns a paginated list of filtered lectures based on the provided parameters. Access: Public (no authentication required). Example URL: /lectures/paginated/filter?targetAudiences=1,2&topics=3,4&workingAreas=NORTH,CENTER&minRank=1.5&maxRank=5.0")
    @ApiResponse(responseCode = "200", description = "Paginated filtered lectures", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> getPaginatedFilteredLectures(@RequestBody PaginationRequest paginationRequest,
                                                          @RequestParam(required = false) List<Long> targetAudiences,
                                                          @RequestParam(required = false) List<Long> topics,
                                                          @RequestParam(required = false) List<Area> workingAreas,
                                                          @RequestParam(required = false) Double minRank,
                                                          @RequestParam(required = false) Double maxRank) {
        try {
            PaginatedResponseDTO<ResponseLectureDTO> paginated = lectureService.filterLecturesPageable(
                paginationRequest.getPageNum(), 
                paginationRequest.getPageSize(),
                targetAudiences, 
                topics,
                workingAreas,
                minRank,
                maxRank
            );
            return ResponseEntity.ok(paginated);
        } catch (LectureException e) {
            // logger.error("LectureException while fetching paginated filtered lectures", e);
            if (e.getMessage().contains("price")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid price range: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // logger.error("Unexpected error while filtering lectures", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}


