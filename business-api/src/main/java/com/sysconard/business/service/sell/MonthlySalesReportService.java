package com.sysconard.business.service.sell;

import com.sysconard.business.dto.sell.MonthlySalesReportResponse;
import com.sysconard.business.repository.sell.MonthlySellRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerar relatórios de vendas mensais.
 * Implementa a lógica de negócio para buscar dados da tabela monthly_sells
 * e calcular percentuais de participação por loja.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlySalesReportService {
    
    private final MonthlySellRepository monthlySellRepository;
    
    /**
     * Gera relatório de vendas mensais agregadas por loja.
     * Busca dados da tabela monthly_sells e calcula percentuais de participação.
     * 
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @return Lista de vendas agregadas por loja com percentuais
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    public List<MonthlySalesReportResponse> generateReport(String startYearMonth, String endYearMonth) {
        log.info("Gerando relatório de vendas mensais: {} a {}", startYearMonth, endYearMonth);
        
        // Validar parâmetros
        validateParameters(startYearMonth, endYearMonth);
        
        try {
            // Buscar dados agregados por loja
            List<Object[]> rawData = monthlySellRepository.findAggregatedByStoreAndPeriod(startYearMonth, endYearMonth);
            
            if (rawData.isEmpty()) {
                log.warn("Nenhum dado encontrado para o período {} a {}", startYearMonth, endYearMonth);
                return List.of();
            }
            
            log.debug("Dados agregados obtidos: {} lojas", rawData.size());
            
            // Mapear para DTOs
            List<MonthlySalesReportResponse> salesData = rawData.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            // Calcular percentuais
            List<MonthlySalesReportResponse> result = calculatePercentages(salesData);
            
            log.info("Relatório de vendas mensais gerado com sucesso: {} lojas", result.size());
            
            return result;
            
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de vendas mensais: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar relatório de vendas mensais: " + e.getMessage(), e);
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
     * Mapeia um registro agregado para DTO de resposta.
     * 
     * @param rawData Array de objetos retornado pela query SQL
     * @return DTO de resposta
     */
    private MonthlySalesReportResponse mapToResponse(Object[] rawData) {
        String storeName = (String) rawData[0];
        BigDecimal total = (BigDecimal) rawData[1];
        
        return MonthlySalesReportResponse.builder()
                .storeName(storeName)
                .total(total != null ? total : BigDecimal.ZERO)
                .percentageOfTotal(BigDecimal.ZERO) // Será calculado depois
                .build();
    }
    
    /**
     * Calcula percentuais de participação para cada loja.
     * 
     * @param salesData Lista de vendas por loja
     * @return Lista com percentuais calculados
     */
    private List<MonthlySalesReportResponse> calculatePercentages(List<MonthlySalesReportResponse> salesData) {
        // Calcular total geral
        BigDecimal grandTotal = salesData.stream()
                .map(MonthlySalesReportResponse::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        log.debug("Total geral calculado: {}", grandTotal);
        
        if (grandTotal.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Total geral é zero, retornando dados sem percentuais");
            return salesData;
        }
        
        // Calcular percentuais
        return salesData.stream()
                .map(sale -> calculatePercentage(sale, grandTotal))
                .collect(Collectors.toList());
    }
    
    /**
     * Gera relatório de vendas mensais agregadas por loja usando LocalDate.
     * Converte as datas para formato YYYY-MM e chama o método principal.
     * 
     * @param startDate Data de início
     * @param endDate Data de fim
     * @return Lista de vendas agregadas por loja com percentuais
     */
    public List<MonthlySalesReportResponse> getMonthlySalesReport(LocalDate startDate, LocalDate endDate) {
        String startYearMonth = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String endYearMonth = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        return generateReport(startYearMonth, endYearMonth);
    }
    
    /**
     * Calcula percentual de participação para uma loja.
     * 
     * @param sale Dados da loja
     * @param grandTotal Total geral
     * @return DTO com percentual calculado
     */
    private MonthlySalesReportResponse calculatePercentage(MonthlySalesReportResponse sale, BigDecimal grandTotal) {
        BigDecimal percentage = sale.total()
                .divide(grandTotal, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        
        return MonthlySalesReportResponse.builder()
                .storeName(sale.storeName())
                .total(sale.total())
                .percentageOfTotal(percentage)
                .build();
    }
}
