package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para receber parâmetros da requisição de detalhes de itens de venda.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDetailRequestDTO {
    
    /**
     * Data inicial do período no formato ISO (ex: "2025-11-13T00:00:00")
     */
    private String startDate;
    
    /**
     * Data final do período no formato ISO (ex: "2025-11-14T00:00:00")
     */
    private String endDate;
    
    /**
     * Lista de códigos de origem (ORICOD) para PDV e DANFE
     * Exemplo: ["009", "015", "002"]
     */
    private List<String> originCodes;
    
    /**
     * Lista de códigos de operação (OPECOD) do tipo SELL
     * Exemplo: ["000999", "000007", "000001", "000045", "000054", "000062", "000063", "000064", "000065", "000067", "000068", "000069", "000071"]
     */
    private List<String> operationCodes;
    
    /**
     * Lista de códigos de lojas (LOJCOD)
     * Exemplo: ["000001", "000002", "000003", ...]
     */
    private List<String> storeCodes;
}

