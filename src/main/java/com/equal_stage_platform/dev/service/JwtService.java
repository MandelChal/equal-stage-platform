package com.equal_stage_platform.dev.service;

import com.equal_stage_platform.dev.model.CustomUserDetails;
import com.equal_stage_platform.dev.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUserId().toString())
            .claim("username", user.getEmail())
            .claim("role", user.getRole().name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .setId(UUID.randomUUID().toString())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateResetToken(long expiration){
        return Jwts.builder()
        .setIssuedAt(new Date())
        .setExpiration(new Date(expiration))
        .setId(UUID.randomUUID().toString())
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().getSubject());
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().get("username", String.class);
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().get("role", String.class);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        UUID userId = extractUserId(token);
        return userId.equals(((CustomUserDetails) userDetails).getId()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }
}
