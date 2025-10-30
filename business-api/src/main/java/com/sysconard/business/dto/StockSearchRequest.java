package com.sysconard.business.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Record para requisição de busca de estoque
 * Utiliza record do Java 17 para DTOs simples
 * 
 * @author Business API
 * @version 1.0
 */
public record StockSearchRequest(
    String refplu,
    String marca,
    String descricao,
    Boolean hasStock,
    @Min(0) int page,
    @Min(1) @Max(100) int size,
    String sortBy,
    String sortDir
) {
    /**
     * Construtor compacto para validações
     */
    public StockSearchRequest {
        if (refplu != null && refplu.trim().isEmpty()) {
            refplu = null;
        }
        if (marca != null && marca.trim().isEmpty()) {
            marca = null;
        }
        if (descricao != null && descricao.trim().isEmpty()) {
            descricao = null;
        }
        if (hasStock == null) {
            hasStock = true;
        }
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "refplu";
        }
        if (sortDir == null || sortDir.trim().isEmpty()) {
            sortDir = "asc";
        }
        
        // Validar valores permitidos para sortBy
        String[] validSortFields = {
            "refplu", "marca", "descricao", 
            "loj1", "loj2", "loj3", "loj4", "loj5", "loj6", "loj7", 
            "loj8", "loj9", "loj10", "loj11", "loj12", "loj13", "loj14",
            "total"
        };
        
        boolean isValidSortBy = false;
        for (String field : validSortFields) {
            if (field.equalsIgnoreCase(sortBy)) {
                isValidSortBy = true;
                break;
            }
        }
        
        if (!isValidSortBy) {
            throw new IllegalArgumentException(
                "Campo de ordenação inválido: " + sortBy + 
                ". Valores válidos: refplu, marca, descricao, loj1-loj14, total"
            );
        }
    }
}
