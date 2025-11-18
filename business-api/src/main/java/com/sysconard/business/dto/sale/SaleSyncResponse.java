package com.sysconard.business.dto.sale;

import java.time.LocalDateTime;

/**
 * Record para resposta de sincronização de vendas.
 * Usado no Business API (Java 17) - DTOs simples usam Records.
 * 
 * @param totalItemsReceived Total de itens recebidos da Legacy API
 * @param productsInserted Quantidade de produtos novos inseridos
 * @param productsSkipped Quantidade de produtos que já existiam (não inseridos)
 * @param salesInserted Quantidade de vendas novas inseridas
 * @param salesSkipped Quantidade de vendas que já existiam (não inseridas)
 * @param syncDate Data e hora da sincronização
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
public record SaleSyncResponse(
    int totalItemsReceived,
    int productsInserted,
    int productsSkipped,
    int salesInserted,
    int salesSkipped,
    LocalDateTime syncDate
) {
}

