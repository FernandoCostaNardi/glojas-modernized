package com.sysconard.business.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sysconard.business.dto.config.LegacyApiResponseDTO;
import com.sysconard.business.dto.operation.OperationKindDto;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.dto.StockPageResponseDTO;
import com.sysconard.business.dto.StockItemResponseDTO;
import com.sysconard.business.dto.PurchaseAnalysisPageResponseDTO;
import com.sysconard.business.dto.CriticalStockPageResponseDTO;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Cliente para comunicação com a Legacy API
 * Encapsula todas as chamadas para o serviço legacy
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Component
public class LegacyApiClient {
    
    private final WebClient legacyApiWebClient;
    
    @Value("${legacy-api.timeout:30}")
    private Integer timeoutSeconds;
    
    public LegacyApiClient(@Qualifier("legacyApiWebClient") WebClient legacyApiWebClient) {
        this.legacyApiWebClient = legacyApiWebClient;
    }
    
    /**
     * Busca produtos registrados na Legacy API com filtros e paginação
     * 
     * @param secao Filtro por seção (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Resposta da Legacy API
     * @throws RuntimeException Se houver erro na comunicação
     */
    public LegacyApiResponseDTO getRegisteredProducts(
            String secao, String grupo, String marca, String descricao,
            Integer page, Integer size, String sortBy, String sortDir) {
        
        log.info("Buscando produtos registrados na Legacy API - página: {}, tamanho: {}", page, size);
        
        try {
            return legacyApiWebClient
                    .get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/products/registered");
                        
                        // Adicionar parâmetros obrigatórios
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        builder.queryParam("sortBy", sortBy != null ? sortBy : "codigo");
                        builder.queryParam("sortDir", sortDir != null ? sortDir : "asc");
                        
                        // Adicionar filtros opcionais
                        if (secao != null && !secao.trim().isEmpty()) {
                            builder.queryParam("secao", secao);
                        }
                        if (grupo != null && !grupo.trim().isEmpty()) {
                            builder.queryParam("grupo", grupo);
                        }
                        if (marca != null && !marca.trim().isEmpty()) {
                            builder.queryParam("marca", marca);
                        }
                        if (descricao != null && !descricao.trim().isEmpty()) {
                            builder.queryParam("descricao", descricao);
                        }
                        
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(LegacyApiResponseDTO.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .doOnSuccess(response -> log.info("Sucesso ao buscar produtos. Total encontrado: {}", 
                            response != null ? response.getTotalElements() : "N/A"))
                    .doOnError(error -> log.error("Erro ao buscar produtos na Legacy API", error))
                    .block();
                    
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao chamar Legacy API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao buscar produtos na Legacy API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar Legacy API", e);
            throw new RuntimeException("Erro inesperado ao buscar produtos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca tipos de operação na Legacy API
     * 
     * @return Lista de tipos de operação
     * @throws RuntimeException Se houver erro na comunicação
     */
    public List<OperationKindDto> getOperationKinds() {
        log.info("Buscando tipos de operação na Legacy API");
        
        try {
            return legacyApiWebClient
                    .get()
                    .uri("/operations")
                    .retrieve()
                    .bodyToFlux(OperationKindDto.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .doOnSuccess(response -> log.info("Sucesso ao buscar tipos de operação. Total encontrado: {}", 
                            response != null ? response.size() : "N/A"))
                    .doOnError(error -> log.error("Erro ao buscar tipos de operação na Legacy API", error))
                    .block();
                    
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao chamar Legacy API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao buscar tipos de operação na Legacy API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar Legacy API", e);
            throw new RuntimeException("Erro inesperado ao buscar tipos de operação: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca lojas na Legacy API
     * 
     * @return Lista de lojas com id, name e city
     * @throws RuntimeException Se houver erro na comunicação
     */
    public List<StoreResponseDto> getStores() {
        log.info("Buscando lojas na Legacy API");
        
        try {
            return legacyApiWebClient
                    .get()
                    .uri("/stores")
                    .retrieve()
                    .bodyToFlux(StoreResponseDto.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(60)) // Aumentado para 60 segundos
                    .doOnSuccess(response -> log.info("Sucesso ao buscar lojas. Total encontrado: {}", 
                            response != null ? response.size() : "N/A"))
                    .doOnError(error -> log.error("Erro ao buscar lojas na Legacy API", error))
                    .onErrorReturn(List.of()) // Fallback para lista vazia em caso de erro
                    .block();
                    
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao chamar Legacy API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao buscar lojas na Legacy API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar Legacy API", e);
            throw new RuntimeException("Erro inesperado ao buscar lojas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca estoque na Legacy API com filtros e paginação
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Resposta da Legacy API com dados de estoque
     * @throws RuntimeException Se houver erro na comunicação
     */
    public StockPageResponseDTO getStocks(
            String refplu, String marca, String descricao, Boolean hasStock,
            Integer page, Integer size, String sortBy, String sortDir) {
        
        log.info("Buscando estoque na Legacy API - página: {}, tamanho: {}, hasStock: {}", page, size, hasStock);
        
        try {
            return legacyApiWebClient
                    .get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/stocks");
                        
                        // Adicionar parâmetros obrigatórios
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 15);
                        builder.queryParam("sortBy", sortBy != null ? sortBy : "refplu");
                        builder.queryParam("sortDir", sortDir != null ? sortDir : "asc");
                        builder.queryParam("hasStock", hasStock != null ? hasStock : true);
                        
                        // Adicionar filtros opcionais
                        if (refplu != null && !refplu.trim().isEmpty()) {
                            builder.queryParam("refplu", refplu);
                        }
                        if (marca != null && !marca.trim().isEmpty()) {
                            builder.queryParam("marca", marca);
                        }
                        if (descricao != null && !descricao.trim().isEmpty()) {
                            builder.queryParam("descricao", descricao);
                        }
                        
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .doOnSuccess(response -> log.info("Sucesso ao buscar estoque. Total encontrado: {}", 
                            response != null ? response.get("totalElements") : "N/A"))
                    .doOnError(error -> log.error("Erro ao buscar estoque na Legacy API", error))
                    .map(this::convertMapToBusinessDTO)
                    .block();
                    
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao chamar Legacy API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao buscar estoque na Legacy API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar Legacy API", e);
            throw new RuntimeException("Erro inesperado ao buscar estoque: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca análise de compras na Legacy API com filtros e paginação
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param hideNoSales Ocultar produtos sem vendas nos últimos 90 dias
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Resposta da Legacy API com dados de análise de compras
     * @throws RuntimeException Se houver erro na comunicação
     */
    public PurchaseAnalysisPageResponseDTO getPurchaseAnalysis(
            String refplu, String descricao, String grupo, String marca, Boolean hideNoSales, Integer page, Integer size, String sortBy, String sortDir) {
        
        log.info("Buscando análise de compras na Legacy API - refplu: {}, descricao: {}, grupo: {}, marca: {}, hideNoSales: {}, página: {}, tamanho: {}", 
                refplu, descricao, grupo, marca, hideNoSales, page, size);
        
        try {
            return legacyApiWebClient
                    .get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/purchase-analysis");
                        
                        // Adicionar parâmetros obrigatórios
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        builder.queryParam("sortBy", sortBy != null ? sortBy : "refplu");
                        builder.queryParam("sortDir", sortDir != null ? sortDir : "asc");
                        builder.queryParam("hideNoSales", hideNoSales != null ? hideNoSales : true);
                        
                        // Adicionar filtros opcionais
                        if (refplu != null && !refplu.trim().isEmpty()) {
                            builder.queryParam("refplu", refplu);
                        }
                        if (descricao != null && !descricao.trim().isEmpty()) {
                            builder.queryParam("descricao", descricao);
                        }
                        if (grupo != null && !grupo.trim().isEmpty()) {
                            builder.queryParam("grupo", grupo);
                        }
                        if (marca != null && !marca.trim().isEmpty()) {
                            builder.queryParam("marca", marca);
                        }
                        
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(PurchaseAnalysisPageResponseDTO.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .doOnSuccess(response -> log.info("Sucesso ao buscar análise de compras. Total encontrado: {}", 
                            response != null && response.getPagination() != null ? response.getPagination().getTotalElements() : "N/A"))
                    .doOnError(error -> log.error("Erro ao buscar análise de compras na Legacy API", error))
                    .block();
                    
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao chamar Legacy API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao buscar análise de compras na Legacy API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar Legacy API", e);
            throw new RuntimeException("Erro inesperado ao buscar análise de compras: " + e.getMessage(), e);
        }
    }
    
    /**
     * Testa a conectividade com a Legacy API
     * 
     * @return Informações sobre o teste de conexão
     */
    public Map<String, Object> testConnection() {
        log.info("Testando conexão com Legacy API");
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = legacyApiWebClient
                    .get()
                    .uri("/products/test-connection")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .doOnSuccess(result -> log.info("Teste de conexão com Legacy API bem-sucedido"))
                    .doOnError(error -> log.error("Erro no teste de conexão com Legacy API", error))
                    .block();
                    
            return response;
            
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP no teste de conexão: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro no teste de conexão com Legacy API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado no teste de conexão", e);
            throw new RuntimeException("Erro inesperado no teste de conexão: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converte Map da Legacy API para StockPageResponseDTO da Business API
     * 
     * @param legacyResponse Resposta da Legacy API como Map
     * @return DTO da Business API
     */
    private StockPageResponseDTO convertMapToBusinessDTO(Map<String, Object> legacyResponse) {
        if (legacyResponse == null) {
            return null;
        }
        
        // Converter itens de estoque
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) legacyResponse.get("content");
        List<StockItemResponseDTO> businessItems = new java.util.ArrayList<>();
        
        if (contentList != null) {
            for (Map<String, Object> item : contentList) {
                businessItems.add(convertMapToStockItem(item));
            }
        }
        
        // Criar metadados de paginação
        StockPageResponseDTO.PaginationMetadata pagination = StockPageResponseDTO.PaginationMetadata.builder()
                .totalElements(legacyResponse.get("totalElements") != null ? 
                    Long.valueOf(legacyResponse.get("totalElements").toString()) : 0L)
                .totalPages(legacyResponse.get("totalPages") != null ? 
                    Integer.valueOf(legacyResponse.get("totalPages").toString()) : 0)
                .currentPage(legacyResponse.get("currentPage") != null ? 
                    Integer.valueOf(legacyResponse.get("currentPage").toString()) : 0)
                .pageSize(legacyResponse.get("pageSize") != null ? 
                    Integer.valueOf(legacyResponse.get("pageSize").toString()) : 15)
                .hasNext(legacyResponse.get("hasNext") != null ? 
                    Boolean.valueOf(legacyResponse.get("hasNext").toString()) : false)
                .hasPrevious(legacyResponse.get("hasPrevious") != null ? 
                    Boolean.valueOf(legacyResponse.get("hasPrevious").toString()) : false)
                .build();
        
        // Criar informações da fonte de dados
        StockPageResponseDTO.DataSourceInfo dataSource = StockPageResponseDTO.DataSourceInfo.builder()
                .source("Legacy API")
                .version("1.0")
                .endpoint("/api/legacy/stocks")
                .build();
        
        return StockPageResponseDTO.builder()
                .content(businessItems)
                .pagination(pagination)
                .dataSource(dataSource)
                .timestamp(java.time.LocalDateTime.now())
                .status("SUCCESS")
                .message("Estoque carregado com sucesso")
                .build();
    }
    
    /**
     * Converte Map de item da Legacy API para StockItemResponseDTO da Business API
     * 
     * @param itemMap Item da Legacy API como Map
     * @return Item da Business API
     */
    private StockItemResponseDTO convertMapToStockItem(Map<String, Object> itemMap) {
        if (itemMap == null) {
            return null;
        }
        
        return StockItemResponseDTO.builder()
                .refplu(itemMap.get("refplu") != null ? itemMap.get("refplu").toString() : "")
                .marca(itemMap.get("marca") != null ? itemMap.get("marca").toString() : "")
                .descricao(itemMap.get("descricao") != null ? itemMap.get("descricao").toString() : "")
                .loj1(getLongValue(itemMap, "loj1"))
                .loj2(getLongValue(itemMap, "loj2"))
                .loj3(getLongValue(itemMap, "loj3"))
                .loj4(getLongValue(itemMap, "loj4"))
                .loj5(getLongValue(itemMap, "loj5"))
                .loj6(getLongValue(itemMap, "loj6"))
                .loj7(getLongValue(itemMap, "loj7"))
                .loj8(getLongValue(itemMap, "loj8"))
                .loj9(getLongValue(itemMap, "loj9"))
                .loj10(getLongValue(itemMap, "loj10"))
                .loj11(getLongValue(itemMap, "loj11"))
                .loj12(getLongValue(itemMap, "loj12"))
                .loj13(getLongValue(itemMap, "loj13"))
                .loj14(getLongValue(itemMap, "loj14"))
                .total(getLongValue(itemMap, "total"))
                .build();
    }
    
    /**
     * Busca produtos com estoque crítico na Legacy API.
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Resposta da Legacy API com dados de estoque crítico
     * @throws RuntimeException Se houver erro na comunicação
     */
    public CriticalStockPageResponseDTO getCriticalStock(
            String refplu, String descricao, String grupo, String marca, Integer page, Integer size, String sortBy, String sortDir) {
        
        log.info("Buscando estoque crítico na Legacy API - refplu: {}, descricao: {}, grupo: {}, marca: {}, página: {}, tamanho: {}", 
                refplu, descricao, grupo, marca, page, size);
        
        try {
            return legacyApiWebClient
                    .get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/critical-stock");
                        
                        // Adicionar parâmetros obrigatórios
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        builder.queryParam("sortBy", sortBy != null ? sortBy : "diferenca");
                        builder.queryParam("sortDir", sortDir != null ? sortDir : "desc");
                        
                        // Adicionar filtros opcionais
                        if (refplu != null && !refplu.trim().isEmpty()) {
                            builder.queryParam("refplu", refplu);
                        }
                        if (descricao != null && !descricao.trim().isEmpty()) {
                            builder.queryParam("descricao", descricao);
                        }
                        if (grupo != null && !grupo.trim().isEmpty()) {
                            builder.queryParam("grupo", grupo);
                        }
                        if (marca != null && !marca.trim().isEmpty()) {
                            builder.queryParam("marca", marca);
                        }
                        
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(CriticalStockPageResponseDTO.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .doOnSuccess(response -> log.info("Sucesso ao buscar estoque crítico. Total encontrado: {}", 
                            response != null && response.getPagination() != null ? response.getPagination().getTotalElements() : "N/A"))
                    .doOnError(error -> log.error("Erro ao buscar estoque crítico na Legacy API", error))
                    .block();
                    
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao chamar Legacy API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao buscar estoque crítico na Legacy API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar Legacy API", e);
            throw new RuntimeException("Erro inesperado ao buscar estoque crítico: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extrai valor Long do Map de forma segura
     * 
     * @param map Map contendo os dados
     * @param key Chave para buscar o valor
     * @return Valor Long ou 0L se não encontrado
     */
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value != null) {
            try {
                return Long.valueOf(value.toString());
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }
}
