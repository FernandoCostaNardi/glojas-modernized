package com.sysconard.business.dto.emailnotifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta paginada para EmailNotifier
 * Lombok para DTOs complexos com metadados de paginação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotifierPageResponse {
    
    /**
     * Lista de notificadores na página atual
     */
    private List<EmailNotifierResponse> content;
    
    /**
     * Número da página atual (0-based)
     */
    private int page;
    
    /**
     * Tamanho da página
     */
    private int size;
    
    /**
     * Número total de páginas
     */
    private int totalPages;
    
    /**
     * Número total de elementos
     */
    private long totalElements;
    
    /**
     * Indica se há próxima página
     */
    private boolean hasNext;
    
    /**
     * Indica se há página anterior
     */
    private boolean hasPrevious;
}
