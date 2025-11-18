package com.sysconard.business.dto.exchange;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record para resposta de uma troca.
 * Contém todos os campos da entidade Exchange formatados para resposta.
 * 
 * @param id Identificador único da troca
 * @param documentCode Código do documento
 * @param storeCode Código da loja
 * @param operationCode Código da operação
 * @param originCode Código de origem
 * @param employeeCode Código do colaborador
 * @param documentNumber Número do documento/nota
 * @param nfeKey Chave NFE do documento
 * @param issueDate Data de emissão
 * @param observation Observação do documento
 * @param newSaleNumber Número da nova venda extraído
 * @param newSaleNfeKey Chave NFE da venda extraída
 * @param createdAt Data de criação
 * @param updatedAt Data de atualização
 */
public record ExchangeResponse(
    UUID id,
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
    String newSaleNfeKey,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

