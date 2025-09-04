package com.sysconard.business.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisição de alteração de status de role.
 * Utiliza Record para DTOs simples (≤5 campos) conforme regras do projeto.
 * Contém apenas o campo necessário para alterar o status de uma role.
 */
public record UpdateRoleStatusRequest(
    @NotNull(message = "Status da role é obrigatório")
    boolean active
) {
    /**
     * Validação customizada para garantir dados válidos
     */
    public UpdateRoleStatusRequest {
        // Validação adicional se necessário
    }
}
