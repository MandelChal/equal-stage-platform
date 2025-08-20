package com.equal_stage_platform.dev.service;

import com.equal_stage_platform.dev.exception.AuthException;
import com.equal_stage_platform.dev.model.User;
import com.equal_stage_platform.dev.model.enums.Role;
import com.equal_stage_platform.dev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRoleManagementService {
    private final UserRepository userRepository;

    @Transactional
    public void addRole(UUID userId, Role role) {
        User user = getUserById(userId);
        user.addRole(role);
        userRepository.save(user);
    }
    
    @Transactional
    public void removeRole(UUID userId, Role role) {
        User user = getUserById(userId);
        user.removeRole(role);
        userRepository.save(user);
    }
    
    @Transactional
    public void removeRole(User user, Role role) {
        user.removeRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void addRole(User user, Role role) {
        user.addRole(role);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean hasRole(UUID userId, Role role) {
        return getUserRoles(userId).contains(role);
    }

    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(UUID userId) {
        return userRepository.getRolesByUserId(userId)
            .orElseThrow(() -> new AuthException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User with userId: " + userId + " not found"));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException("User with email: " + email + " not found"));
    }
}
