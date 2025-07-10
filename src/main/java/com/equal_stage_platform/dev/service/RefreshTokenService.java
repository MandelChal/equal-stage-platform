package com.equal_stage_platform.dev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createRefreshToken(UUID userId) {
        String tokenId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("refresh:" + tokenId, userId.toString(), refreshExpiration, TimeUnit.MILLISECONDS);
        return tokenId;
    }

    public boolean isValid(String tokenId) {
        return redisTemplate.hasKey("refresh:" + tokenId);
    }

    public UUID getUserId(String tokenId) {
        String userIdStr = redisTemplate.opsForValue().get("refresh:" + tokenId);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    public void revokeToken(String tokenId) {
        redisTemplate.delete("refresh:" + tokenId);
    }

    // JWT Blacklist methods
    public void blacklistToken(String jti, long expirationMillis) {
        redisTemplate.opsForValue().set("blacklist:" + jti, "true", expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti));
    }
}
