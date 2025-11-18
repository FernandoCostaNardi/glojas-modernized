package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO para retornar dados de produtos de trocas.
 * Representa cada produto de uma troca com todas as informações do item de entrada.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeProductDTO {
    
    /**
     * Código da loja (LOJCOD)
     */
    private String storeCode;
    
    /**
     * Código do documento (ENTCOD/DOCCOD)
     */
    private String documentCode;
    
    /**
     * Código de referência do produto (REFPLU)
     */
    private String productRefCode;
    
    /**
     * Data da troca (ITEDATMOV)
     */
    private Date exchangeDate;
    
    /**
     * Quantidade do produto (ITEQTDEMB convertido para INT)
     */
    private Integer quantity;
    
    /**
     * Valor unitário do produto (ITEVLREMB convertido para DECIMAL(10,2))
     */
    private BigDecimal unitValue;
    
    /**
     * Número da nota fiscal (DOCNUMDOC)
     */
    private String documentNumber;
    
    /**
     * Chave NFE da nota fiscal (DOCCHVNFE)
     */
    private String nfeKey;
}

