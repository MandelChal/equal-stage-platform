package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

import com.equal_stage_platform.dev.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response DTO containing login details")
public class ResponseLoginDTO {
    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String token;
    @Schema(description = "Refresh token")
    private String refreshToken;
    @Schema(description = "Set of roles of the user", example = "[USER, ADMIN]")
    private Set<Role> roles;
}
