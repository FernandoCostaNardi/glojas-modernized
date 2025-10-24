package com.sysconard.business.dto.operation;

import com.sysconard.business.enums.OperationSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de busca de operações com filtros e paginação.
 * Permite filtrar operações por código e configurar paginação e ordenação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationSearchRequest {
    
    /**
     * Filtro por código da operação (busca parcial).
     * Se fornecido, retorna apenas operações cujo código contenha este valor.
     */
    private String code;
    
    /**
     * Filtro por fonte da operação.
     * Se fornecido, retorna apenas operações com a fonte especificada.
     * Valores válidos: SELL, EXCHANGE.
     */
    private OperationSource operationSource;
    
    /**
     * Número da página (baseado em 0).
     * Padrão: 0 (primeira página).
     */
    @Builder.Default
    private int page = 0;
    
    /**
     * Tamanho da página (número de itens por página).
     * Padrão: 20 itens por página.
     */
    @Builder.Default
    private int size = 20;
    
    /**
     * Campo para ordenação.
     * Valores válidos: "code", "description", "createdAt", "updatedAt".
     * Padrão: "code".
     */
    @Builder.Default
    private String sortBy = "code";
    
    /**
     * Direção da ordenação.
     * Valores válidos: "asc" (crescente) ou "desc" (decrescente).
     * Padrão: "asc".
     */
    @Builder.Default
    private String sortDir = "asc";
}
