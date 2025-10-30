package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta paginada de estoque crítico.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalStockPageResponse {
    
    /**
     * Lista de itens de estoque crítico
     */
    private List<CriticalStockItemDTO> content;
    
    /**
     * Informações de paginação
     */
    private PaginationInfo pagination;
    
    /**
     * Informações de paginação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        
        /**
         * Página atual (base 0)
         */
        private int currentPage;
        
        /**
         * Tamanho da página
         */
        private int pageSize;
        
        /**
         * Total de elementos
         */
        private long totalElements;
        
        /**
         * Total de páginas
         */
        private int totalPages;
        
        /**
         * É a primeira página?
         */
        private boolean first;
        
        /**
         * É a última página?
         */
        private boolean last;
    }
}

