package com.sysconard.legacy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sysconard.legacy.dto.CriticalStockItemDTO;
import com.sysconard.legacy.dto.CriticalStockPageResponse;

import java.util.List;

/**
 * Controller Service para estoque crítico.
 * Orquestra chamadas e monta resposta paginada.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CriticalStockControllerService {
    
    private final CriticalStockService criticalStockService;
    
    /**
     * Busca produtos com estoque crítico com filtros, paginação e ordenação.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param descricao Filtro opcional por descrição (busca também em grupo e marca)
     * @param grupo Filtro opcional por grupo
     * @param marca Filtro opcional por marca
     * @param page Número da página (base 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     * @return Resposta paginada com itens de estoque crítico
     */
    public CriticalStockPageResponse getCriticalStock(
            String refplu, String descricao, String grupo, String marca, int page, int size, String sortBy, String sortDir) {
        
        log.info("Processando requisição de estoque crítico: refplu={}, descricao={}, grupo={}, marca={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, descricao, grupo, marca, page, size, sortBy, sortDir);
        
        try {
            // Buscar dados
            List<CriticalStockItemDTO> content = criticalStockService.findCriticalStockWithFilters(
                refplu, descricao, grupo, marca, page, size, sortBy, sortDir
            );
            
            // Contar total
            long totalElements = criticalStockService.countWithFilters(refplu, descricao, grupo, marca);
            
            // Calcular total de páginas
            int totalPages = (int) Math.ceil((double) totalElements / size);
            
            // Montar informações de paginação
            CriticalStockPageResponse.PaginationInfo pagination = CriticalStockPageResponse.PaginationInfo.builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .first(page == 0)
                    .last(page >= totalPages - 1)
                    .build();
            
            // Montar resposta
            CriticalStockPageResponse response = CriticalStockPageResponse.builder()
                    .content(content)
                    .pagination(pagination)
                    .build();
            
            log.info("Estoque crítico processado com sucesso: {} itens de {} total", 
                    content.size(), totalElements);
            
            return response;
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação no estoque crítico: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar estoque crítico", e);
            throw new RuntimeException("Erro ao processar estoque crítico: " + e.getMessage(), e);
        }
    }
}

