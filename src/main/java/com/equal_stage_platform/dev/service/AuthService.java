package com.equal_stage_platform.dev.service;

import com.equal_stage_platform.dev.dto.CompleteRegistrationRequestDTO;
import com.equal_stage_platform.dev.dto.RegisterRequest;
import com.equal_stage_platform.dev.dto.ResponseLoginDTO;
import com.equal_stage_platform.dev.exception.AuthException;
import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.model.enums.UserStatus;
import com.equal_stage_platform.dev.repository.UserRepository;


import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.Set;

@Service
@RequiredArgsConstructor
// @Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final MailService mailService;
    private final PasswordResetRedisService passwordResetRedisService;
    private final LecturerService lecturerService;
    private final UserRoleManagementService userRoleManagementService;
    @Value("${google.client-id}")
    private String googleClientId;

 // function without endpoint - just for inner use validation
    public String deleteAccountByAdmin(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new AuthException("User not found with email :" + email));
        userRepository.delete(user);
        return "User deleted successfully";
    }

    @Transactional
    public String register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new AuthException("Email is taken");
        }

        // encrypt password in the request object
        encryptPassword(registerRequest);
        // create user object
        User user = new User(registerRequest);

        userRepository.save(user);
        return "User registered successfully";
    }

    private void encryptPassword(RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    }

    @Transactional
    public String completeRegistration(String token, CompleteRegistrationRequestDTO completeRegistrationRequest) {
        UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
        if(user.isRegistrationCompleted()){
            throw new AuthException("Registration already completed");
        }
        user.completeRegistration(completeRegistrationRequest.getFirstName(), completeRegistrationRequest.getLastName(), completeRegistrationRequest.getPhone());
        userRepository.save(user);
        return "Registration completed successfully";
    }

    public ResponseLoginDTO loginWithGoogle(String idToken) {
        try{
            var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            var jsonFactory = GsonFactory.getDefaultInstance();
            var googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if(googleIdToken == null){
                throw new AuthException("Invalid Google ID token");
            }
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String googleId = payload.getSubject();
            User user = userRepository.findByGoogleId(googleId).orElse(null);
            if(user == null){ // user not found - need to register
                user = new User(email, googleId);
                userRepository.save(user);
            }
            return enrollUserSession(user);
        }catch(GeneralSecurityException e){
            throw new AuthException("Invalid Google ID token");
        }catch(AuthException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private ResponseLoginDTO enrollUserSession(User user){
        String accessToken = jwtService.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();
        redisTokenService.enrollUserSession(user.getUserId(), accessToken, refreshToken);
        return new ResponseLoginDTO(accessToken, refreshToken, user.getRoles(), user.isRegistrationCompleted());
    }

    @Transactional(readOnly = true)
    public ResponseLoginDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        return enrollUserSession(user);
    }

    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(UUID userId) {
        return userRepository.getRolesByUserId(userId)
            .orElseThrow(() -> new AuthException("User not found"));
    }

    @Transactional(readOnly = true)
    public Map<String, String> refresh(String refreshToken) {
        if (!redisTokenService.isValid(refreshToken)) {
            throw new AuthException("Invalid refresh token");
        }

        UUID userId = redisTokenService.getUserId(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));

        String newAccessToken = jwtService.generateToken(user);
        redisTokenService.updateAccessToken(userId, newAccessToken);
        return Map.of("token", newAccessToken);
    }

    @Transactional(readOnly = true)
    public String logout(String accessToken, String refreshToken) {
        // Blacklist the access token
        blacklistUserSession(accessToken);
        // Revoke the refresh token
        redisTokenService.revokeToken(refreshToken);
        return "Logged out successfully";
    }

    @Transactional
    public String setupSuperAdmin(String token) {
        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            throw new AuthException("Super Admin already exists");
        }

        UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));

        if (user.isSuperAdmin()) {
            throw new AuthException("User is already a super admin");
        }

        user.addRole(Role.SUPER_ADMIN);
        userRepository.save(user);
        return "Super Admin user created successfully";
    }

    @Transactional
    public String createAdmin(String token, String newAdminEmail) {
        UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        User requestingUser = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));

        if (!requestingUser.isSuperAdmin()) {
            throw new AuthException("Only super admins can create new admin users");
        }

        if (requestingUser.getStatus() != UserStatus.ACTIVE) {
            throw new AuthException("Requesting user is not active");
        }

        User newAdmin = userRepository.findByEmail(newAdminEmail)
            .orElseThrow(() -> new AuthException("User not found"));

        if (newAdmin.isAdmin()) {
            throw new AuthException("User is already an admin");
        }
        newAdmin.addRole(Role.ADMIN);
        userRepository.save(newAdmin);
        return "New admin user created successfully";
    }
    
    @Transactional(readOnly = true)
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

    @Transactional
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

    @Transactional
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
    
    // @Transactional
    // public String changeRole(UUID userId, Role newRole) {
    //     User user = userRepository.findById(userId)
    //         .orElseThrow(() -> new AuthException("User not found"));

    //     if (user.getRole() == newRole) {
    //         throw new AuthException("User already has this role");
    //     }

    //     user.setRole(newRole);
    //     userRepository.save(user);
    //     return "User role changed successfully";
    // }

    @Transactional
    public String deleteClientAccount(String accessToken) {
        UUID userId = jwtService.extractUserId(accessToken.replace("Bearer ", ""));
        Set<Role> roles = getUserRoles(userId);
        if(!roles.contains(Role.CLIENT)){
            throw new AuthException("Error: User with userId: " + userId + " is not a client");
        }
        if(roles.contains(Role.SUPER_ADMIN)){
            throw new AuthException("Error: Cannot delete Super Admin user");
        }
        if (roles.contains(Role.ADMIN)) {
            throw new AuthException("Error: Cannot delete an Admin user. Please contact Super Admin and request to delete the admin profile first.");
        }
        if (roles.contains(Role.LECTURER)){
            throw new AuthException("Error: Cannot delete a lecturer user. Please remove the lecturer profile first.");
        }
        // Blacklist the access token
        blacklistUserSession(accessToken);
        // Delete the user
        userRepository.deleteById(userId);
        return "User deleted successfully";
    }

    @Transactional
    public String deleteClientAccountBySuperAdmin(UUID userId){
        Set<Role> roles = getUserRoles(userId);
        if(roles.contains(Role.SUPER_ADMIN))
            throw new AuthException("Error: Cannot delete Super Admin user");
        if(roles.contains(Role.ADMIN))
            throw new AuthException("Error: Cannot delete an Admin user. Please delete the admin profile first.");
        if(roles.contains(Role.LECTURER))
            throw new AuthException("Error: Cannot delete a lecturer user. Please delete the lecturer profile first.");
        if(roles.contains(Role.CLIENT))
            userRepository.deleteById(userId);
        blacklistUserSession(redisTokenService.getAccessToken(userId));
        userRepository.deleteById(userId);
        return "User deleted successfully";
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException("User with email: " + email + " not found"));
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User with userId: " + userId + " not found"));
    }

    @Transactional
    public String deleteAdminProfileBySuperAdmin(UUID userId) {
        if(userRoleManagementService.hasRole(userId, Role.SUPER_ADMIN)){
            throw new AuthException("Error: Cannot delete Super Admin user");
        }
        if(!userRoleManagementService.hasRole(userId, Role.ADMIN)){
            throw new AuthException("Error: User with userId: " + userId + " is not an admin");
        }
        userRoleManagementService.removeRole(userId, Role.ADMIN);
        return "Admin profile deleted successfully";
    }

    @Transactional
    public String deleteUserBySuperAdmin(UUID userId) {
        Set<Role> roles = getUserRoles(userId);
        if(roles.contains(Role.SUPER_ADMIN))
            throw new AuthException("Error: Cannot delete Super Admin user");
        if(roles.contains(Role.ADMIN))
            userRoleManagementService.removeRole(userId, Role.ADMIN);
        if(roles.contains(Role.LECTURER))
            lecturerService.deleteLecturer(userId);
        if(roles.contains(Role.CLIENT))
            userRepository.deleteById(userId);
        blacklistUserSession(redisTokenService.getAccessToken(userId));
        return "All profiles deleted successfully";
    }

    private void blacklistUserSession(String accessToken){
        if (accessToken == null)
            return;
        String jti = jwtService.extractJti(accessToken);
        long expirationMillis = jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
        redisTokenService.blacklistToken(jti, expirationMillis);
    }
}
