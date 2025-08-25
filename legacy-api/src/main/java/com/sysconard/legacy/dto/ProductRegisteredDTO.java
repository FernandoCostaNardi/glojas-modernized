package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para produtos cadastrados com informações completas
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisteredDTO {
    
    /**
     * Código do produto
     */
    private Long codigo;
    
    /**
     * Descrição da seção
     */
    private String secao;
    
    /**
     * Descrição do grupo
     */
    private String grupo;
    
    /**
     * Descrição do subgrupo
     */
    private String subgrupo;
    
    /**
     * Descrição da marca
     */
    private String marca;
    
    /**
     * Código do part number
     */
    private String partNumberCodigo;
    
    /**
     * Código PLU da referência
     */
    private String refplu;
    
    /**
     * Descrição do produto
     */
    private String descricao;
    
    /**
     * Código NCM do produto
     */
    private String ncm;
}
