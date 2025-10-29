package com.sysconard.business.dto.sync;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO de requisição para sincronização de vendas mensais.
 * Contém o período de datas para processamento das vendas diárias.
 * 
 * @param startDate Data de início do período (obrigatório)
 * @param endDate Data de fim do período (obrigatório)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record MonthlySalesSyncRequest(
    @NotNull(message = "Data de início é obrigatória")
    LocalDate startDate,
    
    @NotNull(message = "Data de fim é obrigatória")
    LocalDate endDate
) {
    /**
     * Construtor compacto com validações
     */
    public MonthlySalesSyncRequest {
        Objects.requireNonNull(startDate, "Data de início não pode ser nula");
        Objects.requireNonNull(endDate, "Data de fim não pode ser nula");
        
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Data de fim deve ser maior ou igual à data de início");
        }
    }
}
