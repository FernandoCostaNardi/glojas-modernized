package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para item de análise de compras.
 * Contém informações de vendas em múltiplos períodos, estoque, custos e preços.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseAnalysisItemDTO {
    
    /**
     * Descrição do grupo do produto
     */
    private String descricaoGrupo;
    
    /**
     * Código do part number (referência do fabricante)
     */
    private String codigoPartNumber;
    
    /**
     * Descrição da marca do produto
     */
    private String descricaoMarca;
    
    /**
     * Código PLU da referência
     */
    private String refplu;
    
    /**
     * Descrição do produto
     */
    private String descricaoProduto;
    
    /**
     * Custo de reposição do produto
     */
    private BigDecimal custoReposicao;
    
    /**
     * Preço de venda do produto
     */
    private BigDecimal precoVenda;
    
    /**
     * Total de vendas nos últimos 90 dias (3 meses atrás)
     */
    private BigDecimal vendas90Dias;
    
    /**
     * Total de vendas nos últimos 60 dias (2 meses atrás)
     */
    private BigDecimal vendas60Dias;
    
    /**
     * Total de vendas nos últimos 30 dias (1 mês atrás)
     */
    private BigDecimal vendas30Dias;
    
    /**
     * Total de vendas no mês atual
     */
    private BigDecimal vendasMesAtual;
    
    /**
     * Quantidade total em estoque
     */
    private BigDecimal estoque;
}

