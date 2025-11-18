package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para cargos do sistema
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
public class JobPositionDTO {
    
    /**
     * Código único do cargo formatado com 6 dígitos (ex: 000001)
     */
    private String id;
    
    /**
     * Descrição do cargo
     */
    private String description;
}

