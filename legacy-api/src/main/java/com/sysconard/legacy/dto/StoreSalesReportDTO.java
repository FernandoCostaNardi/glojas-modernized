package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO que representa o relatório de vendas por loja.
 * Contém as agregações de DANFE, PDV e TROCA3.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSalesReportDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Nome fantasia da loja
     */
    private String storeName;
    
    /**
     * Código da loja
     */
    private String storeCode;
    
    /**
     * Valor total de vendas DANFE
     */
    private BigDecimal danfe;
    
    /**
     * Valor total de vendas PDV
     */
    private BigDecimal pdv;
    
    /**
     * Valor total de trocas (TROCA3)
     */
    private BigDecimal troca3;
}
