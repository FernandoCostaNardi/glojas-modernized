package com.sysconard.business.dto.sale;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Record para requisição de sincronização de vendas.
 * Usado no Business API (Java 17) - DTOs simples usam Records.
 * 
 * @param startDate Data inicial para sincronização (primeiro segundo do dia será usado)
 * @param endDate Data final para sincronização (último segundo do dia será usado)
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
public record SaleSyncRequest(
    @NotNull(message = "Data inicial é obrigatória")
    LocalDate startDate,
    
    @NotNull(message = "Data final é obrigatória")
    LocalDate endDate
) {
    /**
     * Construtor compacto com validações adicionais
     */
    public SaleSyncRequest {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }
    }
}

