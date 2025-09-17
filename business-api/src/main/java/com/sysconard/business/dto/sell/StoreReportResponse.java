package com.sysconard.business.dto.sell;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Record para resposta do relatório de vendas por loja.
 * Contém os dados agregados de vendas para uma loja específica.
 * 
 * @param storeName Nome da loja
 * @param storeCode Código da loja
 * @param danfe Valor total das vendas DANFE
 * @param pdv Valor total das vendas PDV
 * @param troca Valor total das trocas (tipo 3)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record StoreReportResponse(
    String storeName,
    String storeCode,
    BigDecimal danfe,
    BigDecimal pdv,
    BigDecimal troca
) {
    /**
     * Construtor compacto com validações e inicializações
     */
    public StoreReportResponse {
        // Garantir que valores nulos sejam convertidos para zero
        danfe = danfe != null ? danfe : BigDecimal.ZERO;
        pdv = pdv != null ? pdv : BigDecimal.ZERO;
        troca = troca != null ? troca : BigDecimal.ZERO;
    }
}
