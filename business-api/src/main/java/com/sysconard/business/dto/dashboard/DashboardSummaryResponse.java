package com.sysconard.business.dto.dashboard;

import java.math.BigDecimal;

/**
 * DTO de resposta para resumo do dashboard.
 * Contém métricas principais para exibição na página inicial.
 * Utiliza Record para simplicidade e imutabilidade.
 * 
 * @author Business API
 * @version 1.0
 */
public record DashboardSummaryResponse(
    BigDecimal totalSalesToday,
    BigDecimal totalSalesMonth,
    BigDecimal totalSalesYear,
    Integer activeStoresCount
) {
    
    /**
     * Construtor com validação básica.
     * Garante que valores nulos sejam tratados como zero.
     */
    public DashboardSummaryResponse {
        if (totalSalesToday == null) {
            totalSalesToday = BigDecimal.ZERO;
        }
        if (totalSalesMonth == null) {
            totalSalesMonth = BigDecimal.ZERO;
        }
        if (totalSalesYear == null) {
            totalSalesYear = BigDecimal.ZERO;
        }
        if (activeStoresCount == null) {
            activeStoresCount = 0;
        }
    }
}
