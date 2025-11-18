package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO para retornar detalhes de itens de venda.
 * Representa cada item de uma venda com todas as informações do produto e da venda.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDetailDTO {
    
    /**
     * Data da venda (DOCDATEMI)
     */
    private Date saleDate;
    
    /**
     * Código da venda/nota/cupom formatado com 6 dígitos (SAICOD)
     */
    private String saleCode;
    
    /**
     * Sequência do item na venda (ITSSEQ)
     */
    private Integer itemSequence;
    
    /**
     * Código do colaborador que fez a venda formatado com 6 dígitos (FUNCOD)
     */
    private String employeeCode;
    
    /**
     * Código interno do produto (REFPLU)
     */
    private String productRefCode;
    
    /**
     * Código da loja formatado com 6 dígitos (LOJCOD)
     */
    private String storeCode;
    
    /**
     * Código do produto formatado com 6 dígitos (PROCOD)
     */
    private String productCode;
    
    /**
     * Marca do produto (MARDES)
     */
    private String brand;
    
    /**
     * Seção do produto (SECDES)
     */
    private String section;
    
    /**
     * Grupo do produto (GRPDES)
     */
    private String group;
    
    /**
     * Subgrupo do produto (SBGDES)
     */
    private String subgroup;
    
    /**
     * Descrição do produto (PRODES)
     */
    private String productDescription;
    
    /**
     * Código NCM do produto (PRONCM)
     */
    private String ncm;
    
    /**
     * Quantidade do produto na venda (ITSQTDTOT)
     */
    private Integer quantity;
    
    /**
     * Preço unitário do produto (Unitario)
     */
    private BigDecimal unitPrice;
    
    /**
     * Valor total do item da venda (ITSTOTFAT)
     */
    private BigDecimal totalPrice;
}

