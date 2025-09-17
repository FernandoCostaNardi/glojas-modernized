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
 * DTO de entrada para solicitação de relatório de vendas por loja.
 * Centraliza todos os parâmetros necessários para gerar o relatório.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSalesReportRequestDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Data de início para filtrar os documentos.
     * Formato: YYYY-MM-DD
     */
    @NotBlank(message = "Data de início é obrigatória")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data de início deve estar no formato YYYY-MM-DD")
    private String startDate;
    
    /**
     * Data de fim para filtrar os documentos.
     * Formato: YYYY-MM-DD
     */
    @NotBlank(message = "Data de fim é obrigatória")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data de fim deve estar no formato YYYY-MM-DD")
    private String endDate;
    
    /**
     * Lista de códigos de loja para incluir no relatório.
     */
    @NotEmpty(message = "Códigos de loja são obrigatórios")
    private List<String> storeCodes;
    
    /**
     * Lista de códigos de origem para DANFE.
     */
    @NotEmpty(message = "Códigos de origem DANFE são obrigatórios")
    private List<String> danfeOrigin;
    
    /**
     * Lista de códigos de origem para PDV.
     */
    @NotEmpty(message = "Códigos de origem PDV são obrigatórios")
    private List<String> pdvOrigin;
    
    /**
     * Lista de códigos de origem para trocas.
     */
    @NotEmpty(message = "Códigos de origem de troca são obrigatórios")
    private List<String> exchangeOrigin;
    
    /**
     * Lista de códigos de operação para vendas.
     */
    @NotEmpty(message = "Códigos de operação de venda são obrigatórios")
    private List<String> sellOperation;
    
    /**
     * Lista de códigos de operação para trocas.
     */
    @NotEmpty(message = "Códigos de operação de troca são obrigatórios")
    private List<String> exchangeOperation;
}
