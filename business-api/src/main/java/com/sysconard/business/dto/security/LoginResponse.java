package com.sysconard.business.dto.security;

import java.util.Set;

/**
 * DTO para resposta de login.
 * Utiliza Record para simplicidade e imutabilidade.
 */
public record LoginResponse(
    String token,
    String username,
    String name,
    Set<String> roles,
    Set<String> permissions
) {
    /**
     * Construtor com validações básicas
     */
    public LoginResponse {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token não pode ser nulo ou vazio");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser nulo ou vazio");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        if (roles == null) {
            throw new IllegalArgumentException("Roles não pode ser nulo");
        }
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions não pode ser nulo");
        }
    }
}
