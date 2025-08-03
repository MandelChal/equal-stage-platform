package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateTargetAudienceDTO;
import com.equal_stage_platform.dev.dto.UpdateTargetAudienceDTO;
import com.equal_stage_platform.dev.model.TargetAudience;
import com.equal_stage_platform.dev.service.TargetAudienceService;
import com.equal_stage_platform.dev.exception.TargetAudienceException;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/target-audiences")
public class TargetAudienceController {
    private final TargetAudienceService targetAudienceService;

    public TargetAudienceController(TargetAudienceService targetAudienceService) {
        this.targetAudienceService = targetAudienceService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all target audiences", description = "Retrieves all target audiences. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of target audiences", content = @Content(schema = @Schema(implementation = TargetAudience.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllTargetAudiences() {
        try {
            List<TargetAudience> targetAudiences = targetAudienceService.getAllTargetAudiences();
            return ResponseEntity.ok(targetAudiences);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get target audience by ID", description = "Retrieves a target audience by its ID. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Target audience found", content = @Content(schema = @Schema(implementation = TargetAudience.class)))
    @ApiResponse(responseCode = "404", description = "Target audience not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> getTargetAudienceById(@PathVariable Long id) {
        try {
            TargetAudience targetAudience = targetAudienceService.getTargetAudienceById(id);
            return ResponseEntity.ok(targetAudience);
        } catch (TargetAudienceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/create")
    @Operation(summary = "Create a new target audience", description = "Creates a new target audience. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "201", description = "Target audience created successfully", content = @Content(schema = @Schema(implementation = TargetAudience.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "409", description = "Conflict: target audience with this type already exists", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> createTargetAudience(@Valid @RequestBody CreateTargetAudienceDTO targetAudience) {
        try {
            TargetAudience createdTargetAudience = targetAudienceService.createTargetAudience(targetAudience);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTargetAudience);
        } catch (TargetAudienceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/admin/{id}")
    @Operation(summary = "Update target audience", description = "Updates a target audience by ID. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Target audience updated successfully", content = @Content(schema = @Schema(implementation = TargetAudience.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Target audience not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> updateTargetAudience(@PathVariable Long id, @Valid @RequestBody UpdateTargetAudienceDTO targetAudienceDetails) {
        try {
            TargetAudience updatedTargetAudience = targetAudienceService.updateTargetAudience(id, targetAudienceDetails);
            return ResponseEntity.ok(updatedTargetAudience);
        } catch (TargetAudienceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/{id}")
    @Operation(summary = "Delete target audience", description = "Deletes a target audience by ID. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Target audience deleted successfully", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Target audience not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> deleteTargetAudience(@PathVariable Long id) {
        try {
            targetAudienceService.deleteTargetAudience(id);
            return ResponseEntity.ok("Target audience deleted successfully");
        } catch (TargetAudienceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/search/{prefix}")
    @Operation(summary = "Search target audiences by prefix", description = "Searches for target audiences by their type prefix. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of target audiences matching the prefix", content = @Content(schema = @Schema(implementation = TargetAudience.class)))
    @ApiResponse(responseCode = "404", description = "No target audiences found with the given prefix", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> findByPrefixName(@PathVariable String prefix) {
        try {
            List<TargetAudience> targetAudiences = targetAudienceService.findByPrefixName(prefix);
            if (targetAudiences.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No target audiences found with prefix: " + prefix);
            }
            return ResponseEntity.ok(targetAudiences);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}