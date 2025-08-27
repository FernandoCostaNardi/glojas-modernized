package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de produtos registrados do Legacy API
 * Representa um produto individual retornado pela API legacy
 * 
 * @author Business API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisteredResponseDTO {
    
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
