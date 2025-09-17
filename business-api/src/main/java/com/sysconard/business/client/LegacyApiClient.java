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
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .doOnSuccess(response -> log.info("Sucesso ao buscar lojas. Total encontrado: {}", 
                            response != null ? response.size() : "N/A"))
                    .doOnError(error -> log.error("Erro ao buscar lojas na Legacy API", error))
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
}
