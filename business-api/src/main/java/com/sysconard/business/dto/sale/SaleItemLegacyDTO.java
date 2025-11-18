package com.sysconard.business.dto.sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO para receber dados de item de venda da Legacy API.
 * Utilizado para deserializar respostas da Legacy API.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemLegacyDTO {
    
    /**
     * Data e hora da venda
     */
    @JsonProperty("saleDate")
    private OffsetDateTime saleDate;
    
    /**
     * Código único da venda
     */
    @JsonProperty("saleCode")
    private String saleCode;
    
    /**
     * Sequência do item na venda
     */
    @JsonProperty("itemSequence")
    private Integer itemSequence;
    
    /**
     * Código do colaborador que realizou a venda
     */
    @JsonProperty("employeeCode")
    private String employeeCode;
    
    /**
     * Código de referência do produto
     */
    @JsonProperty("productRefCode")
    private String productRefCode;
    
    /**
     * Código da loja onde a venda foi realizada
     */
    @JsonProperty("storeCode")
    private String storeCode;
    
    /**
     * Código do produto
     */
    @JsonProperty("productCode")
    private String productCode;
    
    /**
     * Marca do produto
     */
    @JsonProperty("brand")
    private String brand;
    
    /**
     * Seção do produto
     */
    @JsonProperty("section")
    private String section;
    
    /**
     * Grupo do produto
     */
    @JsonProperty("group")
    private String group;
    
    /**
     * Subgrupo do produto
     */
    @JsonProperty("subgroup")
    private String subgroup;
    
    /**
     * Descrição completa do produto
     */
    @JsonProperty("productDescription")
    private String productDescription;
    
    /**
     * Código NCM do produto
     */
    @JsonProperty("ncm")
    private String ncm;
    
    /**
     * Quantidade de produtos vendidos
     */
    @JsonProperty("quantity")
    private Integer quantity;
    
    /**
     * Preço unitário do produto
     */
    @JsonProperty("unitPrice")
    private BigDecimal unitPrice;
    
    /**
     * Preço total do item
     */
    @JsonProperty("totalPrice")
    private BigDecimal totalPrice;
}

