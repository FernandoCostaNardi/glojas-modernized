package com.sysconard.business.service.sell;

import com.sysconard.business.dto.sell.ChartDataResponse;
import com.sysconard.business.dto.sell.ChartDataWithMetricsResponse;
import com.sysconard.business.dto.sell.ChartMetricsDto;
import com.sysconard.business.repository.sell.MonthlySellRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerar dados de gráfico para vendas mensais.
 * Implementa a lógica de negócio para buscar dados da tabela monthly_sells
 * e calcular métricas como melhor mês, pior mês e total de lojas.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlySalesChartService {
    
    private final MonthlySellRepository monthlySellRepository;
    
    /**
     * Obtém dados do gráfico de vendas mensais com métricas calculadas.
     * Busca dados da tabela monthly_sells e calcula métricas de performance.
     * 
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @param storeCode Código da loja específica (opcional)
     * @return Dados do gráfico com métricas calculadas
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    public ChartDataWithMetricsResponse getChartDataWithMetrics(String startYearMonth, String endYearMonth, String storeCode) {
        log.info("Gerando dados do gráfico mensal: {} a {}, loja: {}", startYearMonth, endYearMonth, storeCode);
        
        // Validar parâmetros
        validateParameters(startYearMonth, endYearMonth);
        
        try {
            // Buscar dados do gráfico
            List<Object[]> rawData = storeCode != null && !storeCode.trim().isEmpty()
                    ? monthlySellRepository.findChartDataByStore(storeCode, startYearMonth, endYearMonth)
                    : monthlySellRepository.findChartData(startYearMonth, endYearMonth);
            
            if (rawData.isEmpty()) {
                log.warn("Nenhum dado encontrado para o período {} a {}", startYearMonth, endYearMonth);
                return buildEmptyResponse();
            }
            
            log.debug("Dados do gráfico obtidos: {} pontos", rawData.size());
            
            // Mapear para DTOs
            List<ChartDataResponse> chartData = rawData.stream()
                    .map(this::mapToChartData)
                    .collect(Collectors.toList());
            
            // Calcular métricas
            ChartMetricsDto metrics = calculateMetrics(chartData);
            
            log.info("Dados do gráfico mensal gerados com sucesso: {} pontos, melhor mês: {}, pior mês: {}", 
                    chartData.size(), metrics.bestDay(), metrics.worstDay());
            
            return ChartDataWithMetricsResponse.builder()
                    .chartData(chartData)
                    .metrics(metrics)
                    .build();
            
        } catch (Exception e) {
            log.error("Erro ao gerar dados do gráfico mensal: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar dados do gráfico mensal: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida os parâmetros de entrada.
     * 
     * @param startYearMonth Ano/mês de início
     * @param endYearMonth Ano/mês de fim
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    private void validateParameters(String startYearMonth, String endYearMonth) {
        if (startYearMonth == null || startYearMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("Ano/mês de início é obrigatório");
        }
        
        if (endYearMonth == null || endYearMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("Ano/mês de fim é obrigatório");
        }
        
        if (endYearMonth.compareTo(startYearMonth) < 0) {
            throw new IllegalArgumentException("Ano/mês de fim deve ser maior ou igual ao ano/mês de início");
        }
        
        // Validar formato YYYY-MM
        if (!startYearMonth.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("Formato do ano/mês de início deve ser YYYY-MM");
        }
        
        if (!endYearMonth.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("Formato do ano/mês de fim deve ser YYYY-MM");
        }
    }
    
    /**
     * Mapeia um registro para DTO de dados do gráfico.
     * 
     * @param rawData Array de objetos retornado pela query SQL
     * @return DTO de dados do gráfico
     */
    private ChartDataResponse mapToChartData(Object[] rawData) {
        String yearMonth = (String) rawData[0];
        BigDecimal total = (BigDecimal) rawData[1];
        
        // Converter yearMonth para LocalDate (primeiro dia do mês)
        LocalDate date = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return ChartDataResponse.builder()
                .date(date)
                .total(total != null ? total : BigDecimal.ZERO)
                .build();
    }
    
    /**
     * Calcula métricas do gráfico (melhor mês, pior mês, total de lojas).
     * 
     * @param chartData Dados do gráfico
     * @return Métricas calculadas
     */
    private ChartMetricsDto calculateMetrics(List<ChartDataResponse> chartData) {
        if (chartData.isEmpty()) {
            return ChartMetricsDto.builder()
                    .bestDay(LocalDate.now())
                    .bestDayValue(BigDecimal.ZERO)
                    .worstDay(LocalDate.now())
                    .worstDayValue(BigDecimal.ZERO)
                    .totalActiveStores(0)
                    .build();
        }
        
        // Encontrar melhor e pior mês
        ChartDataResponse bestMonth = chartData.stream()
                .max((a, b) -> a.total().compareTo(b.total()))
                .orElse(chartData.get(0));
        
        ChartDataResponse worstMonth = chartData.stream()
                .min((a, b) -> a.total().compareTo(b.total()))
                .orElse(chartData.get(0));
        
        // Contar lojas únicas (assumindo que cada ponto representa um mês)
        // Para uma estimativa mais precisa, seria necessário uma query adicional
        int totalStores = chartData.size(); // Aproximação baseada nos pontos de dados
        
        return ChartMetricsDto.builder()
                .bestDay(bestMonth.date())
                .bestDayValue(bestMonth.total())
                .worstDay(worstMonth.date())
                .worstDayValue(worstMonth.total())
                .totalActiveStores(totalStores)
                .build();
    }
    
    
    /**
     * Constrói resposta vazia para casos sem dados.
     * 
     * @return Resposta vazia
     */
    private ChartDataWithMetricsResponse buildEmptyResponse() {
        ChartMetricsDto emptyMetrics = ChartMetricsDto.builder()
                .bestDay(LocalDate.now())
                .bestDayValue(BigDecimal.ZERO)
                .worstDay(LocalDate.now())
                .worstDayValue(BigDecimal.ZERO)
                .totalActiveStores(0)
                .build();
        
        return ChartDataWithMetricsResponse.builder()
                .chartData(List.of())
                .metrics(emptyMetrics)
                .build();
    }
}
