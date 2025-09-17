package com.sysconard.business.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta de busca de operações com paginação.
 * Contém a lista de operações encontradas e informações de paginação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationSearchResponse {
    
    /**
     * Lista de operações encontradas na busca.
     */
    private List<OperationResponse> operations;
    
    /**
     * Informações de paginação.
     */
    private PaginationInfo pagination;
    
    /**
     * Total de elementos encontrados (considerando filtros).
     */
    private long totalElements;
    
    /**
     * Total de páginas disponíveis.
     */
    private int totalPages;
    
    /**
     * Página atual (baseado em 0).
     */
    private int currentPage;
    
    /**
     * Tamanho da página.
     */
    private int pageSize;
    
    /**
     * Indica se há próxima página.
     */
    private boolean hasNext;
    
    /**
     * Indica se há página anterior.
     */
    private boolean hasPrevious;
    
    /**
     * Classe interna para informações de paginação.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int pageSize;
        private int totalPages;
        private long totalElements;
        private boolean hasNext;
        private boolean hasPrevious;
    }
}
