package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa o relatório de vendas por loja e por dia.
 * Contém as agregações de DANFE, PDV e TROCA3 para uma loja específica em uma data específica.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSalesReportByDayDTO implements Serializable {
    
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
     * Data do relatório (apenas a data, sem horário)
     */
    private LocalDate reportDate;
    
    /**
     * Valor total de vendas DANFE para esta loja nesta data
     */
    private BigDecimal danfe;
    
    /**
     * Valor total de vendas PDV para esta loja nesta data
     */
    private BigDecimal pdv;
    
    /**
     * Valor total de trocas (TROCA3) para esta loja nesta data
     */
    private BigDecimal troca3;
    
    /**
     * Valor total geral (soma de DANFE + PDV + TROCA3) para esta loja nesta data
     */
    public BigDecimal getTotal() {
        if (danfe == null && pdv == null && troca3 == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        if (danfe != null) {
            total = total.add(danfe);
        }
        if (pdv != null) {
            total = total.add(pdv);
        }
        if (troca3 != null) {
            total = total.add(troca3);
        }
        
        return total;
    }
}
