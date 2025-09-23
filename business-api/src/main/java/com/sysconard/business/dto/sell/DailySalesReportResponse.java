package com.sysconard.business.dto.sell;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Record para resposta do relatório de vendas diárias.
 * Contém os dados agregados de vendas para uma loja específica no período.
 * 
 * @param storeName Nome da loja
 * @param pdv Valor total das vendas PDV no período
 * @param danfe Valor total das vendas DANFE no período
 * @param exchange Valor total das trocas no período
 * @param total Valor total geral no período
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record DailySalesReportResponse(
    String storeName,
    BigDecimal pdv,
    BigDecimal danfe,
    BigDecimal exchange,
    BigDecimal total
) {
    /**
     * Construtor compacto com validações e inicializações
     */
    public DailySalesReportResponse {
        // Garantir que valores nulos sejam convertidos para zero
        pdv = pdv != null ? pdv : BigDecimal.ZERO;
        danfe = danfe != null ? danfe : BigDecimal.ZERO;
        exchange = exchange != null ? exchange : BigDecimal.ZERO;
        total = total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Calcula o valor total geral (DANFE + PDV - EXCHANGE) para esta loja.
     * A troca é subtraída pois representa devoluções/trocas que reduzem o faturamento.
     * 
     * @return Valor total líquido das vendas
     */
    public BigDecimal getTotal() {
        return danfe.add(pdv).subtract(exchange);
    }
}
