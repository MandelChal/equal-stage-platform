package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.CreateTopicDTO;
import com.equal_stage_platform.dev.dto.UpdateTopicDTO;
import com.equal_stage_platform.dev.exception.TopicException;
import com.equal_stage_platform.dev.model.LectureTopic;
import com.equal_stage_platform.dev.service.LectureTopicService;

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
@RequestMapping("/lecture-topics")
public class LectureTopicController {
    private final LectureTopicService lectureTopicService;

    public LectureTopicController(LectureTopicService lectureTopicService) {
        this.lectureTopicService = lectureTopicService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all topics", description = "Retrieves all topics. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of topics", content = @Content(schema = @Schema(implementation = LectureTopic.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllTopics() {
        try {
            List<LectureTopic> topics = lectureTopicService.getAllTopics();
            return ResponseEntity.ok(topics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic by ID", description = "Retrieves a topic by its ID. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Topic found", content = @Content(schema = @Schema(implementation = LectureTopic.class)))
    @ApiResponse(responseCode = "404", description = "Topic not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> getTopicById(@PathVariable Long id) {
        try {
            LectureTopic topic = lectureTopicService.getTopicById(id);
            return ResponseEntity.ok(topic);
        } catch (TopicException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/admin/create")
    @Operation(summary = "Create a new topic", description = "Creates a new topic. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "201", description = "Topic created successfully", content = @Content(schema = @Schema(implementation = LectureTopic.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "409", description = "Conflict: topic with the same name already exists", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> createTopic(@Valid @RequestBody CreateTopicDTO topic) {
        try {
            LectureTopic createdTopic = lectureTopicService.createTopic(topic);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTopic);
        } catch (TopicException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PatchMapping("/admin/{id}")
    @Operation(summary = "Update topic", description = "Updates a topic by ID. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Topic updated successfully", content = @Content(schema = @Schema(implementation = LectureTopic.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Topic not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> updateTopic(@PathVariable Long id, @Valid @RequestBody UpdateTopicDTO topicDetails) {
        try {
            LectureTopic updatedTopic = lectureTopicService.updateTopic(id, topicDetails);
            return ResponseEntity.ok(updatedTopic);
        } catch (TopicException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/admin/{id}")
    @Operation(summary = "Delete topic", description = "Deletes a topic by ID. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Topic deleted successfully", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Topic not found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        try {
            lectureTopicService.deleteTopic(id);
            return ResponseEntity.ok("Topic deleted successfully");
        } catch (TopicException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/search/{prefix}")
    @Operation(summary = "Search topics by prefix", description = "Searches for topics by name starting with a given prefix. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "List of topics found", content = @Content(schema = @Schema(implementation = LectureTopic.class)))
    @ApiResponse(responseCode = "404", description = "No topics found", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> findByPrefixName(@PathVariable String prefix) {
        try {
            List<LectureTopic> topics = lectureTopicService.findByPrefixName(prefix);
            if (topics.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No topics found with prefix: " + prefix);
            }
            return ResponseEntity.ok(topics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}