package com.sysconard.business.dto.sell;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para métricas calculadas do gráfico de vendas.
 * Contém informações sobre melhor dia, pior dia e total de lojas ativas.
 * Usado para fornecer métricas calculadas no backend para o frontend.
 * 
 * @param bestDay Data do melhor dia de vendas
 * @param bestDayValue Valor total do melhor dia de vendas
 * @param worstDay Data do pior dia de vendas
 * @param worstDayValue Valor total do pior dia de vendas
 * @param totalActiveStores Total de lojas ativas com permissão do usuário
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record ChartMetricsDto(
    LocalDate bestDay,
    BigDecimal bestDayValue,
    LocalDate worstDay,
    BigDecimal worstDayValue,
    Integer totalActiveStores
) {
    /**
     * Construtor compacto com validações e inicializações
     */
    public ChartMetricsDto {
        // Garantir que valores nulos sejam convertidos para zero
        bestDayValue = bestDayValue != null ? bestDayValue : BigDecimal.ZERO;
        worstDayValue = worstDayValue != null ? worstDayValue : BigDecimal.ZERO;
        totalActiveStores = totalActiveStores != null ? totalActiveStores : 0;
    }
}
