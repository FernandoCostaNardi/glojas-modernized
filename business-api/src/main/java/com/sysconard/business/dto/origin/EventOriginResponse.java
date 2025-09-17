package com.sysconard.business.dto.origin;

import java.util.UUID;

import com.sysconard.business.enums.EventSource;

/**
 * DTO de resposta para EventOrigin.
 * Utiliza Record para DTOs simples (≤5 campos) seguindo padrões de Clean Code.
 * 
 * @param id Identificador único do EventOrigin
 * @param eventSource Fonte do evento (PDV, EXCHANGE, DANFE)
 * @param sourceCode Código da fonte
 */
public record EventOriginResponse(
    UUID id,
    EventSource eventSource,
    String sourceCode
) {
    /**
     * Construtor compacto para validações.
     */
    public EventOriginResponse {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        if (eventSource == null) {
            throw new IllegalArgumentException("EventSource não pode ser nulo");
        }
        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            throw new IllegalArgumentException("SourceCode não pode ser nulo ou vazio");
        }
    }
}
