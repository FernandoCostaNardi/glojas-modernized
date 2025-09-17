package com.sysconard.business.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO para resposta de roles do sistema.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Contém apenas os campos necessários para exibição das roles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    
    /**
     * Identificador único da role (UUID)
     */
    private UUID id;
    
    /**
     * Nome da role (ex: ADMIN, USER, MANAGER)
     */
    private String name;
    
    /**
     * Descrição da role
     */
    private String description;
    
    /**
     * Indica se a role está ativa
     */
    private boolean active;
    
    /**
     * Data e hora de criação da role
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização da role
     */
    private LocalDateTime updatedAt;
    
    /**
     * Nomes das permissões associadas à role
     */
    private Set<String> permissionNames;
}
