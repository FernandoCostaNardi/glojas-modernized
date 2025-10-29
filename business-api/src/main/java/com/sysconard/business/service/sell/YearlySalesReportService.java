package com.sysconard.business.service.sell;

import com.sysconard.business.dto.sell.YearlySalesReportResponse;
import com.sysconard.business.repository.sell.YearSellRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerar relatórios de vendas anuais.
 * Implementa a lógica de negócio para buscar dados da tabela year_sells
 * e calcular percentuais de participação por loja.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YearlySalesReportService {
    
    private final YearSellRepository yearSellRepository;
    
    /**
     * Gera relatório de vendas anuais agregadas por loja.
     * Busca dados da tabela year_sells e calcula percentuais de participação.
     * 
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @return Lista de vendas agregadas por loja com percentuais
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    public List<YearlySalesReportResponse> generateReport(Integer startYear, Integer endYear) {
        log.info("Gerando relatório de vendas anuais: {} a {}", startYear, endYear);
        
        // Validar parâmetros
        validateParameters(startYear, endYear);
        
        try {
            // Buscar dados agregados por loja
            List<Object[]> rawData = yearSellRepository.findAggregatedByStoreAndPeriod(startYear, endYear);
            
            if (rawData.isEmpty()) {
                log.warn("Nenhum dado encontrado para o período {} a {}", startYear, endYear);
                return List.of();
            }
            
            log.debug("Dados agregados obtidos: {} lojas", rawData.size());
            
            // Mapear para DTOs
            List<YearlySalesReportResponse> salesData = rawData.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            // Calcular percentuais
            List<YearlySalesReportResponse> result = calculatePercentages(salesData);
            
            log.info("Relatório de vendas anuais gerado com sucesso: {} lojas", result.size());
            
            return result;
            
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de vendas anuais: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar relatório de vendas anuais: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida os parâmetros de entrada.
     * 
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    private void validateParameters(Integer startYear, Integer endYear) {
        if (startYear == null) {
            throw new IllegalArgumentException("Ano de início é obrigatório");
        }
        
        if (endYear == null) {
            throw new IllegalArgumentException("Ano de fim é obrigatório");
        }
        
        if (endYear < startYear) {
            throw new IllegalArgumentException("Ano de fim deve ser maior ou igual ao ano de início");
        }
        
        // Validar range de anos
        int currentYear = java.time.LocalDate.now().getYear();
        if (startYear < 2000 || startYear > currentYear + 1) {
            throw new IllegalArgumentException("Ano de início deve estar entre 2000 e " + (currentYear + 1));
        }
        
        if (endYear < 2000 || endYear > currentYear + 1) {
            throw new IllegalArgumentException("Ano de fim deve estar entre 2000 e " + (currentYear + 1));
        }
    }
    
    /**
     * Mapeia um registro agregado para DTO de resposta.
     * 
     * @param rawData Array de objetos retornado pela query SQL
     * @return DTO de resposta
     */
    private YearlySalesReportResponse mapToResponse(Object[] rawData) {
        String storeName = (String) rawData[0];
        BigDecimal total = (BigDecimal) rawData[1];
        
        return YearlySalesReportResponse.builder()
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
    private List<YearlySalesReportResponse> calculatePercentages(List<YearlySalesReportResponse> salesData) {
        // Calcular total geral
        BigDecimal grandTotal = salesData.stream()
                .map(YearlySalesReportResponse::total)
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
     * Calcula percentual de participação para uma loja.
     * 
     * @param sale Dados da loja
     * @param grandTotal Total geral
     * @return DTO com percentual calculado
     */
    private YearlySalesReportResponse calculatePercentage(YearlySalesReportResponse sale, BigDecimal grandTotal) {
        BigDecimal percentage = sale.total()
                .divide(grandTotal, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        
        return YearlySalesReportResponse.builder()
                .storeName(sale.storeName())
                .total(sale.total())
                .percentageOfTotal(percentage)
                .build();
    }
}
