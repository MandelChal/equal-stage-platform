package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.AddHomePageBanner;
import com.equal_stage_platform.dev.dto.HomePageBannerDTO;
import com.equal_stage_platform.dev.service.HomePageService;
import com.equal_stage_platform.dev.exception.HomePageExeption;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/HomePage")
public class HomePageController {
    private final HomePageService homePageService;
    public HomePageController(HomePageService homePageService) {
        this.homePageService = homePageService;
    }
    
    @GetMapping("/banner/urls")
    @Operation(summary = "Get Home Page Banner", description = "Fetches the banner details for the home page. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Banner details fetched successfully", content = @Content(schema = @Schema(implementation = HomePageBannerDTO.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getHomePageBanner() {
        try{
            HomePageBannerDTO bannerDetails = homePageService.getAllBannerUrls();
            return ResponseEntity.ok(bannerDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/banner/url/{url}")
    @Operation(summary = "Delete Banner URL", description = "Deletes a banner URL from the home page. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Banner URL deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Banner URL not found")
    public ResponseEntity<String> deleteBannerUrl(@PathVariable String url) {
        try {
            String response = homePageService.deleteBannerUrl(url);
            return ResponseEntity.ok(response);
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //TODO - Support editing -> @PatchMapping("/admin/banner/url/{url}")

}