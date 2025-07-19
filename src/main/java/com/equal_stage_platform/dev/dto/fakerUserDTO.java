package com.equal_stage_platform.dev.dto;
import java.util.UUID;
import lombok.AllArgsConstructor;

import lombok.Data;

@Data
@AllArgsConstructor
public class fakerUserDTO {
    String firstName;
    String lastName;
    String email;
    String password;
    UUID userId;
    // String token;
    // String refreshToken;
    @Override
    public String toString() {
        return "fakerUserDTO:\n\t" +
                "firstName='" + firstName + "\n\t" +
                "lastName='" + lastName + "\n\t" +
                "email='" + email + "\n\t" +
                "password='" + password + "\n\t" +
                "userId=" + userId + "\n";
    }
}
