package com.sysconard.business.service;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.StockPageResponseDTO;
import com.sysconard.business.dto.StockSearchRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Serviço de negócio para operações com estoque
 * Orquestra chamadas para APIs externas e aplica regras de negócio
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
public class StockService {
    
    private final LegacyApiClient legacyApiClient;
    
    public StockService(LegacyApiClient legacyApiClient) {
        this.legacyApiClient = legacyApiClient;
    }
    
    /**
     * Busca estoque aplicando regras de negócio
     * 
     * @param request Dados da requisição de busca
     * @return Resposta formatada da Business API
     */
    public StockPageResponseDTO getStocks(StockSearchRequest request) {
        
        log.info("Iniciando busca de estoque com filtros - refplu: {}, marca: {}, descricao: {}, hasStock: {}", 
                request.refplu(), request.marca(), request.descricao(), request.hasStock());
        
        try {
            // Buscar dados na Legacy API
            StockPageResponseDTO legacyResponse = legacyApiClient.getStocks(
                    request.refplu(), request.marca(), request.descricao(), request.hasStock(),
                    request.page(), request.size(), request.sortBy(), request.sortDir());
            
            // Aplicar regras de negócio (por enquanto apenas transformação)
            var stocks = legacyResponse.getContent() != null ? 
                    legacyResponse.getContent() : Collections.<com.sysconard.business.dto.StockItemResponseDTO>emptyList();
            
            // Construir resposta da Business API
            return StockPageResponseDTO.builder()
                    .content(stocks)
                    .pagination(StockPageResponseDTO.PaginationMetadata.builder()
                            .totalElements(legacyResponse.getPagination().getTotalElements())
                            .totalPages(legacyResponse.getPagination().getTotalPages())
                            .currentPage(legacyResponse.getPagination().getCurrentPage())
                            .pageSize(legacyResponse.getPagination().getPageSize())
                            .hasNext(legacyResponse.getPagination().getHasNext())
                            .hasPrevious(legacyResponse.getPagination().getHasPrevious())
                            .build())
                    .dataSource(StockPageResponseDTO.DataSourceInfo.builder()
                            .source("legacy-api")
                            .version("1.0")
                            .endpoint("/stocks")
                            .build())
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .message("Estoque encontrado com sucesso")
                    .build();
                    
        } catch (Exception e) {
            log.error("Erro ao buscar estoque", e);
            
            // Retornar resposta de erro
            return StockPageResponseDTO.builder()
                    .content(Collections.emptyList())
                    .pagination(StockPageResponseDTO.PaginationMetadata.builder()
                            .totalElements(0L)
                            .totalPages(0)
                            .currentPage(request.page())
                            .pageSize(request.size())
                            .hasNext(false)
                            .hasPrevious(false)
                            .build())
                    .dataSource(StockPageResponseDTO.DataSourceInfo.builder()
                            .source("legacy-api")
                            .version("1.0")
                            .endpoint("/stocks")
                            .build())
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Erro ao buscar estoque: " + e.getMessage())
                    .build();
        }
    }
}
