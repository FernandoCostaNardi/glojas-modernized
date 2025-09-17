package com.sysconard.business.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar um tipo de operação (OperationKind).
 * Contém informações básicas sobre o tipo de operação retornado pela Legacy API.
 * 
 * @author Business API
 * @version 1.0
 */
public record OperationKindDto(
    
    /**
     * Identificador único do tipo de operação formatado com 6 dígitos (ex: 000001)
     */
    @JsonProperty("id")
    String id,
    
    /**
     * Descrição do tipo de operação
     */
    @JsonProperty("description")
    String description
) {
    
    /**
     * Construtor compacto para validações básicas
     */
    public OperationKindDto {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID não pode ser nulo ou vazio");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser nula ou vazia");
        }
    }
    
    /**
     * Retorna uma representação em string do OperationKind
     * 
     * @return String formatada com ID e descrição
     */
    @Override
    public String toString() {
        return "OperationKindDto{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
