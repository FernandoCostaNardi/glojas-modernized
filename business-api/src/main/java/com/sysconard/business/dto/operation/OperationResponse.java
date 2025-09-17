package com.sysconard.business.dto.operation;

import com.sysconard.business.enums.OperationSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de operação.
 * Contém todos os dados de uma operação para retorno nas APIs.
 * Inclui informações de auditoria (criação e atualização).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponse {
    
    /**
     * Identificador único da operação.
     */
    private UUID id;
    
    /**
     * Código único da operação.
     */
    private String code;
    
    /**
     * Descrição da operação.
     */
    private String description;
    
    /**
     * Fonte da operação.
     */
    private OperationSource operationSource;
    
    /**
     * Data e hora de criação da operação.
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização da operação.
     */
    private LocalDateTime updatedAt;
}
