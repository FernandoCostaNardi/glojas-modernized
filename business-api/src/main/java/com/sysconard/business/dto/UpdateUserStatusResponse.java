package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resposta da alteração de status ativo/inativo de um usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusResponse {
    
    /**
     * ID do usuário
     */
    private UUID userId;
    
    /**
     * Username do usuário
     */
    private String username;
    
    /**
     * Nome do usuário
     */
    private String name;
    
    /**
     * Status anterior do usuário
     */
    private Boolean previousStatus;
    
    /**
     * Novo status do usuário
     */
    private Boolean newStatus;
    
    /**
     * Comentário sobre a alteração
     */
    private String comment;
    
    /**
     * Data e hora da alteração
     */
    private LocalDateTime updatedAt;
    
    /**
     * Mensagem de confirmação
     */
    private String message;
}
