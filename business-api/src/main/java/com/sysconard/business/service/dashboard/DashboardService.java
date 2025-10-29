package com.sysconard.business.service.dashboard;

import com.sysconard.business.dto.dashboard.DashboardSummaryResponse;
import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.repository.sell.MonthlySellRepository;
import com.sysconard.business.repository.sell.YearSellRepository;
import com.sysconard.business.service.sell.CurrentDailySalesService;
import com.sysconard.business.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Serviço responsável por fornecer dados consolidados para o dashboard.
 * Agrega informações de vendas diárias, mensais, anuais e contagem de lojas ativas.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final CurrentDailySalesService currentDailySalesService;
    private final MonthlySellRepository monthlySellRepository;
    private final YearSellRepository yearSellRepository;
    private final StoreService storeService;
    
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    /**
     * Obtém resumo completo do dashboard com todas as métricas principais.
     * Agrega dados de vendas e contagem de lojas ativas em uma única operação.
     * 
     * @return Resumo consolidado do dashboard
     */
    public DashboardSummaryResponse getDashboardSummary() {
        log.info("Gerando resumo do dashboard");
        
        try {
            // Obter métricas em paralelo para melhor performance
            BigDecimal totalSalesToday = getTotalSalesToday();
            BigDecimal totalSalesMonth = getTotalSalesMonth();
            BigDecimal totalSalesYear = getTotalSalesYear();
            Integer activeStoresCount = getActiveStoresCount();
            
            DashboardSummaryResponse summary = new DashboardSummaryResponse(
                totalSalesToday,
                totalSalesMonth,
                totalSalesYear,
                activeStoresCount
            );
            
            log.info("Resumo do dashboard gerado com sucesso: vendas hoje={}, mês={}, ano={}, lojas={}", 
                    totalSalesToday, totalSalesMonth, totalSalesYear, activeStoresCount);
            
            return summary;
            
        } catch (Exception e) {
            log.error("Erro ao gerar resumo do dashboard: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar resumo do dashboard: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calcula total de vendas do dia atual.
     * Utiliza o serviço de vendas em tempo real para dados sempre atualizados.
     * 
     * @return Total de vendas do dia atual
     */
    private BigDecimal getTotalSalesToday() {
        log.debug("Calculando total de vendas do dia atual");
        
        try {
            List<DailySalesReportResponse> dailySales = currentDailySalesService.getCurrentDailySales();
            
            BigDecimal total = dailySales.stream()
                    .map(DailySalesReportResponse::total)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("Total de vendas do dia atual: {}", total);
            return total;
            
        } catch (Exception e) {
            log.error("Erro ao calcular vendas do dia atual: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcula total de vendas do mês atual.
     * Consulta a tabela monthly_sells para dados agregados.
     * 
     * @return Total de vendas do mês atual
     */
    private BigDecimal getTotalSalesMonth() {
        log.debug("Calculando total de vendas do mês atual");
        
        try {
            String currentYearMonth = LocalDate.now().format(YEAR_MONTH_FORMATTER);
            BigDecimal total = monthlySellRepository.sumTotalByYearMonth(currentYearMonth);
            
            log.debug("Total de vendas do mês atual ({}): {}", currentYearMonth, total);
            return total;
            
        } catch (Exception e) {
            log.error("Erro ao calcular vendas do mês atual: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcula total de vendas do ano atual.
     * Consulta a tabela year_sells para dados agregados.
     * 
     * @return Total de vendas do ano atual
     */
    private BigDecimal getTotalSalesYear() {
        log.debug("Calculando total de vendas do ano atual");
        
        try {
            Integer currentYear = LocalDate.now().getYear();
            BigDecimal total = yearSellRepository.sumTotalByYear(currentYear);
            
            log.debug("Total de vendas do ano atual ({}): {}", currentYear, total);
            return total;
            
        } catch (Exception e) {
            log.error("Erro ao calcular vendas do ano atual: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Conta o número de lojas ativas no sistema.
     * Utiliza o serviço de lojas para obter dados atualizados.
     * 
     * @return Número de lojas ativas
     */
    private Integer getActiveStoresCount() {
        log.debug("Contando lojas ativas");
        
        try {
            List<StoreResponseDto> activeStores = storeService.getAllActiveStores();
            Integer count = activeStores.size();
            
            log.debug("Número de lojas ativas: {}", count);
            return count;
            
        } catch (Exception e) {
            log.error("Erro ao contar lojas ativas: {}", e.getMessage(), e);
            return 0;
        }
    }
}
