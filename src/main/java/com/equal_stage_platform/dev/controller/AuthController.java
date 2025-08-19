package com.equal_stage_platform.dev.controller;

import com.equal_stage_platform.dev.dto.RegisterRequest;
import com.equal_stage_platform.dev.dto.ResetPassDTO;
import com.equal_stage_platform.dev.dto.LoginRequest;
import com.equal_stage_platform.dev.dto.PassDTO;
import com.equal_stage_platform.dev.dto.CreateAdminRequest;
import com.equal_stage_platform.dev.dto.ForgotPassDTO;
import com.equal_stage_platform.dev.exception.AuthException;
import com.equal_stage_platform.dev.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    // function without endpoint - just for inner use validation
    public String deleteAccountByAdmin(String email){
        return authService.deleteAccountByAdmin(email);
    }
    
    @Operation(summary = "Register a new user", description = "Registers a new user. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "409", description = "Conflict - user already exists", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String result = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns tokens. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.login(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "Refresh token", description = "Refreshes the authentication token. Access: USER, ADMIN, or LECTURER.")
    @ApiResponse(responseCode = "200", description = "Token refreshed", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        try {
            Map<String, String> token = authService.refresh(body.get("refresh"));
            return ResponseEntity.ok(token);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "Logout user", description = "Logs out the user. Access: USER, ADMIN, or LECTURER.")
    @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
            String accessToken = token.replace("Bearer ", "");
            String result = authService.logout(accessToken, body.get("refresh"));
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "Setup first admin", description = "Sets up the first admin user. Access: USER, ADMIN, or LECTURER.")
    @ApiResponse(responseCode = "200", description = "Admin setup successful", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/registerAdmin")
    public ResponseEntity<?> setupFirstAdmin(@RequestHeader("Authorization") String token) {
        try {
            String result = authService.setupFirstAdmin(token);
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "Create admin (admin only)", description = "Creates a new admin user. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Admin created", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/admin/create-admin")
    public ResponseEntity<?> createAdmin(@RequestHeader("Authorization") String token,@Valid @RequestBody CreateAdminRequest createAdminRequest) {
        try {
            String result = authService.createAdmin(token, createAdminRequest.getEmail());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Forgot password", description = "Initiates forgot password process. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Password reset email sent", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/forgot-pass")
    public ResponseEntity<?> forgotPass(@Valid @RequestBody ForgotPassDTO forgotPassRequest){
        try {
            String result = authService.forgotPass(forgotPassRequest.getEmail());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "Reset password with token", description = "Resets password using a token. Access: Public (no authentication required).")
    @ApiResponse(responseCode = "200", description = "Password reset successful", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/reset-pass-token")
    public ResponseEntity<?> resetPassToken(@RequestHeader("token") String token, @Valid @RequestBody PassDTO passDto){
        try{
            String result = authService.resetPassToken(token, passDto.getPass());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "Reset password", description = "Resets password for authenticated user. Access: USER, ADMIN, or LECTURER.")
    @ApiResponse(responseCode = "200", description = "Password reset successful", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request: invalid input data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/reset-pass")
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> resetPass(@RequestHeader("Authorization") String token, @Valid @RequestBody ResetPassDTO resetPassRequest){
        try{
            String result = authService.resetPass(token, resetPassRequest.getOldPassword(), resetPassRequest.getNewPassword());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }

    @Operation(summary = "Delete own account", description = "Deletes the authenticated user's account. Access: USER only.")
    @ApiResponse(responseCode = "200", description = "Account deleted", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/delete-account")
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String token) {
        try {
            String result = authService.deleteUser(token);
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }
    @Operation(summary = "Delete account by admin", description = "Deletes a user account by admin. Access: Only users with role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Account deleted", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/admin/delete-account/{userId}")
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAccountByAdmin(@PathVariable UUID userId) {
        try {
            String result = authService.deleteUserByAdmin(userId);
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }
}
