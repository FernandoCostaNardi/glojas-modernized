package com.sysconard.business.dto.origin;

import com.sysconard.business.enums.EventSource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de EventOrigin.
 * Utiliza Record para DTOs simples (≤5 campos) seguindo padrões de Clean Code.
 * 
 * @param eventSource Fonte do evento (PDV, EXCHANGE, DANFE)
 * @param sourceCode Código da fonte (máximo 50 caracteres)
 */
public record UpdateEventOriginRequest(
    
    @NotNull(message = "EventSource é obrigatório")
    EventSource eventSource,
    
    @NotBlank(message = "SourceCode é obrigatório")
    @Size(max = 50, message = "SourceCode deve ter no máximo 50 caracteres")
    String sourceCode
    
) {
    /**
     * Construtor compacto para validações adicionais.
     */
    public UpdateEventOriginRequest {
        if (sourceCode != null && sourceCode.trim().isEmpty()) {
            throw new IllegalArgumentException("SourceCode não pode ser vazio");
        }
    }
}
