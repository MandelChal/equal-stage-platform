package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.AddHomePageBanner;
import com.equal_stage_platform.dev.dto.UpdateHomePageBannerDTO;
import com.equal_stage_platform.dev.model.HomePageBanner;
import com.equal_stage_platform.dev.service.HomePageService;
import com.equal_stage_platform.dev.exception.HomePageExeption;

import java.util.List;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/HomePage")
public class HomePageController {
    private final HomePageService homePageService;
    public HomePageController(HomePageService homePageService) {
        this.homePageService = homePageService;
    }
    
    @GetMapping("/banner/urls")
    @Operation(summary = "Get Home Page Banner", description = "Fetches the banner details for the home page sorted by display order. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Banner details fetched successfully", content = @Content(schema = @Schema(implementation = HomePageBanner.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getHomePageBanner() {
        try{
            return ResponseEntity.ok(homePageService.getAllBannerUrls());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/admin/banner/url")
    @Operation(summary = "Add Banner URL", description = "Adds a new banner URL to the home page. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Banner URL added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<String> addBannerUrl(@Valid @RequestBody AddHomePageBanner bannerObject){
        try {
            String response = homePageService.addBannerUrl(bannerObject);
            return ResponseEntity.ok(response);
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/admin/banner/url/{id}")
    @Operation(summary = "Delete Banner URL", description = "Deletes a banner URL from the home page. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Banner URL deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Banner URL not found")
    public ResponseEntity<String> deleteBannerUrl(@PathVariable Long id) {
        try {
            String response = homePageService.deleteBannerUrl(id);
            return ResponseEntity.ok(response);
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/admin/banner/urls")
    @Operation(summary = "Edit Banner URLs", description = "Edits multiple banner URLs on the home page. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Banner URLs updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Banner not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<String> editBannerUrls(@Valid @RequestBody List<UpdateHomePageBannerDTO> updateList) {
        try {
            String response = homePageService.editBannerUrls(updateList);
            return ResponseEntity.ok(response);
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

}