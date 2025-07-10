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
    private final MailService mailService;
    private final PasswordResetRedisService passwordResetRedisService;

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

    public Role getUserRole(UUID userId) {
        return userRepository.getRoleByUserId(userId)
            .orElseThrow(() -> new AuthException("User not found or role not assigned"));
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

    public String logout(String accessToken, String refreshToken) {
        // Blacklist the access token
        String jti = jwtService.extractJti(accessToken);
        long expirationMillis = jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
        refreshTokenService.blacklistToken(jti, expirationMillis);
        // Revoke the refresh token
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
    
    public String forgotPass(String userEmail){
        if(!userRepository.existsByEmail(userEmail)){
            return "If your email exists, you will receive a reset token by email.";
        }
        long tokenExpireTime = System.currentTimeMillis() + 300000; // 5 min expiration time
        String resetToken = jwtService.generateResetToken(tokenExpireTime);
        // Store the reset token in Redis
        passwordResetRedisService.storeToken(resetToken, userEmail, tokenExpireTime);
        // Compose email
        String subject = "Password Reset Request";
        String text = "You requested a password reset.\n" +
                "Use the following token to reset your password (valid for 5 minutes):\n\n" +
                resetToken +
                "\n\nIf you did not request this, please ignore this email.";
        mailService.sendMail(userEmail, subject, text);
        return "If your email exists, you will receive a reset token by email.";
    }

    public String resetPassToken(String token, String newPass){
        if(jwtService.isTokenExpired(token)){
            throw new AuthException("Token Expired");
        }
        String userEmail = passwordResetRedisService.getUserEmailByToken(token);
        if (userEmail == null) {
            throw new AuthException("Token Invalid");
        }
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new AuthException("User not found"));
        if(!user.updatePass(newPass, passwordEncoder)){
            throw new AuthException("You’ve already used this password. Please create a new password.");
        }
        userRepository.save(user);
        passwordResetRedisService.deleteToken(token); // Remove token after use
        return "Password Changed Successfully";
    }

    public String resetPass(String token, String oldPass, String newPass) {
        UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        User requestingUser = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));
        if (!passwordEncoder.matches(oldPass, requestingUser.getPassword())) {
            throw new AuthException("Invalid oldPass");
        }
        if(!requestingUser.updatePass(newPass, passwordEncoder)){
            throw new AuthException("You’ve already used this password. Please create a new password.");
        }
        userRepository.save(requestingUser);
        return "Password Changes Succeesfuly";    
    }
    

    public String changeRole(UUID userId, Role newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));

        if (user.getRole() == newRole) {
            throw new AuthException("User already has this role");
        }

        user.setRole(newRole);
        userRepository.save(user);
        return "User role changed successfully";
    }

    public String deleteUser(String token) {
        // Blacklist the access token
        String jti = jwtService.extractJti(token.replace("Bearer ", ""));
        long expirationMillis = jwtService.extractExpiration(token.replace("Bearer ", "")).getTime() - System.currentTimeMillis();
        refreshTokenService.blacklistToken(jti, expirationMillis);
        UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));
        if (user.isAdmin()) {
            throw new AuthException("Cannot delete admin user");
        }
        if (user.isLecturer()){
            throw new AuthException("Error: Cannot delete a lecturer user. Please remove the lecturer profile first.");
        }
      
        // Delete the user
        userRepository.delete(user);
        return "User deleted successfully";
    }

    public String deleteUserByAdmin(UUID userId){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));
            
        if (user.isLecturer()){
            throw new AuthException("Error: Cannot delete a lecturer user. Please remove the lecturer profile first.");
        }
      
        // Delete the user
        userRepository.delete(user);
        return "User deleted successfully";
    }
}
