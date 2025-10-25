package com.sysconard.business.service.sell;

import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.sell.StoreReportByDayResponse;
import com.sysconard.business.dto.sell.StoreReportRequest;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável por buscar vendas do dia atual em tempo real.
 * Implementa a lógica de negócio para obter dados diretamente da Legacy API
 * sem gravar no banco, garantindo informações sempre atualizadas.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentDailySalesService {
    
    private final SellService sellService;
    private final StoreService storeService;
    
    /**
     * Obtém vendas do dia atual diretamente da Legacy API.
     * Sempre busca dados frescos, sem cache, garantindo informações atualizadas.
     * 
     * @return Lista de vendas agregadas por loja para o dia atual
     * @throws RuntimeException se houver erro na comunicação ou processamento
     */
    public List<DailySalesReportResponse> getCurrentDailySales() {
        LocalDate today = LocalDate.now();
        
        log.info("Buscando vendas do dia atual em tempo real: {}", today);
        
        try {
            // Passo 1: Verificar se há lojas ativas cadastradas
            List<StoreResponseDto> activeStores = storeService.getAllActiveStores();
            
            if (activeStores.isEmpty()) {
                log.warn("Nenhuma loja ativa encontrada no sistema. Retornando lista vazia.");
                return List.of();
            }
            
            log.debug("Encontradas {} lojas ativas para busca de vendas", activeStores.size());
            
            // Passo 2: Buscar dados frescos da Legacy API
            List<StoreReportByDayResponse> currentDayData = fetchCurrentDayData(today);
            
            // Passo 3: Converter para formato de relatório
            List<DailySalesReportResponse> reportData = mapToReportResponse(currentDayData);
            
            // Passo 4: Garantir que todas as lojas ativas estejam presentes
            List<DailySalesReportResponse> completeReport = ensureAllActiveStoresPresent(reportData);
            
            log.info("Vendas do dia atual obtidas com sucesso: {} lojas no resultado final", 
                    completeReport.size());
            log.debug("Data consultada: {}", today);
            
            return completeReport;
            
        } catch (Exception e) {
            log.error("Erro ao buscar vendas do dia atual {}: {}", today, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar vendas do dia atual: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca dados do dia atual diretamente da Legacy API.
     * 
     * @param today Data atual
     * @return Lista de dados de vendas do dia atual
     */
    private List<StoreReportByDayResponse> fetchCurrentDayData(LocalDate today) {
        log.debug("Buscando dados do dia atual da Legacy API: {}", today);
        
        try {
            // Obter todas as lojas ativas
            List<StoreResponseDto> activeStores = storeService.getAllActiveStores();
            
            if (activeStores.isEmpty()) {
                log.warn("Nenhuma loja ativa encontrada para buscar vendas do dia atual");
                return List.of();
            }
            
            List<String> storeCodes = activeStores.stream()
                    .map(StoreResponseDto::getCode)
                    .collect(Collectors.toList());
            
            log.debug("Lojas ativas para busca: {} lojas", storeCodes.size());
            
            // Criar requisição para o dia atual
            StoreReportRequest reportRequest = StoreReportRequest.builder()
                    .startDate(today)
                    .endDate(today)
                    .storeCodes(storeCodes)
                    .build();
            
            // Buscar dados da Legacy API
            List<StoreReportByDayResponse> currentDayData = sellService.getStoreReportByDay(reportRequest);
            
            log.debug("Dados do dia atual obtidos da Legacy API: {} registros", currentDayData.size());
            
            return currentDayData;
            
        } catch (Exception e) {
            log.error("Erro ao buscar dados da Legacy API para o dia {}: {}", today, e.getMessage(), e);
            
            // Se a Legacy API não estiver disponível, retornar lista vazia
            // O método ensureAllActiveStoresPresent() criará registros zerados
            log.warn("Legacy API indisponível. Retornando dados zerados para todas as lojas ativas.");
            return List.of();
        }
    }
    
    /**
     * Converte dados da Legacy API para formato de relatório.
     * 
     * @param currentDayData Dados brutos da Legacy API
     * @return Lista no formato DailySalesReportResponse
     */
    private List<DailySalesReportResponse> mapToReportResponse(List<StoreReportByDayResponse> currentDayData) {
        log.debug("Convertendo {} registros para formato de relatório", currentDayData.size());
        
        // Agrupar por loja e somar os valores
        Map<String, DailySalesReportResponse> storeAggregates = currentDayData.stream()
                .collect(Collectors.groupingBy(
                        StoreReportByDayResponse::storeName,
                        Collectors.reducing(
                                createEmptyReport(""),
                                this::mapSingleRecord,
                                this::mergeReports
                        )
                ));
        
        // Corrigir o nome da loja (que fica vazio no reducing)
        List<DailySalesReportResponse> result = storeAggregates.entrySet().stream()
                .map(entry -> DailySalesReportResponse.builder()
                        .storeName(entry.getKey())
                        .pdv(entry.getValue().pdv())
                        .danfe(entry.getValue().danfe())
                        .exchange(entry.getValue().exchange())
                        .total(entry.getValue().total())
                        .build())
                .collect(Collectors.toList());
        
        log.debug("Conversão concluída: {} lojas com vendas", result.size());
        
        return result;
    }
    
    /**
     * Converte um registro individual da Legacy API para formato de relatório.
     * 
     * @param record Registro individual da Legacy API
     * @return Objeto DailySalesReportResponse
     */
    private DailySalesReportResponse mapSingleRecord(StoreReportByDayResponse record) {
        BigDecimal pdvValue = record.pdv() != null ? record.pdv() : BigDecimal.ZERO;
        BigDecimal danfeValue = record.danfe() != null ? record.danfe() : BigDecimal.ZERO;
        BigDecimal exchangeValue = record.troca() != null ? record.troca() : BigDecimal.ZERO;
        BigDecimal totalValue = danfeValue.add(pdvValue).subtract(exchangeValue);
        
        return DailySalesReportResponse.builder()
                .storeName(record.storeName())
                .pdv(pdvValue)
                .danfe(danfeValue)
                .exchange(exchangeValue)
                .total(totalValue)
                .build();
    }
    
    /**
     * Mescla dois relatórios (usado para agregação por loja).
     * 
     * @param report1 Primeiro relatório
     * @param report2 Segundo relatório
     * @return Relatório mesclado
     */
    private DailySalesReportResponse mergeReports(DailySalesReportResponse report1, DailySalesReportResponse report2) {
        return DailySalesReportResponse.builder()
                .storeName(report1.storeName().isEmpty() ? report2.storeName() : report1.storeName())
                .pdv(report1.pdv().add(report2.pdv()))
                .danfe(report1.danfe().add(report2.danfe()))
                .exchange(report1.exchange().add(report2.exchange()))
                .total(report1.total().add(report2.total()))
                .build();
    }
    
    /**
     * Cria um relatório vazio para uma loja.
     * 
     * @param storeName Nome da loja
     * @return Relatório com valores zerados
     */
    private DailySalesReportResponse createEmptyReport(String storeName) {
        return DailySalesReportResponse.builder()
                .storeName(storeName)
                .pdv(BigDecimal.ZERO)
                .danfe(BigDecimal.ZERO)
                .exchange(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
    }
    
    
    /**
     * Garante que todas as lojas ativas estejam presentes no relatório,
     * mesmo aquelas sem vendas no dia (com valores zerados).
     * 
     * @param reportData Dados do relatório
     * @return Relatório completo com todas as lojas ativas
     */
    private List<DailySalesReportResponse> ensureAllActiveStoresPresent(List<DailySalesReportResponse> reportData) {
        // Obter todas as lojas ativas
        List<StoreResponseDto> activeStores = storeService.getAllActiveStores();
        
        // Criar mapa dos dados existentes por nome da loja
        Map<String, DailySalesReportResponse> existingData = reportData.stream()
                .collect(Collectors.toMap(
                        DailySalesReportResponse::storeName,
                        report -> report
                ));
        
        // Garantir que todas as lojas ativas estejam presentes
        List<DailySalesReportResponse> completeReport = activeStores.stream()
                .map(store -> existingData.getOrDefault(
                        store.getName(),
                        createEmptyReport(store.getName())
                ))
                .collect(Collectors.toList());
        
        log.debug("Relatório completo: {} lojas ativas incluídas", completeReport.size());
        
        return completeReport;
    }
}
