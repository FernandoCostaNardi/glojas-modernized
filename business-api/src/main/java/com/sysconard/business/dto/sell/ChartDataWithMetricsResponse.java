package com.sysconard.business.dto.sell;

import lombok.Builder;

import java.util.List;

/**
 * DTO de resposta consolidada para dados de gráfico com métricas.
 * Combina dados do gráfico de vendas com métricas calculadas em uma única resposta.
 * Reduz o número de chamadas de API e centraliza o cálculo de métricas no backend.
 * 
 * @param chartData Lista de dados agregados por dia para o gráfico
 * @param metrics Métricas calculadas (melhor dia, pior dia, total de lojas)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record ChartDataWithMetricsResponse(
    List<ChartDataResponse> chartData,
    ChartMetricsDto metrics
) {
    /**
     * Construtor compacto com validações
     */
    public ChartDataWithMetricsResponse {
        // Garantir que chartData não seja nulo
        chartData = chartData != null ? chartData : List.of();
    }
}
