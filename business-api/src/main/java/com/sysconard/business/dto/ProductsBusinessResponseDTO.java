package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta da Business API para produtos registrados
 * Inclui metadados adicionais da business layer
 * 
 * @author Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsBusinessResponseDTO {
    
    /**
     * Lista de produtos processados pela business layer
     */
    private List<ProductRegisteredResponseDTO> products;
    
    /**
     * Metadados de paginação
     */
    private PaginationMetadata pagination;
    
    /**
     * Informações sobre a origem dos dados
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
     * Mensagem adicional (se houver)
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
     * Informações sobre a origem dos dados
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
