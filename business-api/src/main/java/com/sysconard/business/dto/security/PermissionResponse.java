package com.sysconard.business.dto.security;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para permissões do sistema.
 * Utiliza Record para DTOs simples (≤5 campos) conforme regras do projeto.
 * Contém apenas os campos essenciais para exibição das permissões.
 */
public record PermissionResponse(
    UUID id,
    String name,
    String resource,
    String action,
    String description,
    LocalDateTime createdAt
) {
    /**
     * Validação customizada para garantir dados válidos
     */
    public PermissionResponse {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da permissão não pode ser nulo ou vazio");
        }
        if (resource == null || resource.trim().isEmpty()) {
            throw new IllegalArgumentException("Recurso da permissão não pode ser nulo ou vazio");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Ação da permissão não pode ser nula ou vazia");
        }
    }
}
