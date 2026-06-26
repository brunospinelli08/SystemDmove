package com.systemdmove.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs de autenticacao agrupados (records aninhados).
 */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres") String password,
            @NotBlank String confirmPassword
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record UserDto(
            Long id,
            String name,
            String email,
            String avatarUrl
    ) {
    }

    public record AuthResponse(
            String token,
            UserDto user
    ) {
    }
}
