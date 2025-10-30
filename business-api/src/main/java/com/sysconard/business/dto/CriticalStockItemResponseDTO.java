package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para resposta de item de estoque crítico.
 * Usado no Business API (Java 17) - DTOs complexos usam Lombok.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalStockItemResponseDTO {
    
    private String descricaoGrupo;
    private String codigoPartNumber;
    private String descricaoMarca;
    private String refplu;
    private String descricaoProduto;
    private BigDecimal custoReposicao;
    private BigDecimal precoVenda;
    private BigDecimal vendas90Dias;
    private BigDecimal vendas60Dias;
    private BigDecimal vendas30Dias;
    private BigDecimal vendasMesAtual;
    private BigDecimal estoque;
    private BigDecimal mediaMensal;
    private BigDecimal diferenca;
}

