package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO de entrada para solicitação de busca de produtos de trocas.
 * Centraliza todos os parâmetros necessários para buscar os produtos das trocas.
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeProductRequestDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Lista de números de nota (DOCNUMDOC) para filtrar as trocas.
     * Será convertido para INT removendo zeros à esquerda.
     * Exemplo: ["54118", "102154"] ou ["000054118", "000102154"]
     */
    private List<String> documentNumbers;
    
    /**
     * Lista de chaves NFE (DOCCHVNFE) para filtrar as trocas.
     * Exemplo: ["23250816571889000440592300701510399804480711"]
     */
    private List<String> nfeKeys;
}

