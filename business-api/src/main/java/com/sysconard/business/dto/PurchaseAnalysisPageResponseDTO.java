package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta paginada para análise de compras.
 * Usado no Business API (Java 17).
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseAnalysisPageResponseDTO {
    
    /**
     * Lista de itens de análise de compras
     */
    private List<PurchaseAnalysisItemResponseDTO> content;
    
    /**
     * Informações de paginação
     */
    private PaginationInfo pagination;
    
    /**
     * Classe interna para informações de paginação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        
        /**
         * Número da página atual (base 0)
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
         * Indica se é a primeira página
         */
        private boolean first;
        
        /**
         * Indica se é a última página
         */
        private boolean last;
    }
}

