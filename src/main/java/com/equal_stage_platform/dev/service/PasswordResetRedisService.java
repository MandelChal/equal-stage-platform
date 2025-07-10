package com.equal_stage_platform.dev.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetRedisService {
    private final StringRedisTemplate redisTemplate;

    public PasswordResetRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken(String token, String userEmail, long expirationMillis) {
        redisTemplate.opsForValue().set(token, userEmail, expirationMillis, TimeUnit.MILLISECONDS);
    }

    public String getUserEmailByToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }
}