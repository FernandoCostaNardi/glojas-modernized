package com.sysconard.business.dto.exchange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Record para requisição de criação de troca.
 * Contém os campos necessários para criar uma troca manualmente.
 * 
 * @param documentCode Código do documento (obrigatório)
 * @param storeCode Código da loja (obrigatório)
 * @param operationCode Código da operação (obrigatório)
 * @param originCode Código de origem (obrigatório)
 * @param employeeCode Código do colaborador (obrigatório)
 * @param documentNumber Número do documento/nota (opcional)
 * @param nfeKey Chave NFE do documento (opcional)
 * @param issueDate Data de emissão (obrigatório)
 * @param observation Observação do documento (opcional)
 * @param newSaleNumber Número da nova venda (opcional)
 * @param newSaleNfeKey Chave NFE da venda (opcional)
 */
public record CreateExchangeRequest(
    @NotBlank(message = "Código do documento é obrigatório")
    String documentCode,
    
    @NotBlank(message = "Código da loja é obrigatório")
    String storeCode,
    
    @NotBlank(message = "Código da operação é obrigatório")
    String operationCode,
    
    @NotBlank(message = "Código de origem é obrigatório")
    String originCode,
    
    @NotBlank(message = "Código do colaborador é obrigatório")
    String employeeCode,
    
    String documentNumber,
    String nfeKey,
    
    @NotNull(message = "Data de emissão é obrigatória")
    LocalDateTime issueDate,
    
    String observation,
    String newSaleNumber,
    String newSaleNfeKey
) {
}

