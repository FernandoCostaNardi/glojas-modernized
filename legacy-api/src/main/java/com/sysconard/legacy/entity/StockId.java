package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Classe de chave primária composta para a entidade Stock
 * Necessária para JPA quando a entidade possui múltiplos campos @Id
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código PLU da referência
     */
    private String refplu;

    /**
     * Código da loja
     */
    private Long lojcod;

    /**
     * Código do local
     */
    private Long loccod;
}
