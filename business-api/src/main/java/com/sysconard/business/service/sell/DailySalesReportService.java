package com.sysconard.business.service.sell;

import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.repository.sell.DailySellRepository;
import com.sysconard.business.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Serviço responsável pela geração de relatórios de vendas diárias.
 * Implementa a lógica de negócio para agregação e consolidação de dados de vendas.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailySalesReportService {
    
    private final DailySellRepository dailySellRepository;
    private final StoreService storeService;
    
    /**
     * Gera relatório de vendas diárias agregadas por loja em um período específico.
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Lista de vendas agregadas por loja
     * @throws IllegalArgumentException se as datas forem inválidas
     */
    public List<DailySalesReportResponse> generateReport(LocalDate startDate, LocalDate endDate) {
        log.info("Gerando relatório de vendas diárias: {} até {}", startDate, endDate);
        
        // Validação de parâmetros
        validateDateRange(startDate, endDate);
        
        try {
            // Buscar dados agregados do banco
            List<DailySalesReportResponse> bankData = dailySellRepository.findAggregatedSalesByDateRange(startDate, endDate);
            
            log.debug("Dados encontrados no banco: {} lojas", bankData.size());
            
            // Garantir que todas as lojas ativas estejam presentes na resposta
            List<DailySalesReportResponse> completeReport = ensureAllActiveStoresPresent(bankData);
            
            log.info("Relatório gerado com sucesso: {} lojas no resultado final (incluindo zeros)", completeReport.size());
            log.debug("Período consultado: {} até {}", startDate, endDate);
            
            return completeReport;
            
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de vendas diárias para o período {} até {}: {}", 
                     startDate, endDate, e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar relatório de vendas diárias: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida se o range de datas é válido.
     * 
     * @param startDate Data de início
     * @param endDate Data de fim
     * @throws IllegalArgumentException se as datas forem inválidas
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Data de início não pode ser nula");
        }
        
        if (endDate == null) {
            throw new IllegalArgumentException("Data de fim não pode ser nula");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data de início deve ser anterior ou igual à data de fim");
        }
        
        // Validar se as datas não são muito antigas (opcional - ajustar conforme necessário)
        LocalDate minDate = LocalDate.now().minusYears(5);
        if (startDate.isBefore(minDate)) {
            throw new IllegalArgumentException("Data de início não pode ser anterior a " + minDate);
        }
        
        // Validar se as datas não são futuras
        LocalDate maxDate = LocalDate.now().plusDays(1);
        if (endDate.isAfter(maxDate)) {
            throw new IllegalArgumentException("Data de fim não pode ser posterior a " + maxDate);
        }
        
        log.debug("Validação de datas concluída: {} até {}", startDate, endDate);
    }
    
    /**
     * Garante que todas as lojas ativas estejam presentes na resposta do relatório.
     * Para lojas ausentes, cria registros com valores zerados.
     * 
     * @param bankData Dados agregados retornados do banco
     * @return Lista completa com todas as lojas ativas
     */
    private List<DailySalesReportResponse> ensureAllActiveStoresPresent(List<DailySalesReportResponse> bankData) {
        log.debug("Validando completude da resposta: {} lojas encontradas no banco", bankData.size());
        
        try {
            // 1. Buscar todas as lojas ativas
            List<StoreResponseDto> activeStores = storeService.getAllActiveStores();
            
            if (activeStores.isEmpty()) {
                log.warn("Nenhuma loja ativa encontrada no sistema");
                return bankData;
            }
            
            log.debug("Lojas ativas encontradas: {}", activeStores.size());
            
            // 2. Indexar dados do banco por nome da loja
            Map<String, DailySalesReportResponse> bankDataMap = indexBankDataByStoreName(bankData);
            
            // 3. Gerar resposta completa com todas as lojas ativas
            List<DailySalesReportResponse> completeResponse = activeStores.stream()
                .map(store -> bankDataMap.getOrDefault(store.getName(), createZeroedResponse(store.getName())))
                .sorted(Comparator.comparing(DailySalesReportResponse::storeName))
                .collect(Collectors.toList());
            
            int addedStores = completeResponse.size() - bankData.size();
            if (addedStores > 0) {
                log.info("Adicionadas {} lojas com valores zerados para garantir completude", addedStores);
            }
            
            log.debug("Resposta completa: {} lojas no resultado final", completeResponse.size());
            
            return completeResponse;
            
        } catch (Exception e) {
            log.error("Erro ao validar completude das lojas ativas: {}", e.getMessage(), e);
            // Em caso de erro, retornar dados originais para não quebrar o fluxo
            log.warn("Retornando dados originais do banco devido ao erro na validação");
            return bankData;
        }
    }
    
    /**
     * Indexa dados do banco por nome da loja para consulta eficiente.
     * 
     * @param bankData Dados retornados do banco
     * @return Map indexado por nome da loja
     */
    private Map<String, DailySalesReportResponse> indexBankDataByStoreName(List<DailySalesReportResponse> bankData) {
        return bankData.stream()
            .filter(data -> data.storeName() != null)
            .collect(Collectors.toMap(
                DailySalesReportResponse::storeName,
                data -> data,
                (existing, replacement) -> {
                    log.warn("Nome de loja duplicado encontrado: {}. Mantendo primeiro registro.", existing.storeName());
                    return existing;
                }
            ));
    }
    
    /**
     * Cria uma resposta com valores zerados para uma loja específica.
     * 
     * @param storeName Nome da loja
     * @return DailySalesReportResponse com valores zerados
     */
    private DailySalesReportResponse createZeroedResponse(String storeName) {
        return DailySalesReportResponse.builder()
            .storeName(storeName)
            .pdv(BigDecimal.ZERO)
            .danfe(BigDecimal.ZERO)
            .exchange(BigDecimal.ZERO)
            .total(BigDecimal.ZERO)
            .build();
    }
}
