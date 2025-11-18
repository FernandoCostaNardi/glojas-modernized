package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO para retornar dados de trocas realizadas.
 * Representa cada troca com todas as informações do documento.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeDTO {
    
    /**
     * Código de origem do documento (ORICOD)
     */
    private String originCode;
    
    /**
     * Código de operação do documento (OPECOD)
     */
    private String operationCode;
    
    /**
     * Código da loja (LOJCOD)
     */
    private String storeCode;
    
    /**
     * Código do documento (DOCCOD)
     */
    private String documentCode;
    
    /**
     * Código do funcionário que realizou a troca (FUNCOD)
     */
    private String employeeCode;
    
    /**
     * Número do documento/nota (DOCNUMDOC)
     */
    private String documentNumber;
    
    /**
     * Chave NFE do documento (DOCCHVNFE)
     */
    private String nfeKey;
    
    /**
     * Data de emissão do documento (DOCDATEMI)
     */
    private Date issueDate;
    
    /**
     * Observação do documento (DOCOBS)
     */
    private String observation;
}

