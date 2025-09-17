package com.sysconard.business.dto.origin;

import com.sysconard.business.enums.EventSource;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para busca de EventOrigin com filtros e paginação.
 * Utiliza Record para DTOs simples (≤5 campos) seguindo padrões de Clean Code.
 * 
 * @param eventSource Filtro por fonte do evento (opcional)
 * @param page Número da página (padrão: 0)
 * @param size Tamanho da página (padrão: 20, máximo: 100)
 * @param sortBy Campo para ordenação (padrão: "sourceCode")
 * @param sortDir Direção da ordenação (asc/desc, padrão: "asc")
 */
public record EventOriginSearchRequest(
    
    EventSource eventSource,
    
    @Min(value = 0, message = "Número da página deve ser >= 0")
    int page,
    
    @Min(value = 1, message = "Tamanho da página deve ser >= 1")
    @Max(value = 100, message = "Tamanho da página deve ser <= 100")
    int size,
    
    String sortBy,
    
    @Pattern(regexp = "^(asc|desc)$", message = "Direção de ordenação deve ser 'asc' ou 'desc'")
    String sortDir
    
) {
    /**
     * Construtor compacto com valores padrão.
     */
    public EventOriginSearchRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "sourceCode";
        if (sortDir == null || sortDir.trim().isEmpty()) sortDir = "asc";
    }
    
    /**
     * Construtor para busca sem filtros.
     */
    public EventOriginSearchRequest() {
        this(null, 0, 20, "sourceCode", "asc");
    }
    
    /**
     * Construtor para busca com filtro de EventSource.
     */
    public EventOriginSearchRequest(EventSource eventSource) {
        this(eventSource, 0, 20, "sourceCode", "asc");
    }
}
