package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para item de estoque crítico.
 * Produtos com estoque menor que a média de vendas mensal.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalStockItemDTO {
    
    /**
     * Descrição do grupo
     */
    private String descricaoGrupo;
    
    /**
     * Código do Part Number
     */
    private String codigoPartNumber;
    
    /**
     * Descrição da marca
     */
    private String descricaoMarca;
    
    /**
     * REFPLU do produto
     */
    private String refplu;
    
    /**
     * Descrição do produto
     */
    private String descricaoProduto;
    
    /**
     * Custo de reposição
     */
    private BigDecimal custoReposicao;
    
    /**
     * Preço de venda
     */
    private BigDecimal precoVenda;
    
    /**
     * Vendas dos últimos 90 dias
     */
    private BigDecimal vendas90Dias;
    
    /**
     * Vendas dos últimos 60 dias
     */
    private BigDecimal vendas60Dias;
    
    /**
     * Vendas dos últimos 30 dias
     */
    private BigDecimal vendas30Dias;
    
    /**
     * Vendas do mês atual
     */
    private BigDecimal vendasMesAtual;
    
    /**
     * Estoque total atual
     */
    private BigDecimal estoque;
    
    /**
     * Média mensal de vendas (calculada)
     * (vendas90Dias + vendas60Dias + vendas30Dias) / 3
     */
    private BigDecimal mediaMensal;
    
    /**
     * Diferença entre média mensal e estoque (criticidade)
     * mediaMensal - estoque
     * Quanto maior, mais crítico o produto
     */
    private BigDecimal diferenca;
}

