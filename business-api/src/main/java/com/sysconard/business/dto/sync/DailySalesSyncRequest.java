package com.sysconard.business.dto.sync;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Record para requisição de sincronização de vendas diárias.
 * Contém os parâmetros necessários para definir o período de sincronização.
 * 
 * @param startDate Data de início do período de sincronização (obrigatório)
 * @param endDate Data de fim do período de sincronização (obrigatório)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record DailySalesSyncRequest(
    @NotNull(message = "Data de início é obrigatória")
    LocalDate startDate,
    
    @NotNull(message = "Data de fim é obrigatória")
    LocalDate endDate
) {
    /**
     * Construtor compacto com validações customizadas.
     * Garante que a data de início não seja posterior à data de fim.
     */
    public DailySalesSyncRequest {
        // Validação das datas
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
        }
        
        // Validação para não permitir datas futuras
        if (startDate != null && startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de início não pode ser futura");
        }
        
        if (endDate != null && endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de fim não pode ser futura");
        }
    }
}
