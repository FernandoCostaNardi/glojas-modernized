package com.sysconard.business.dto.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO para mapear a resposta da Legacy API de trocas.
 * Representa os dados de uma troca retornados pela API legacy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeLegacyDTO {
    
    /**
     * Código de origem do documento (ORICOD)
     */
    @JsonProperty("originCode")
    private String originCode;
    
    /**
     * Código de operação do documento (OPECOD)
     */
    @JsonProperty("operationCode")
    private String operationCode;
    
    /**
     * Código da loja (LOJCOD)
     */
    @JsonProperty("storeCode")
    private String storeCode;
    
    /**
     * Código do documento (DOCCOD)
     */
    @JsonProperty("documentCode")
    private String documentCode;
    
    /**
     * Código do funcionário que realizou a troca (FUNCOD)
     */
    @JsonProperty("employeeCode")
    private String employeeCode;
    
    /**
     * Número do documento/nota (DOCNUMDOC)
     */
    @JsonProperty("documentNumber")
    private String documentNumber;
    
    /**
     * Chave NFE do documento (DOCCHVNFE)
     */
    @JsonProperty("nfeKey")
    private String nfeKey;
    
    /**
     * Data de emissão do documento (DOCDATEMI)
     */
    @JsonProperty("issueDate")
    private Date issueDate;
    
    /**
     * Observação do documento (DOCOBS)
     */
    @JsonProperty("observation")
    private String observation;
}

