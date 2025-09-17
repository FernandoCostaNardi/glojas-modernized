package com.sysconard.business.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para dados de loja obtidos da Legacy API.
 * Representa os dados básicos de uma loja: id, name e city.
 * O campo id é mantido como String para preservar a formatação de 6 dígitos (ex: 000001).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponseDto {
    
    /**
     * Identificador único da loja.
     * Mantido como String para preservar a formatação de 6 dígitos (ex: 000001).
     */
    private String id;
    
    /**
     * Nome da loja.
     * Representa o nome comercial ou fantasia da loja.
     */
    private String name;
    
    /**
     * Cidade onde a loja está localizada.
     * Utilizado para organização geográfica e relatórios.
     */
    private String city;
}
