package com.equal_stage_platform.dev.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class PasswordResetToken {
    @Id
    private String token;
    @OneToOne
    private User user;
    private long expirationTime;

    public PasswordResetToken(String token, User user, long tokenExpireTime){
        this.token = token;
        this.user = user;
        this.expirationTime = tokenExpireTime;
    }
}
