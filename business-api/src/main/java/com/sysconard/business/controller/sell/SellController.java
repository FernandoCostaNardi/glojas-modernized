package com.sysconard.business.controller.sell;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.sell.StoreReportRequest;
import com.sysconard.business.dto.sell.StoreReportResponse;
import com.sysconard.business.dto.sell.StoreReportByDayResponse;
import com.sysconard.business.dto.sell.ChartDataResponse;
import com.sysconard.business.dto.sell.ChartDataWithMetricsResponse;
import com.sysconard.business.dto.sell.MonthlySalesReportResponse;
import com.sysconard.business.dto.sell.YearlySalesReportResponse;
import com.sysconard.business.service.sell.DailySalesReportService;
import com.sysconard.business.service.sell.SellService;
import com.sysconard.business.service.sell.CurrentDailySalesService;
import com.sysconard.business.service.sell.SalesChartService;
import com.sysconard.business.service.sell.MonthlySalesReportService;
import com.sysconard.business.service.sell.MonthlySalesChartService;
import com.sysconard.business.service.sell.YearlySalesReportService;
import com.sysconard.business.service.sell.YearlySalesChartService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * Controller REST para operações relacionadas a vendas.
 * Fornece endpoints para relatórios de vendas consumindo dados da Legacy API.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Validated
public class SellController {
    
    private final SellService sellService;
    private final DailySalesReportService dailySalesReportService;
    private final CurrentDailySalesService currentDailySalesService;
    private final SalesChartService salesChartService;
    private final MonthlySalesReportService monthlySalesReportService;
    private final MonthlySalesChartService monthlySalesChartService;
    private final YearlySalesReportService yearlySalesReportService;
    private final YearlySalesChartService yearlySalesChartService;
    
