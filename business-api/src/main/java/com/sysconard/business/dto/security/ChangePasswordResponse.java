package com.sysconard.business.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de alteração de senha.
 * Utiliza Lombok para reduzir boilerplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    
    private UUID userId;
    private String username;
    private String message;
    private LocalDateTime changedAt;
    private boolean success;
}
