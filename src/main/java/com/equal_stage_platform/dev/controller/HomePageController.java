package com.equal_stage_platform.dev.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equal_stage_platform.dev.dto.AddHomePageBanner;
import com.equal_stage_platform.dev.dto.UpdateHomePageBannerDTO;
import com.equal_stage_platform.dev.dto.AboutUsDTOs.*;
import com.equal_stage_platform.dev.dto.ContactUsDTOs.*;
import com.equal_stage_platform.dev.model.HomePageBanner;
import com.equal_stage_platform.dev.model.AboutUs;
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
    @ApiResponse(responseCode = "200", description = "Banner details fetched successfully", content = @Content(schema = @Schema(implementation = HomePageBanner[].class)))
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
    public ResponseEntity<?> addBannerUrl(@Valid @RequestBody AddHomePageBanner bannerObject){
        try {
            return ResponseEntity.ok(homePageService.addBannerUrl(bannerObject));
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
    public ResponseEntity<?> deleteBannerUrl(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(homePageService.deleteBannerUrl(id));
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
    public ResponseEntity<?> editBannerUrls(@Valid @RequestBody List<UpdateHomePageBannerDTO> updateList) {
        try {
            return ResponseEntity.ok(homePageService.editBannerUrls(updateList));
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/admin/banner/urls/reorder")
    @Operation(summary = "Reorder Banner URLs", description = "Reorders the banner URLs on the home page. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "Banner URLs reordered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> reOrderBanners(@Valid @RequestBody List<Integer> newPositions) {
        try {
            return ResponseEntity.ok(homePageService.reOrderBanners(newPositions));
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // @GetMapping("/all")
    // @Operation(summary = "Get All Home Page Data", description = "Fetches all the data for the home page. Access: Public (no authentication required).")
    // @ApiResponse(responseCode = "200", description = "Home page data fetched successfully", content = @Content(schema = @Schema(implementation = HomePageData.class)))
    // @ApiResponse(responseCode = "500", description = "Internal server error")
    // public ResponseEntity<?> getAllHomePageData() {
    //     return ResponseEntity.ok(homePageService.getAllHomePageData());
    // }

    // @GetMapping("/customer_recommendations")
    // @Operation(summary = "Get Customer Recommendations", description = "Fetches the customer recommendations for the home page. Access: Public (no authentication required).")
    // @ApiResponse(responseCode = "200", description = "Customer recommendations fetched successfully", content = @Content(schema = @Schema(implementation = CustomerRecommendation.class)))
    // @ApiResponse(responseCode = "500", description = "Internal server error")
    // public ResponseEntity<?> getCustomerRecommendations() {
    //     return ResponseEntity.ok(homePageService.getCustomerRecommendations());
    // }

    @GetMapping("/about_us")
    @Operation(summary = "Get About Us", description = "Fetches the about us data for the home page. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "About us data fetched successfully", content = @Content(schema = @Schema(implementation = AboutUs.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAboutUs() {
        try {
            return ResponseEntity.ok(homePageService.getAboutUs());
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/admin/about_us")
    @Operation(summary = "Add About Us", description = "Adds the about us data for the home page. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "About us data added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> addAboutUs(@Valid @RequestBody AboutUsDTO aboutUsDTO) {
        try {
            return ResponseEntity.ok(homePageService.addAboutUs(aboutUsDTO));
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/admin/about_us")
    @Operation(summary = "Edit About Us", description = "Edits the about us data for the home page. Access: Admin (requires authentication).")
    @ApiResponse(responseCode = "200", description = "About us data edited successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> editAboutUs(@Valid @RequestBody UpdateAboutUsDTO updateAboutUsDTO) {
        try {
            return ResponseEntity.ok(homePageService.editAboutUs(updateAboutUsDTO));
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/contact_us")
    @Operation(summary = "Contact Us", description = "Sends a contact us message. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Contact us message sent successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<String> contactUs(@Valid @RequestBody ContactUsDTO contactUsDTO) {
        try {
            return ResponseEntity.ok(homePageService.contactUs(contactUsDTO));
        } catch (HomePageExeption e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

}