    /**
     * Endpoint para obter relatório de vendas por loja.
     * Consome a Legacy API para retornar dados agregados de DANFE, PDV e TROCA3.
     * 
     * @param request DTO com todos os parâmetros necessários para o relatório
     * @return Lista de dados agregados por loja
     */
    @PostMapping("/store-report")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<List<StoreReportResponse>> getStoreReport(
            @Valid @RequestBody StoreReportRequest request) {
        
        log.info("Recebida solicitação de relatório de vendas por loja: startDate={}, endDate={}, storeCodes={}", 
                request.startDate(), request.endDate(), request.storeCodes());
        
        try {
            List<StoreReportResponse> report = sellService.getStoreReport(request);
            
            log.info("Relatório de vendas processado com sucesso: {} lojas retornadas", report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Erro ao processar relatório de vendas: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter relatório de vendas por loja e por dia.
     * Consome a Legacy API para retornar dados agregados de DANFE, PDV e TROCA3
     * separados por dia dentro do período solicitado.
     * 
     * @param request DTO com todos os parâmetros necessários para o relatório
     * @return Lista de dados agregados por loja e por dia
     */
    @PostMapping("/store-report-by-day")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<List<StoreReportByDayResponse>> getStoreReportByDay(
            @Valid @RequestBody StoreReportRequest request) {
        
        log.info("Recebida solicitação de relatório de vendas por loja e por dia: startDate={}, endDate={}, storeCodes={}", 
                request.startDate(), request.endDate(), request.storeCodes());
        
        try {
            List<StoreReportByDayResponse> report = sellService.getStoreReportByDay(request);
            
            log.info("Relatório de vendas por dia processado com sucesso: {} registros retornados", report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Erro ao processar relatório de vendas por dia: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter relatório de vendas diárias agregadas por loja.
     * Retorna dados consolidados de vendas para um período específico.
     * 
     * @param startDate Data de início do período (obrigatório)
     * @param endDate Data de fim do período (obrigatório)
     * @return Lista de vendas agregadas por loja
     */
    @GetMapping("/daily-sales")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<List<DailySalesReportResponse>> getDailySalesReport(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Recebida solicitação de relatório de vendas diárias: startDate={}, endDate={}", 
                startDate, endDate);
        
        try {
            List<DailySalesReportResponse> report = dailySalesReportService.generateReport(startDate, endDate);
            
            log.info("Relatório de vendas diárias processado com sucesso: {} lojas retornadas", report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetros inválidos para relatório de vendas diárias: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Erro ao processar relatório de vendas diárias: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter vendas do dia atual em tempo real.
     * Busca dados diretamente da Legacy API sem usar cache,
     * garantindo informações sempre atualizadas para o frontend.
     * 
     * @return Lista de vendas agregadas por loja para o dia atual
     */
    @GetMapping("/current-daily-sales")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<List<DailySalesReportResponse>> getCurrentDailySales() {
        
        log.info("Recebida solicitação de vendas do dia atual em tempo real");
        
        try {
            List<DailySalesReportResponse> currentSales = currentDailySalesService.getCurrentDailySales();
            
            log.info("Vendas do dia atual processadas com sucesso: {} lojas retornadas", currentSales.size());
            
            // Configurar headers anti-cache para garantir dados sempre frescos
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(currentSales);
            
        } catch (Exception e) {
            log.error("Erro ao processar vendas do dia atual: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter dados de vendas para gráficos.
     * Retorna dados agregados por dia para alimentar gráficos de vendas.
     * Aplica filtros de loja e permissões do usuário autenticado.
     * 
     * @param startDate Data de início do período (obrigatório)
     * @param endDate Data de fim do período (obrigatório)
     * @param storeCode Código da loja específica (opcional, null = todas as lojas)
     * @return Lista de dados agregados por dia para o gráfico
     */
    @GetMapping("/monthly-chart-data")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<List<ChartDataResponse>> getMonthlyChartData(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String storeCode) {
        
        log.info("Recebida solicitação de dados do gráfico: startDate={}, endDate={}, storeCode={}", 
                startDate, endDate, storeCode);
        
        try {
            List<ChartDataResponse> chartData = salesChartService.getChartData(startDate, endDate, storeCode);
            
            log.info("Dados do gráfico processados com sucesso: {} pontos de dados retornados", chartData.size());
            
            return ResponseEntity.ok(chartData);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetros inválidos para dados do gráfico: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Erro ao processar dados do gráfico: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter dados de gráfico de vendas com métricas calculadas.
     * Retorna dados agregados por dia para alimentar gráficos de vendas
     * junto com métricas como melhor dia, pior dia e total de lojas ativas.
     * Aplica filtros de loja e permissões do usuário autenticado.
     * 
     * @param startDate Data de início do período (obrigatório)
     * @param endDate Data de fim do período (obrigatório)
     * @param storeCode Código da loja específica (opcional, null = todas as lojas)
     * @return Dados do gráfico com métricas calculadas
     */
    @GetMapping("/chart-data-with-metrics")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<ChartDataWithMetricsResponse> getChartDataWithMetrics(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String storeCode) {
        
        log.info("Recebida solicitação de dados do gráfico com métricas: startDate={}, endDate={}, storeCode={}", 
                startDate, endDate, storeCode);
        
        try {
            ChartDataWithMetricsResponse response = salesChartService.getChartDataWithMetrics(startDate, endDate, storeCode);
            
            log.info("Dados do gráfico com métricas processados com sucesso: {} pontos de dados retornados", 
                    response.chartData().size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetros inválidos para dados do gráfico com métricas: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Erro ao processar dados do gráfico com métricas: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter relatório de vendas mensais agregadas por loja.
     * Retorna dados consolidados de vendas mensais para um período específico.
     * 
     * @param startYearMonth Ano/mês de início do período (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim do período (formato YYYY-MM)
     * @return Lista de vendas mensais agregadas por loja
     */
    @GetMapping("/monthly-sales")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<List<MonthlySalesReportResponse>> getMonthlySalesReport(
            @RequestParam @NotNull String startYearMonth,
            @RequestParam @NotNull String endYearMonth) {
        
        log.info("Recebida solicitação de relatório de vendas mensais: startYearMonth={}, endYearMonth={}", 
                startYearMonth, endYearMonth);
        
        try {
            List<MonthlySalesReportResponse> report = monthlySalesReportService.generateReport(startYearMonth, endYearMonth);
            
            log.info("Relatório de vendas mensais processado com sucesso: {} lojas retornadas", report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetros inválidos para relatório de vendas mensais: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler como 400 Bad Request
        } catch (Exception e) {
            log.error("Erro ao processar relatório de vendas mensais: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter dados de gráfico de vendas mensais com métricas calculadas.
     * Retorna dados agregados por mês para alimentar gráficos de vendas mensais
     * junto com métricas como melhor mês, pior mês e total de lojas ativas.
     * Aplica filtros de loja e permissões do usuário autenticado.
     * 
     * @param startYearMonth Ano/mês de início do período (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim do período (formato YYYY-MM)
     * @param storeCode Código da loja específica (opcional, null = todas as lojas)
     * @return Dados do gráfico mensal com métricas calculadas
     */
    @GetMapping("/monthly-chart-data-with-metrics")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<ChartDataWithMetricsResponse> getMonthlyChartDataWithMetrics(
            @RequestParam @NotNull String startYearMonth,
            @RequestParam @NotNull String endYearMonth,
            @RequestParam(required = false) String storeCode) {
        
        log.info("Recebida solicitação de dados do gráfico mensal com métricas: startYearMonth={}, endYearMonth={}, storeCode={}", 
                startYearMonth, endYearMonth, storeCode);
        
        try {
            ChartDataWithMetricsResponse response = monthlySalesChartService.getChartDataWithMetrics(startYearMonth, endYearMonth, storeCode);
            
            log.info("Dados do gráfico mensal com métricas processados com sucesso: {} pontos de dados retornados", 
                    response.chartData().size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetros inválidos para dados do gráfico mensal com métricas: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler como 400 Bad Request
        } catch (Exception e) {
            log.error("Erro ao processar dados do gráfico mensal com métricas: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter relatório de vendas anuais agregadas por loja.
     * Busca dados da tabela year_sells e calcula percentuais de participação.
     * 
     * @param startYear Ano de início do período
     * @param endYear Ano de fim do período
     * @return Lista de vendas anuais agregadas por loja com percentuais
     */
    @GetMapping("/yearly-sales")
    @PreAuthorize("hasAnyAuthority('sell:read', 'sell:read:all')")
    public ResponseEntity<List<YearlySalesReportResponse>> getYearlySalesReport(
            @RequestParam @NotNull Integer startYear,
            @RequestParam @NotNull Integer endYear) {
        
        log.info("Recebida solicitação de relatório de vendas anuais: startYear={}, endYear={}", 
                startYear, endYear);
        
        try {
            List<YearlySalesReportResponse> report = yearlySalesReportService.generateReport(startYear, endYear);
            
            log.info("Relatório de vendas anuais processado com sucesso: {} lojas retornadas", 
                    report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetros inválidos para relatório de vendas anuais: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler como 400 Bad Request
        } catch (Exception e) {
            log.error("Erro ao processar relatório de vendas anuais: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para obter dados do gráfico de vendas anuais com métricas calculadas.
     * Busca dados da tabela year_sells e calcula métricas de performance.
     * 
     * @param startYear Ano de início do período
     * @param endYear Ano de fim do período
     * @param storeCode Código da loja específica (opcional, null = todas as lojas)
     * @return Dados do gráfico anual com métricas calculadas
     */
    @GetMapping("/yearly-chart-data-with-metrics")
    @PreAuthorize("hasAnyAuthority('sell:read', 'sell:read:all')")
    public ResponseEntity<ChartDataWithMetricsResponse> getYearlyChartDataWithMetrics(
            @RequestParam @NotNull Integer startYear,
            @RequestParam @NotNull Integer endYear,
            @RequestParam(required = false) String storeCode) {
        
        log.info("Recebida solicitação de dados do gráfico anual com métricas: startYear={}, endYear={}, storeCode={}", 
                startYear, endYear, storeCode);
        
        try {
            ChartDataWithMetricsResponse response = yearlySalesChartService.getChartDataWithMetrics(startYear, endYear, storeCode);
            
            log.info("Dados do gráfico anual com métricas processados com sucesso: {} pontos de dados retornados", 
                    response.chartData().size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetros inválidos para dados do gráfico anual com métricas: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler como 400 Bad Request
        } catch (Exception e) {
            log.error("Erro ao processar dados do gráfico anual com métricas: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
}
