package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para lojas do sistema
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
public class StoreDTO {
    
    /**
     * Código único da loja formatado com 6 dígitos (ex: 000001)
     */
    private String id;
    
    /**
     * Nome fantasia da loja
     */
    private String name;
    
    /**
     * Cidade da loja
     */
    private String city;
}
