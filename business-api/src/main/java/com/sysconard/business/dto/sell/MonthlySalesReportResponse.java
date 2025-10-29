package com.sysconard.business.dto.sell;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO de resposta para relatório de vendas mensais.
 * Contém dados agregados de vendas por loja em um período mensal.
 * 
 * @param storeName Nome da loja
 * @param total Valor total das vendas da loja no período
 * @param percentageOfTotal Percentual do total geral representado por esta loja
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record MonthlySalesReportResponse(
    String storeName,
    BigDecimal total,
    BigDecimal percentageOfTotal
) {
    /**
     * Construtor compacto com validações
     */
    public MonthlySalesReportResponse {
        // Garantir que valores não sejam nulos
        total = total != null ? total : BigDecimal.ZERO;
        percentageOfTotal = percentageOfTotal != null ? percentageOfTotal : BigDecimal.ZERO;
        
        // Validar percentual entre 0 e 100
        if (percentageOfTotal.compareTo(BigDecimal.ZERO) < 0 || 
            percentageOfTotal.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentual deve estar entre 0 e 100");
        }
    }
}
