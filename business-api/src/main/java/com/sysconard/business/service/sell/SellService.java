package com.sysconard.business.service.sell;

import com.sysconard.business.dto.operation.OperationResponse;
import com.sysconard.business.dto.origin.EventOriginResponse;
import com.sysconard.business.dto.sell.StoreReportRequest;
import com.sysconard.business.dto.sell.StoreReportResponse;
import com.sysconard.business.dto.sell.StoreReportByDayResponse;
import com.sysconard.business.exception.sell.StoreReportException;
import com.sysconard.business.service.operation.OperationService;
import com.sysconard.business.service.origin.EventOriginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Serviço responsável por operações de vendas.
 * Realiza comunicação com a Legacy API para obter relatórios de vendas por loja.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SellService {
    
    @Qualifier("legacyApiWebClient")
    private final WebClient legacyApiWebClient;
    
    @Value("${legacy-api.timeout:30}")
    private Integer timeoutSeconds;
    
    private static final String STORE_REPORT_ENDPOINT = "/api/legacy/sales/store-report";
    private static final String STORE_REPORT_BY_DAY_ENDPOINT = "/api/legacy/sales/store-report-by-day";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Autowired
    private  EventOriginService originService;
    @Autowired
    private  OperationService operationService;

    
    /**
     * Obtém relatório de vendas por loja consumindo a Legacy API.
     * 
     * @param request Parâmetros para geração do relatório
     * @return Lista de dados de vendas agregados por loja
     * @throws StoreReportException em caso de erro na comunicação ou processamento
     */
    public List<StoreReportResponse> getStoreReport(StoreReportRequest request) {
        log.info("Solicitando relatório de vendas por loja: startDate={}, endDate={}, storeCodes={}", 
                request.startDate(), request.endDate(), request.storeCodes());
        
        try {
            // 1. Validar entrada
            validateRequest(request);
            
            // 2. Converter para formato da Legacy API
            Map<String, Object> legacyRequest = buildLegacyRequest(request);
            
            // 3. Fazer chamada HTTP para Legacy API
            List<Map<String, Object>> legacyResponse = callLegacyApi(legacyRequest);
            
            // 4. Converter resposta para nosso formato
            List<StoreReportResponse> response = mapToStoreReportResponse(legacyResponse);
            
            log.info("Relatório de vendas obtido com sucesso: {} lojas processadas", response.size());
            
            return response;
            
        } catch (StoreReportException e) {
            log.error("Erro ao obter relatório de vendas: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao obter relatório de vendas: {}", e.getMessage(), e);
            throw new StoreReportException("Erro interno ao processar relatório de vendas", e);
        }
    }
    
    /**
     * Valida os parâmetros da requisição
     * 
     * @param request Requisição a ser validada
     * @throws StoreReportException se houver problemas de validação
     */
    private void validateRequest(StoreReportRequest request) {
        if (request.startDate().isAfter(LocalDate.now())) {
            throw new StoreReportException("Data de início não pode ser futura");
        }
        
        if (request.endDate().isAfter(LocalDate.now())) {
            throw new StoreReportException("Data de fim não pode ser futura");
        }
        
        if (request.storeCodes().size() > 50) {
            throw new StoreReportException("Máximo de 50 lojas por requisição");
        }
    }
    
    /**
     * Constrói a requisição no formato esperado pela Legacy API
     * 
     * @param request Nossa requisição
     * @return Map no formato da Legacy API
     */
    private Map<String, Object> buildLegacyRequest(StoreReportRequest request) {
        Map<String, Object> legacyRequest = new HashMap<>();
        
        legacyRequest.put("startDate", request.startDate().format(DATE_FORMATTER));
        legacyRequest.put("endDate", request.endDate().format(DATE_FORMATTER));
        legacyRequest.put("storeCodes", request.storeCodes());
        
        // CORRIGIDO: Valores que correspondem exatamente à query SQL da Legacy API
        log.info("USANDO VALORES CORRETOS baseados na query SQL");
        legacyRequest.put("danfeOrigin", List.of("015", "002"));  // ORICOD para DANFE
        legacyRequest.put("pdvOrigin", List.of("009"));           // ORICOD para PDV
        legacyRequest.put("exchangeOrigin", List.of("051", "065")); // ORICOD para TROCA
        legacyRequest.put("sellOperation", List.of("000999", "000007", "000001", "000045", "000054", "000062", "000063", "000064", "000065", "000067", "000068", "000069", "000071")); // OPECOD para PDV/DANFE
        legacyRequest.put("exchangeOperation", List.of("000015", "000048")); // OPECOD para TROCA
               
        return legacyRequest;
    }
    
    /**
     * Realiza a chamada HTTP para a Legacy API
     * 
     * @param legacyRequest Requisição formatada
     * @return Resposta da Legacy API
     * @throws StoreReportException em caso de erro HTTP
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<Map<String, Object>> callLegacyApi(Map<String, Object> legacyRequest) {
        log.info("=== DEBUGGING WEBCLIENT ===");
        log.info("Endpoint sendo chamado: {}", STORE_REPORT_ENDPOINT);
        log.info("WebClient base URL: {}", legacyApiWebClient.mutate().build());
        log.info("Enviando requisição para Legacy API: {}", legacyRequest);
        
        try {
            Mono<List> responseMono = legacyApiWebClient
                    .post()
                    .uri(STORE_REPORT_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(legacyRequest)
                    .retrieve()
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(120)); // Aumentado para 2 minutos temporariamente
            
            List<Map<String, Object>> response = (List<Map<String, Object>>) responseMono.block();
            
            log.debug("Resposta recebida da Legacy API: {} registros", 
                    response != null ? response.size() : 0);
            
            return response != null ? response : List.of();
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Erro HTTP %d ao chamar Legacy API: %s", 
                    e.getStatusCode().value(), e.getResponseBodyAsString());
            log.error(errorMsg);
            throw new StoreReportException(errorMsg, e);
            
        } catch (Exception e) {
            String errorMsg = "Erro de comunicação com Legacy API: " + e.getMessage();
            log.error(errorMsg, e);
            throw new StoreReportException(errorMsg, e);
        }
    }
    
    /**
     * Converte a resposta da Legacy API para nosso formato
     * 
     * @param legacyResponse Resposta da Legacy API
     * @return Lista de StoreReportResponse
     */
    private List<StoreReportResponse> mapToStoreReportResponse(List<Map<String, Object>> legacyResponse) {
        return legacyResponse.stream()
                .map(this::mapSingleStoreReport)
                .toList();
    }
    
    /**
     * Converte um item da resposta da Legacy API
     * 
     * @param legacyItem Item da Legacy API
     * @return StoreReportResponse
     */
    private StoreReportResponse mapSingleStoreReport(Map<String, Object> legacyItem) {
        try {
            String storeName = (String) legacyItem.get("storeName");
            String storeCode = (String) legacyItem.get("storeCode");
            
            BigDecimal danfe = convertToBigDecimal(legacyItem.get("danfe"));
            BigDecimal pdv = convertToBigDecimal(legacyItem.get("pdv"));
            BigDecimal troca = convertToBigDecimal(legacyItem.get("troca3"));
            
            return StoreReportResponse.builder()
                    .storeName(storeName)
                    .storeCode(storeCode)
                    .danfe(danfe)
                    .pdv(pdv)
                    .troca(troca)
                    .build();
                    
        } catch (Exception e) {
            log.error("Erro ao mapear item da resposta Legacy: {}", legacyItem, e);
            throw new StoreReportException("Erro ao processar dados da loja", e);
        }
    }
    
    /**
     * Converte valor para BigDecimal de forma segura
     * 
     * @param value Valor a ser convertido
     * @return BigDecimal ou ZERO se conversão falhar
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        
        try {
            if (value instanceof Number number) {
                return BigDecimal.valueOf(number.doubleValue());
            }
            
            if (value instanceof String stringValue) {
                return new BigDecimal(stringValue);
            }
            
            return BigDecimal.valueOf(Double.parseDouble(value.toString()));
            
        } catch (Exception e) {
            log.warn("Erro ao converter valor '{}' para BigDecimal, usando ZERO", value);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Obtém relatório de vendas por loja e por dia consumindo a Legacy API.
     * 
     * @param request Parâmetros para geração do relatório
     * @return Lista de dados de vendas agregados por loja e por dia
     * @throws StoreReportException em caso de erro na comunicação ou processamento
     */
    public List<StoreReportByDayResponse> getStoreReportByDay(StoreReportRequest request) {
        log.info("Solicitando relatório de vendas por loja e por dia: startDate={}, endDate={}, storeCodes={}", 
                request.startDate(), request.endDate(), request.storeCodes());
        
        try {
            // 1. Validar entrada
            validateRequest(request);
            
            // 2. Converter para formato da Legacy API
            Map<String, Object> legacyRequest = buildLegacyRequest(request);
            
            // 3. Fazer chamada HTTP para Legacy API (endpoint por dia)
            List<Map<String, Object>> legacyResponse = callLegacyApiByDay(legacyRequest);
            
            // 4. Converter resposta para nosso formato
            List<StoreReportByDayResponse> response = mapToStoreReportByDayResponse(legacyResponse);
            
            // 5. Garantir que todas as lojas e datas solicitadas estejam presentes
            response = ensureAllStoresAndDatesPresent(response, request);
            
            log.info("Relatório de vendas por dia obtido com sucesso: {} registros processados (incluindo zeros)", response.size());
            
            return response;
            
        } catch (StoreReportException e) {
            log.error("Erro ao obter relatório de vendas por dia: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao obter relatório de vendas por dia: {}", e.getMessage(), e);
            throw new StoreReportException("Erro interno ao processar relatório de vendas por dia", e);
        }
    }
    
    /**
     * Realiza a chamada HTTP para a Legacy API (endpoint por dia)
     * 
     * @param legacyRequest Requisição formatada
     * @return Resposta da Legacy API
     * @throws StoreReportException em caso de erro HTTP
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<Map<String, Object>> callLegacyApiByDay(Map<String, Object> legacyRequest) {
        log.info("=== DEBUGGING WEBCLIENT BY DAY ===");
        log.info("Endpoint sendo chamado: {}", STORE_REPORT_BY_DAY_ENDPOINT);
        log.info("Enviando requisição para Legacy API (por dia): {}", legacyRequest);
        
        try {
            Mono<List> responseMono = legacyApiWebClient
                    .post()
                    .uri(STORE_REPORT_BY_DAY_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(legacyRequest)
                    .retrieve()
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(120)); // Aumentado para 2 minutos temporariamente
            
            List<Map<String, Object>> response = (List<Map<String, Object>>) responseMono.block();
            
            log.debug("Resposta recebida da Legacy API (por dia): {} registros", 
                    response != null ? response.size() : 0);
            
            return response != null ? response : List.of();
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Erro HTTP %d ao chamar Legacy API (por dia): %s", 
                    e.getStatusCode().value(), e.getResponseBodyAsString());
            log.error(errorMsg);
            throw new StoreReportException(errorMsg, e);
            
        } catch (Exception e) {
            String errorMsg = "Erro de comunicação com Legacy API (por dia): " + e.getMessage();
            log.error(errorMsg, e);
            throw new StoreReportException(errorMsg, e);
        }
    }
    
    /**
     * Converte a resposta da Legacy API para nosso formato (por dia)
     * 
     * @param legacyResponse Resposta da Legacy API
     * @return Lista de StoreReportByDayResponse
     */
    private List<StoreReportByDayResponse> mapToStoreReportByDayResponse(List<Map<String, Object>> legacyResponse) {
        return legacyResponse.stream()
                .map(this::mapSingleStoreReportByDay)
                .toList();
    }
    
    /**
     * Converte um item da resposta da Legacy API (por dia)
     * 
     * @param legacyItem Item da Legacy API
     * @return StoreReportByDayResponse
     */
    private StoreReportByDayResponse mapSingleStoreReportByDay(Map<String, Object> legacyItem) {
        try {
            String storeName = (String) legacyItem.get("storeName");
            String storeCode = (String) legacyItem.get("storeCode");
            
            // Converter data de string para LocalDate
            LocalDate reportDate = null;
            Object dateValue = legacyItem.get("reportDate");
            if (dateValue instanceof String dateString) {
                reportDate = LocalDate.parse(dateString);
            } else if (dateValue instanceof LocalDate localDate) {
                reportDate = localDate;
            }
            
            BigDecimal danfe = convertToBigDecimal(legacyItem.get("danfe"));
            BigDecimal pdv = convertToBigDecimal(legacyItem.get("pdv"));
            BigDecimal troca = convertToBigDecimal(legacyItem.get("troca3"));
            
            return StoreReportByDayResponse.builder()
                    .storeName(storeName)
                    .storeCode(storeCode)
                    .reportDate(reportDate)
                    .danfe(danfe)
                    .pdv(pdv)
                    .troca(troca)
                    .build();
                    
        } catch (Exception e) {
            log.error("Erro ao mapear item da resposta Legacy (por dia): {}", legacyItem, e);
            throw new StoreReportException("Erro ao processar dados da loja por dia", e);
        }
    }
    
    /**
     * Record para chave composta de loja e data (Java 17)
     */
    private record StoreAndDateKey(String storeCode, LocalDate date) {}
    
    /**
     * Garante que todas as lojas e datas solicitadas estejam presentes na resposta.
     * Para combinações ausentes, cria registros com valores zerados.
     * 
     * @param legacyResponse Resposta recebida da Legacy API
     * @param request Request original com lojas e período solicitados
     * @return Lista completa com todas as combinações de loja + data
     */
    private List<StoreReportByDayResponse> ensureAllStoresAndDatesPresent(
            List<StoreReportByDayResponse> legacyResponse, StoreReportRequest request) {
        
        log.debug("Validando completude da resposta: {} registros recebidos para {} lojas no período {} a {}", 
                 legacyResponse.size(), request.storeCodes().size(), request.startDate(), request.endDate());
        
        // 1. Gerar todas as combinações esperadas
        Set<StoreAndDateKey> expectedCombinations = generateExpectedCombinations(request);
        
        // 2. Indexar dados reais por chave composta
        Map<StoreAndDateKey, StoreReportByDayResponse> realDataMap = indexRealData(legacyResponse);
        
        // 3. Extrair nomes das lojas dos dados reais
        Map<String, String> storeNamesMap = extractStoreNames(legacyResponse);
        
        // 4. Gerar lista completa
        List<StoreReportByDayResponse> completeResponse = expectedCombinations.stream()
            .map(key -> realDataMap.getOrDefault(key, createZeroedResponse(key, storeNamesMap)))
            .sorted(Comparator.comparing(StoreReportByDayResponse::storeName)
                             .thenComparing(StoreReportByDayResponse::reportDate))
            .collect(Collectors.toList());
        
        int addedRecords = completeResponse.size() - legacyResponse.size();
        if (addedRecords > 0) {
            log.info("Adicionados {} registros zerados para garantir completude da resposta", addedRecords);
        }
        
        log.debug("Resposta completa: {} registros para {} lojas", completeResponse.size(), request.storeCodes().size());
        
        return completeResponse;
    }
    
    /**
     * Gera todas as combinações esperadas de loja + data do período.
     * 
     * @param request Request com lojas e período
     * @return Set de chaves compostas esperadas
     */
    private Set<StoreAndDateKey> generateExpectedCombinations(StoreReportRequest request) {
        Set<StoreAndDateKey> combinations = new HashSet<>();
        
        LocalDate currentDate = request.startDate();
        
        while (!currentDate.isAfter(request.endDate())) {
            for (String storeCode : request.storeCodes()) {
                combinations.add(new StoreAndDateKey(storeCode, currentDate));
            }
            currentDate = currentDate.plusDays(1);
        }
        
        log.debug("Geradas {} combinações esperadas de loja + data", combinations.size());
        return combinations;
    }
    
    /**
     * Indexa dados reais por chave composta para consulta eficiente.
     * 
     * @param legacyResponse Dados recebidos da Legacy API
     * @return Map indexado por StoreAndDateKey
     */
    private Map<StoreAndDateKey, StoreReportByDayResponse> indexRealData(
            List<StoreReportByDayResponse> legacyResponse) {
        
        return legacyResponse.stream()
            .filter(response -> response.storeCode() != null && response.reportDate() != null)
            .collect(Collectors.toMap(
                response -> new StoreAndDateKey(response.storeCode(), response.reportDate()),
                response -> response,
                (existing, replacement) -> {
                    log.warn("Chave duplicada encontrada: loja={}, data={}. Mantendo primeiro registro.", 
                            existing.storeCode(), existing.reportDate());
                    return existing;
                }
            ));
    }
    
    /**
     * Extrai nomes das lojas dos dados reais para usar em registros zerados.
     * 
     * @param legacyResponse Dados recebidos da Legacy API
     * @return Map de código -> nome da loja
     */
    private Map<String, String> extractStoreNames(List<StoreReportByDayResponse> legacyResponse) {
        return legacyResponse.stream()
            .filter(response -> response.storeCode() != null && response.storeName() != null)
            .collect(Collectors.toMap(
                StoreReportByDayResponse::storeCode,
                StoreReportByDayResponse::storeName,
                (existing, replacement) -> existing // Manter primeiro nome encontrado
            ));
    }
    
    /**
     * Cria um registro com valores zerados para loja e data específicas.
     * 
     * @param key Chave composta com código da loja e data
     * @param storeNamesMap Map com nomes das lojas
     * @return StoreReportByDayResponse com valores zerados
     */
    private StoreReportByDayResponse createZeroedResponse(
            StoreAndDateKey key, Map<String, String> storeNamesMap) {
        
        String storeName = storeNamesMap.getOrDefault(key.storeCode(), "Loja " + key.storeCode());
        
        return StoreReportByDayResponse.builder()
            .storeName(storeName)
            .storeCode(key.storeCode())
            .reportDate(key.date())
            .danfe(BigDecimal.ZERO)
            .pdv(BigDecimal.ZERO)
            .troca(BigDecimal.ZERO)
            .build();
    }
}
