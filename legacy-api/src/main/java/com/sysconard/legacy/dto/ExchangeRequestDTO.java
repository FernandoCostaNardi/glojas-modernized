package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * DTO de entrada para solicitação de busca de trocas realizadas em um período.
 * Centraliza todos os parâmetros necessários para buscar as trocas.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Lista de códigos de origem (ORICOD) para filtrar as trocas.
     * Exemplo: ["051", "065"]
     */
    @NotEmpty(message = "Códigos de origem são obrigatórios")
    private List<String> originCodes;
    
    /**
     * Lista de códigos de operação (OPECOD) para filtrar as trocas.
     * Exemplo: ["000015", "000048"]
     */
    @NotEmpty(message = "Códigos de operação são obrigatórios")
    private List<String> operationCodes;
    
    /**
     * Data de início para filtrar os documentos.
     * Formato: YYYY-MM-DD (ex: "2025-11-07")
     */
    @NotBlank(message = "Data de início é obrigatória")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data de início deve estar no formato YYYY-MM-DD")
    private String startDate;
    
    /**
     * Data de fim para filtrar os documentos.
     * Formato: YYYY-MM-DD (ex: "2025-11-07")
     */
    @NotBlank(message = "Data de fim é obrigatória")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data de fim deve estar no formato YYYY-MM-DD")
    private String endDate;
}

