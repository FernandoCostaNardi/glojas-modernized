package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO para respostas paginadas de produtos
 * 
 * Segue os princípios de Clean Code:
 * - Uso do Lombok para reduzir boilerplate
 * - Builder pattern automático
 * - Getters/setters automáticos
 * - Construtores automáticos
 * - Documentação clara
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponse {
    
    /**
     * Lista de produtos da página atual
     */
    private List<ProductRegisteredDTO> content;
    
    /**
     * Total de elementos em todas as páginas
     */
    private long totalElements;
    
    /**
     * Total de páginas
     */
    private int totalPages;
    
    /**
     * Página atual (0-based)
     */
    private int currentPage;
    
    /**
     * Tamanho da página
     */
    private int pageSize;
    
    /**
     * Indica se existe próxima página
     */
    private boolean hasNext;
    
    /**
     * Indica se existe página anterior
     */
    private boolean hasPrevious;
    
    /**
     * Filtros aplicados na consulta
     */
    private Map<String, String> filters;
    
    /**
     * Configurações de ordenação aplicadas
     */
    private Map<String, String> sorting;
}
