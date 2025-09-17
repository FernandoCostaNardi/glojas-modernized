package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para operações do sistema
 * 
 * Segue os princípios de Clean Code:
 * - Uso do Lombok para reduzir boilerplate
 * - Builder pattern automático
 * - Getters/setters automáticos
 * - Construtores automáticos
 * - Documentação clara
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDTO {
    
    /**
     * Código único da operação formatado com 6 dígitos (ex: 000001)
     */
    private String id;
    
    /**
     * Descrição da operação
     */
    private String description;
}
