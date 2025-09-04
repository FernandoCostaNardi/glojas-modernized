package com.sysconard.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requisição para alterar o status de bloqueio de um usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserLockRequest {
    
    /**
     * Novo status de bloqueio do usuário (true = não bloqueado, false = bloqueado)
     */
    @NotNull(message = "Status de bloqueio é obrigatório")
    private Boolean isNotLocked;
    
    /**
     * Comentário opcional sobre a alteração de status de bloqueio
     */
    private String comment;
}
