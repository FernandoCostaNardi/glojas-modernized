package com.sysconard.business.dto.sell;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * Record para requisição de relatório de vendas por loja.
 * Contém todos os parâmetros necessários para gerar o relatório.
 * 
 * @param startDate Data de início do período (obrigatório)
 * @param endDate Data de fim do período (obrigatório)
 * @param storeCodes Lista de códigos das lojas (obrigatório, não vazio)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record StoreReportRequest(
    @NotNull(message = "Data de início é obrigatória")
    LocalDate startDate,
    
    @NotNull(message = "Data de fim é obrigatória")
    LocalDate endDate,
    
    @NotEmpty(message = "Lista de códigos de lojas não pode estar vazia")
    List<String> storeCodes
) {
    /**
     * Construtor compacto com validações customizadas
     */
    public StoreReportRequest {
        // Validação das datas
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
        }
        
        // Validação dos códigos de loja
        if (storeCodes != null && storeCodes.stream().anyMatch(code -> code == null || code.trim().isEmpty())) {
            throw new IllegalArgumentException("Códigos de loja não podem ser nulos ou vazios");
        }
    }
}
