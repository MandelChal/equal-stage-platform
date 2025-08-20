package com.equal_stage_platform.dev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refreshExpiration}")
    private long refreshTokenExpiration;

    private final StringRedisTemplate redisTemplate;

    public RedisTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void enrollUserSession(UUID userId, String accessToken, String refreshToken) {
        redisTemplate.opsForValue().set("access:" + userId, accessToken, accessTokenExpiration, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(refreshToken, userId.toString(), refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public boolean isValid(String refreshToken) {
        return redisTemplate.hasKey(refreshToken);
    }

    public UUID getUserId(String refreshToken) {
        String userIdStr = redisTemplate.opsForValue().get(refreshToken);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    public String getAccessToken(UUID userId) {
        return redisTemplate.opsForValue().get("access:" + userId);
    }

    public void updateAccessToken(UUID userId, String accessToken) {
        redisTemplate.opsForValue().set("access:" + userId, accessToken, accessTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public void revokeToken(String refreshToken) {
        UUID userId = getUserId(refreshToken);
        if(userId == null)
            return;
        redisTemplate.delete("access:" + userId);
        redisTemplate.delete(refreshToken);
    }

    // JWT Blacklist methods
    public void blacklistToken(String jti, long expirationMillis) {
        redisTemplate.opsForValue().set("blacklist:" + jti, "true", expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti));
    }
}
