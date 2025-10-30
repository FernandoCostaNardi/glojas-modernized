package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para itens de estoque com informações de lojas
 * Representa um produto com suas quantidades por loja
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemDTO {
    
    /**
     * Código PLU da referência
     */
    private String refplu;
    
    /**
     * Nome da marca
     */
    private String marca;
    
    /**
     * Descrição do produto
     */
    private String descricao;
    
    /**
     * Quantidade na loja 1
     */
    private Long loj1;
    
    /**
     * Quantidade na loja 2
     */
    private Long loj2;
    
    /**
     * Quantidade na loja 3
     */
    private Long loj3;
    
    /**
     * Quantidade na loja 4
     */
    private Long loj4;
    
    /**
     * Quantidade na loja 5
     */
    private Long loj5;
    
    /**
     * Quantidade na loja 6
     */
    private Long loj6;
    
    /**
     * Quantidade na loja 7
     */
    private Long loj7;
    
    /**
     * Quantidade na loja 8
     */
    private Long loj8;
    
    /**
     * Quantidade na loja 9
     */
    private Long loj9;
    
    /**
     * Quantidade na loja 10
     */
    private Long loj10;
    
    /**
     * Quantidade na loja 11
     */
    private Long loj11;
    
    /**
     * Quantidade na loja 12
     */
    private Long loj12;
    
    /**
     * Quantidade na loja 13
     */
    private Long loj13;
    
    /**
     * Quantidade na loja 14
     */
    private Long loj14;
    
    /**
     * Total de todas as lojas
     * Soma de loj1 + loj2 + ... + loj14
     */
    private Long total;
}
