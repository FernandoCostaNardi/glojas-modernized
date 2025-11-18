package com.sysconard.business.dto.exchange;

import java.time.LocalDateTime;

/**
 * Record para requisição de atualização de troca.
 * Todos os campos são opcionais, apenas os fornecidos serão atualizados.
 * 
 * @param documentCode Código do documento
 * @param storeCode Código da loja
 * @param operationCode Código da operação
 * @param originCode Código de origem
 * @param employeeCode Código do colaborador
 * @param documentNumber Número do documento/nota
 * @param nfeKey Chave NFE do documento
 * @param issueDate Data de emissão
 * @param observation Observação do documento
 * @param newSaleNumber Número da nova venda
 * @param newSaleNfeKey Chave NFE da venda
 */
public record UpdateExchangeRequest(
    String documentCode,
    String storeCode,
    String operationCode,
    String originCode,
    String employeeCode,
    String documentNumber,
    String nfeKey,
    LocalDateTime issueDate,
    String observation,
    String newSaleNumber,
    String newSaleNfeKey
) {
}

