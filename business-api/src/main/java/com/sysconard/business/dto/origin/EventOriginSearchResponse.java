package com.sysconard.business.dto.origin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para busca paginada de EventOrigin com totalizadores.
 * Utiliza Lombok para DTOs complexos (>5 campos) seguindo padrões de Clean Code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventOriginSearchResponse {
    
    /**
     * Lista de EventOrigins encontrados.
     */
    private List<EventOriginResponse> eventOrigins;
    
    /**
     * Informações de paginação.
     */
    private PaginationInfo pagination;
    
    /**
     * Totalizadores por EventSource.
     */
    private EventOriginCounts counts;
    
    /**
     * Classe interna para informações de paginação.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int pageSize;
        private boolean hasNext;
        private boolean hasPrevious;
    }
    
    /**
     * Classe interna para totalizadores de EventOrigin.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventOriginCounts {
        private long totalPdv;
        private long totalExchange;
        private long totalDanfe;
        private long totalEventOrigins;
    }
}
