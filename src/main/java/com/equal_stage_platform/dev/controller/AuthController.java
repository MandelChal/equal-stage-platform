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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            String result = authService.register(registerRequest.getEmail(), registerRequest.getPassword());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, String> tokens = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(tokens);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        try {
            Map<String, String> token = authService.refresh(body.get("refresh"));
            return ResponseEntity.ok(token);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        try {
            String result = authService.logout(body.get("refresh"));
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<?> setupFirstAdmin(@RequestHeader("Authorization") String token) {
        try {
            String result = authService.setupFirstAdmin(token);
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateAdminRequest createAdminRequest) {
        try {
            String result = authService.createAdmin(token, createAdminRequest.getEmail());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/forgot-pass")
    public ResponseEntity<?> forgotPass(@RequestBody ForgotPassDTO forgotPassRequest){
        try {
            String result = authService.forgotPass(forgotPassRequest.getEmail());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/reset-pass-token")
    public ResponseEntity<?> resetPassToken(@RequestHeader String token, @RequestBody PassDTO passDto){
        try{
            String result = authService.resetPassToken(token, passDto.getPass());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PostMapping("/reset-pass")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> resetPass(@RequestHeader String token, @RequestBody ResetPassDTO resetPassRequest){
        try{
            String result = authService.resetPass(token, resetPassRequest.getOldPassword(), resetPassRequest.getNewPassword());
            return ResponseEntity.ok(result);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }
}
