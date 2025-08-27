package com.sysconard.business.service;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.LegacyApiResponseDTO;
import com.sysconard.business.dto.ProductsBusinessResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Serviço de negócio para operações com produtos
 * Orquestra chamadas para APIs externas e aplica regras de negócio
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
public class ProductService {
    
    private final LegacyApiClient legacyApiClient;
    
    public ProductService(LegacyApiClient legacyApiClient) {
        this.legacyApiClient = legacyApiClient;
    }
    
    /**
     * Busca produtos registrados aplicando regras de negócio
     * 
     * @param secao Filtro por seção (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Resposta formatada da Business API
     */
    public ProductsBusinessResponseDTO getRegisteredProducts(
            String secao, String grupo, String marca, String descricao,
            Integer page, Integer size, String sortBy, String sortDir) {
        
        log.info("Iniciando busca de produtos registrados com filtros - secao: {}, grupo: {}, marca: {}", 
                secao, grupo, marca);
        
        try {
            // Buscar dados na Legacy API
            LegacyApiResponseDTO legacyResponse = legacyApiClient.getRegisteredProducts(
                    secao, grupo, marca, descricao, page, size, sortBy, sortDir);
            
            // Aplicar regras de negócio (por enquanto apenas transformação)
            var products = legacyResponse.getContent() != null ? 
                    legacyResponse.getContent() : Collections.<com.sysconard.business.dto.ProductRegisteredResponseDTO>emptyList();
            
            // Construir resposta da Business API
            return ProductsBusinessResponseDTO.builder()
                    .products(products)
                    .pagination(ProductsBusinessResponseDTO.PaginationMetadata.builder()
                            .totalElements(legacyResponse.getTotalElements())
                            .totalPages(legacyResponse.getTotalPages())
                            .currentPage(legacyResponse.getCurrentPage())
                            .pageSize(legacyResponse.getPageSize())
                            .hasNext(legacyResponse.getHasNext())
                            .hasPrevious(legacyResponse.getHasPrevious())
                            .build())
                    .dataSource(ProductsBusinessResponseDTO.DataSourceInfo.builder()
                            .source("legacy-api")
                            .version("1.0")
                            .endpoint("/products/registered")
                            .build())
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .message("Produtos encontrados com sucesso")
                    .build();
                    
        } catch (Exception e) {
            log.error("Erro ao buscar produtos registrados", e);
            
            // Retornar resposta de erro
            return ProductsBusinessResponseDTO.builder()
                    .products(Collections.emptyList())
                    .pagination(ProductsBusinessResponseDTO.PaginationMetadata.builder()
                            .totalElements(0L)
                            .totalPages(0)
                            .currentPage(page != null ? page : 0)
                            .pageSize(size != null ? size : 20)
                            .hasNext(false)
                            .hasPrevious(false)
                            .build())
                    .dataSource(ProductsBusinessResponseDTO.DataSourceInfo.builder()
                            .source("legacy-api")
                            .version("1.0")
                            .endpoint("/products/registered")
                            .build())
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Erro ao buscar produtos: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Testa conectividade com a Legacy API
     * 
     * @return Resultado do teste de conexão
     */
    public Map<String, Object> testLegacyApiConnection() {
        log.info("Testando conectividade com Legacy API");
        
        try {
            return legacyApiClient.testConnection();
        } catch (Exception e) {
            log.error("Erro no teste de conectividade", e);
            return Map.of(
                    "status", "ERROR",
                    "message", "Erro ao testar conectividade: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().toString()
            );
        }
    }
}
