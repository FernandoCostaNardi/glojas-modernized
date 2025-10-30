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
     * @param page Número da página (base 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     * @return Resposta paginada com itens de estoque crítico
     */
    public CriticalStockPageResponse getCriticalStock(
            String refplu, int page, int size, String sortBy, String sortDir) {
        
        log.info("Processando requisição de estoque crítico: refplu={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, page, size, sortBy, sortDir);
        
        try {
            // Buscar dados
            List<CriticalStockItemDTO> content = criticalStockService.findCriticalStockWithFilters(
                refplu, page, size, sortBy, sortDir
            );
            
            // Contar total
            long totalElements = criticalStockService.countWithFilters(refplu);
            
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

