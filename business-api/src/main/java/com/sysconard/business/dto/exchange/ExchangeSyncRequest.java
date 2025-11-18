package com.sysconard.business.dto.exchange;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Record para requisição de sincronização de trocas.
 * Contém os parâmetros necessários para buscar trocas da Legacy API.
 * 
 * @param startDate Data de início do período (formato YYYY-MM-DD)
 * @param endDate Data de fim do período (formato YYYY-MM-DD)
 * @param originCodes Lista de códigos de origem (ORICOD) para filtrar
 * @param operationCodes Lista de códigos de operação (OPECOD) para filtrar
 */
public record ExchangeSyncRequest(
    @NotNull(message = "Data de início é obrigatória")
    LocalDate startDate,
    
    @NotNull(message = "Data de fim é obrigatória")
    LocalDate endDate,
    
    @NotEmpty(message = "Lista de códigos de origem não pode ser vazia")
    List<String> originCodes,
    
    @NotEmpty(message = "Lista de códigos de operação não pode ser vazia")
    List<String> operationCodes
) {
}

