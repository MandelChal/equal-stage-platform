package com.equal_stage_platform.dev.service;

import com.equal_stage_platform.dev.exception.AuthException;
import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.UserStatus;
import com.equal_stage_platform.dev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthException("Email is taken");
        }

        User user = new User(email, passwordEncoder.encode(password));

        userRepository.save(user);
        return "User registered successfully";
    }

    public Map<String, String> login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user.getUserId());
        return Map.of("token", accessToken, "refresh", refreshToken);
    }

    public Map<String, String> refresh(String refreshToken) {
        if (!refreshTokenService.isValid(refreshToken)) {
            throw new AuthException("Invalid refresh token");
        }

        UUID userId = refreshTokenService.getUserId(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));

        String newAccessToken = jwtService.generateToken(user);
        return Map.of("token", newAccessToken);
    }

    public String logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
        return "Logged out successfully";
    }

    public String setupFirstAdmin(String token) {
        if (userRepository.findByRole(Role.ADMIN).isPresent()) {
            throw new AuthException("Admin already exists");
        }

        UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new AuthException("User is already an admin");
        }

        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return "Admin user created successfully";
    }

    public String createAdmin(String token, String newAdminEmail) {
        UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        User requestingUser = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));

        if (requestingUser.getRole() != Role.ADMIN) {
            throw new AuthException("Only admins can create new admin users");
        }

        if (requestingUser.getStatus() != UserStatus.ACTIVE) {
            throw new AuthException("Requesting user is not active");
        }

        User newAdmin = userRepository.findByEmail(newAdminEmail)
            .orElseThrow(() -> new AuthException("User not found"));

        if (newAdmin.getRole() == Role.ADMIN) {
            throw new AuthException("User is already an admin");
        }

        newAdmin.setRole(Role.ADMIN);
        userRepository.save(newAdmin);
        return "New admin user created successfully";
    }
}
