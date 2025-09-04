package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO para resposta de atualização de usuário.
 * Utiliza Lombok para reduzir boilerplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {
    
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String profileImageUrl;
    private LocalDateTime joinDate;
    private LocalDateTime lastLoginDate;
    private boolean isActive;
    private boolean isNotLocked;
    private Set<String> roles;
    private String message;
    private LocalDateTime updatedAt;
}
