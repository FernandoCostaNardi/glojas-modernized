package com.sysconard.business.service.sell;

import com.sysconard.business.dto.sell.ChartDataResponse;
import com.sysconard.business.dto.sell.ChartDataWithMetricsResponse;
import com.sysconard.business.dto.sell.ChartMetricsDto;
import com.sysconard.business.dto.sell.StoreReportByDayResponse;
import com.sysconard.business.dto.sell.StoreReportRequest;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável por processar dados para gráficos de vendas.
 * Agrega dados de vendas por dia e aplica filtros de loja e permissões.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesChartService {
    
    private final SellService sellService;
    private final StoreService storeService;
    
    /**
     * Obtém dados agregados de vendas por dia para gráficos.
     * Aplica filtros de loja e permissões do usuário autenticado.
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @param storeCode Código da loja específica (opcional, null = todas as lojas)
     * @return Lista de dados agregados por dia ordenada por data
     * @throws IllegalArgumentException se período for inválido ou muito longo
     */
    public List<ChartDataResponse> getChartData(LocalDate startDate, LocalDate endDate, String storeCode) {
        log.info("Processando dados do gráfico: startDate={}, endDate={}, storeCode={}", 
                startDate, endDate, storeCode);
        
        try {
            // 1. Validar período
            validatePeriod(startDate, endDate);
            
            // 2. Obter lojas permitidas para o usuário
            List<StoreResponseDto> allowedStores = storeService.getAllActiveStores();
            
            if (allowedStores.isEmpty()) {
                log.warn("Nenhuma loja permitida encontrada para o usuário. Retornando lista vazia.");
                return List.of();
            }
            
            // 3. Validar loja específica se fornecida
            if (storeCode != null) {
                validateStoreCode(storeCode, allowedStores);
            }
            
            // 4. Buscar dados da Legacy API
            List<StoreReportByDayResponse> rawData = fetchSalesData(startDate, endDate, allowedStores);
            
            // 5. Filtrar por loja se especificada
            List<StoreReportByDayResponse> filteredData = filterByStore(rawData, storeCode);
            
            // 6. Agregar dados por dia
            List<ChartDataResponse> chartData = aggregateByDate(filteredData);
            
            log.info("Dados do gráfico processados com sucesso: {} pontos de dados", chartData.size());
            
            return chartData;
            
        } catch (Exception e) {
            log.error("Erro ao processar dados do gráfico: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Valida se o período é válido e não excede 31 dias.
     * 
     * @param startDate Data de início
     * @param endDate Data de fim
     * @throws IllegalArgumentException se período for inválido
     */
    private void validatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Data de início e fim são obrigatórias");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data de início deve ser menor ou igual à data de fim");
        }
        
        if (startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de início não pode ser futura");
        }
        
        if (endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de fim não pode ser futura");
        }
        
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 31) {
            throw new IllegalArgumentException("Período máximo permitido é de 31 dias");
        }
        
        log.debug("Período validado: {} dias entre {} e {}", daysBetween + 1, startDate, endDate);
    }
    
    /**
     * Valida se o código da loja está entre as lojas permitidas.
     * 
     * @param storeCode Código da loja
     * @param allowedStores Lista de lojas permitidas
     * @throws IllegalArgumentException se loja não for permitida
     */
    private void validateStoreCode(String storeCode, List<StoreResponseDto> allowedStores) {
        boolean isAllowed = allowedStores.stream()
                .anyMatch(store -> store.getCode().equals(storeCode));
        
        if (!isAllowed) {
            throw new IllegalArgumentException("Loja não permitida para o usuário: " + storeCode);
        }
        
        log.debug("Loja validada: {}", storeCode);
    }
    
    /**
     * Busca dados de vendas da Legacy API.
     * 
     * @param startDate Data de início
     * @param endDate Data de fim
     * @param allowedStores Lojas permitidas
     * @return Lista de dados de vendas por loja e por dia
     */
    private List<StoreReportByDayResponse> fetchSalesData(LocalDate startDate, LocalDate endDate, 
                                                         List<StoreResponseDto> allowedStores) {
        log.debug("Buscando dados de vendas da Legacy API: {} lojas no período {} a {}", 
                allowedStores.size(), startDate, endDate);
        
        List<String> storeCodes = allowedStores.stream()
                .map(StoreResponseDto::getCode)
                .collect(Collectors.toList());
        
        StoreReportRequest request = StoreReportRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeCodes(storeCodes)
                .build();
        
        List<StoreReportByDayResponse> data = sellService.getStoreReportByDay(request);
        
        log.debug("Dados obtidos da Legacy API: {} registros", data.size());
        
        return data;
    }
    
    /**
     * Filtra dados por loja específica se fornecida.
     * 
     * @param rawData Dados brutos
     * @param storeCode Código da loja (null = todas as lojas)
     * @return Dados filtrados
     */
    private List<StoreReportByDayResponse> filterByStore(List<StoreReportByDayResponse> rawData, String storeCode) {
        if (storeCode == null) {
            log.debug("Nenhum filtro de loja aplicado - retornando todas as lojas");
            return rawData;
        }
        
        List<StoreReportByDayResponse> filtered = rawData.stream()
                .filter(data -> data.storeCode().equals(storeCode))
                .collect(Collectors.toList());
        
        log.debug("Dados filtrados por loja {}: {} registros", storeCode, filtered.size());
        
        return filtered;
    }
    
    /**
     * Agrega dados por data somando totais de todas as lojas.
     * 
     * @param data Dados filtrados por loja
     * @return Lista agregada por data
     */
    private List<ChartDataResponse> aggregateByDate(List<StoreReportByDayResponse> data) {
        log.debug("Agregando dados por data: {} registros", data.size());
        
        Map<LocalDate, BigDecimal> aggregatedByDate = data.stream()
                .collect(Collectors.groupingBy(
                    StoreReportByDayResponse::reportDate,
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        StoreReportByDayResponse::getTotal,
                        BigDecimal::add
                    )
                ));
        
        List<ChartDataResponse> chartData = aggregatedByDate.entrySet().stream()
                .map(entry -> ChartDataResponse.builder()
                        .date(entry.getKey())
                        .total(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(ChartDataResponse::date))
                .collect(Collectors.toList());
        
        log.debug("Dados agregados: {} pontos de dados únicos", chartData.size());
        
        return chartData;
    }
    
    /**
     * Obtém dados agregados de vendas por dia para gráficos com métricas calculadas.
     * Aplica filtros de loja e permissões do usuário autenticado.
     * Calcula métricas como melhor dia, pior dia e total de lojas ativas.
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @param storeCode Código da loja específica (opcional, null = todas as lojas)
     * @return Dados do gráfico com métricas calculadas
     * @throws IllegalArgumentException se período for inválido ou muito longo
     */
    public ChartDataWithMetricsResponse getChartDataWithMetrics(LocalDate startDate, LocalDate endDate, String storeCode) {
        log.info("Processando dados do gráfico com métricas: startDate={}, endDate={}, storeCode={}", 
                startDate, endDate, storeCode);
        
        try {
            // 1. Obter dados do gráfico usando método existente
            List<ChartDataResponse> chartData = getChartData(startDate, endDate, storeCode);
            
            // 2. Calcular métricas
            ChartMetricsDto metrics = calculateMetrics(chartData, startDate, endDate);
            
            log.info("Dados do gráfico com métricas processados com sucesso: {} pontos de dados", chartData.size());
            
            return ChartDataWithMetricsResponse.builder()
                    .chartData(chartData)
                    .metrics(metrics)
                    .build();
            
        } catch (Exception e) {
            log.error("Erro ao processar dados do gráfico com métricas: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Calcula métricas a partir dos dados do gráfico.
     * 
     * @param chartData Dados do gráfico
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Métricas calculadas
     */
    private ChartMetricsDto calculateMetrics(List<ChartDataResponse> chartData, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculando métricas para {} pontos de dados", chartData.size());
        
        // Obter total de lojas ativas permitidas
        List<StoreResponseDto> allowedStores = storeService.getAllActiveStores();
        Integer totalActiveStores = allowedStores.size();
        
        if (chartData.isEmpty()) {
            log.debug("Nenhum dado disponível para calcular métricas");
            return ChartMetricsDto.builder()
                    .bestDay(startDate)
                    .bestDayValue(BigDecimal.ZERO)
                    .worstDay(startDate)
                    .worstDayValue(BigDecimal.ZERO)
                    .totalActiveStores(totalActiveStores)
                    .build();
        }
        
        // Encontrar melhor e pior dia
        ChartDataResponse bestDay = chartData.stream()
                .max(Comparator.comparing(ChartDataResponse::total))
                .orElse(chartData.get(0));
        
        ChartDataResponse worstDay = chartData.stream()
                .filter(data -> data.total().compareTo(BigDecimal.ZERO) > 0) // Apenas dias com vendas > 0
                .min(Comparator.comparing(ChartDataResponse::total))
                .orElse(chartData.stream()
                        .min(Comparator.comparing(ChartDataResponse::total))
                        .orElse(chartData.get(0)));
        
        ChartMetricsDto metrics = ChartMetricsDto.builder()
                .bestDay(bestDay.date())
                .bestDayValue(bestDay.total())
                .worstDay(worstDay.date())
                .worstDayValue(worstDay.total())
                .totalActiveStores(totalActiveStores)
                .build();
        
        log.debug("Métricas calculadas: melhor dia={} ({}), pior dia={} ({}), total lojas={}", 
                metrics.bestDay(), metrics.bestDayValue(),
                metrics.worstDay(), metrics.worstDayValue(),
                metrics.totalActiveStores());
        
        return metrics;
    }
}
