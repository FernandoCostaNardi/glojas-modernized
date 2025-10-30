package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta paginada de estoque
 * Contém lista de itens de estoque e metadados de paginação
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPageResponse {
    
    /**
     * Lista de itens de estoque
     */
    private List<StockItemDTO> content;
    
    /**
     * Número total de elementos
     */
    private Long totalElements;
    
    /**
     * Número total de páginas
     */
    private Integer totalPages;
    
    /**
     * Página atual (0-based)
     */
    private Integer currentPage;
    
    /**
     * Tamanho da página
     */
    private Integer pageSize;
    
    /**
     * Indica se há próxima página
     */
    private Boolean hasNext;
    
    /**
     * Indica se há página anterior
     */
    private Boolean hasPrevious;
    
    /**
     * Indica se é a primeira página
     */
    private Boolean first;
    
    /**
     * Indica se é a última página
     */
    private Boolean last;
    
    /**
     * Número de elementos na página atual
     */
    private Integer numberOfElements;
}
