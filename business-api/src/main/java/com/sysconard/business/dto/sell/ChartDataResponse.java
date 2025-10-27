package com.sysconard.business.dto.sell;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Record para resposta de dados do gráfico de vendas.
 * Contém os dados agregados de vendas para uma data específica.
 * Usado para alimentar gráficos de vendas diárias no frontend.
 * 
 * @param date Data do relatório (apenas a data, sem horário)
 * @param total Valor total das vendas para esta data (agregado de todas as lojas ou loja específica)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record ChartDataResponse(
    LocalDate date,
    BigDecimal total
) {
    /**
     * Construtor compacto com validações e inicializações
     */
    public ChartDataResponse {
        // Garantir que valor nulo seja convertido para zero
        total = total != null ? total : BigDecimal.ZERO;
    }
}
