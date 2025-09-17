package com.sysconard.business.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para dados de loja.
 * Representa os dados completos de uma loja retornados pela API.
 * Inclui informações de auditoria (createdAt, updatedAt).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {
    
    /**
     * Identificador único da loja.
     * Gerado automaticamente como UUID.
     */
    private UUID id;
    
    /**
     * Código único da loja.
     * Utilizado para identificação rápida e referência externa.
     */
    private String code;
    
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
    
    /**
     * Status da loja.
     * Indica se a loja está ativa (true) ou inativa (false).
     */
    private boolean status;
    
    /**
     * Data e hora de criação do registro.
     * Preenchida automaticamente na primeira persistência.
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do registro.
     * Atualizada automaticamente a cada modificação.
     */
    private LocalDateTime updatedAt;
}
