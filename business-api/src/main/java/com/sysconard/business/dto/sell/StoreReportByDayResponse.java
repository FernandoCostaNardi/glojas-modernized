package com.sysconard.business.dto.sell;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Record para resposta do relatório de vendas por loja e por dia.
 * Contém os dados agregados de vendas para uma loja específica em uma data específica.
 * 
 * @param storeName Nome fantasia da loja
 * @param storeCode Código da loja
 * @param reportDate Data do relatório (apenas a data, sem horário)
 * @param danfe Valor total das vendas DANFE para esta loja nesta data
 * @param pdv Valor total das vendas PDV para esta loja nesta data
 * @param troca Valor total das trocas (TROCA3) para esta loja nesta data
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record StoreReportByDayResponse(
    String storeName,
    String storeCode,
    LocalDate reportDate,
    BigDecimal danfe,
    BigDecimal pdv,
    BigDecimal troca
) {
    /**
     * Construtor compacto com validações e inicializações
     */
    public StoreReportByDayResponse {
        // Garantir que valores nulos sejam convertidos para zero
        danfe = danfe != null ? danfe : BigDecimal.ZERO;
        pdv = pdv != null ? pdv : BigDecimal.ZERO;
        troca = troca != null ? troca : BigDecimal.ZERO;
    }
    
    /**
     * Calcula o valor total geral (DANFE + PDV - TROCA3) para esta loja nesta data.
     * A troca é subtraída pois representa devoluções/trocas que reduzem o faturamento.
     * 
     * @return Valor total líquido das vendas do dia
     */
    public BigDecimal getTotal() {
        return danfe.add(pdv).subtract(troca);
    }
}
