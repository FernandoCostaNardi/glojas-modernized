package com.sysconard.business.dto.security;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requisição para alterar o status ativo/inativo de um usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {
    
    /**
     * Novo status do usuário (true = ativo, false = inativo)
     */
    @NotNull(message = "Status é obrigatório")
    private Boolean isActive;
    
    /**
     * Comentário opcional sobre a alteração de status
     */
    private String comment;
}
