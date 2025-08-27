package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO para capturar a resposta completa da API legacy /products/registered
 * Inclui dados de paginação, filtros e produtos
 * 
 * @author Business API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegacyApiResponseDTO {
    
    /**
     * Lista de produtos registrados
     */
    private List<ProductRegisteredResponseDTO> content;
    
    /**
     * Total de elementos encontrados
     */
    private Long totalElements;
    
    /**
     * Total de páginas
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
     * Filtros aplicados na busca
     */
    private Map<String, String> filters;
    
    /**
     * Informações de ordenação
     */
    private Map<String, String> sorting;
}
