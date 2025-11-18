package com.sysconard.business.dto.exchange;

import java.time.LocalDateTime;

/**
 * Record para requisição de busca de trocas com filtros.
 * Todos os campos são opcionais para permitir buscas flexíveis.
 * 
 * @param documentCode Código do documento
 * @param storeCode Código da loja
 * @param operationCode Código da operação
 * @param originCode Código de origem
 * @param employeeCode Código do colaborador
 * @param startDate Data de início do período
 * @param endDate Data de fim do período
 */
public record ExchangeSearchRequest(
    String documentCode,
    String storeCode,
    String operationCode,
    String originCode,
    String employeeCode,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
}

