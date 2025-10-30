package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta paginada para estoque na Business API
 * Contém lista de itens de estoque e metadados de paginação
 * 
 * @author Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPageResponseDTO {
    
    /**
     * Lista de itens de estoque
     */
    private List<StockItemResponseDTO> content;
    
    /**
     * Metadados de paginação
     */
    private PaginationMetadata pagination;
    
    /**
     * Informações da fonte de dados
     */
    private DataSourceInfo dataSource;
    
    /**
     * Timestamp da resposta
     */
    private LocalDateTime timestamp;
    
    /**
     * Status da operação
     */
    private String status;
    
    /**
     * Mensagem de resposta
     */
    private String message;
    
    /**
     * Metadados de paginação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        private Long totalElements;
        private Integer totalPages;
        private Integer currentPage;
        private Integer pageSize;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }
    
    /**
     * Informações da fonte de dados
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataSourceInfo {
        private String source;
        private String version;
        private String endpoint;
    }
}
