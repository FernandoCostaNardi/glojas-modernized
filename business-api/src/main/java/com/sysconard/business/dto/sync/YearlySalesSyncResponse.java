package com.sysconard.business.dto.sync;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO de resposta para sincronização de vendas anuais.
 * Contém estatísticas do processamento realizado.
 * 
 * @param created Número de registros criados
 * @param updated Número de registros atualizados
 * @param processedAt Timestamp do processamento
 * @param year Ano processado
 * @param storesProcessed Número de lojas processadas
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record YearlySalesSyncResponse(
    @Min(value = 0, message = "Número de registros criados não pode ser negativo")
    int created,
    
    @Min(value = 0, message = "Número de registros atualizados não pode ser negativo")
    int updated,
    
    @NotNull(message = "Timestamp de processamento é obrigatório")
    LocalDateTime processedAt,
    
    @NotNull(message = "Ano processado é obrigatório")
    Integer year,
    
    @Min(value = 0, message = "Número de lojas processadas não pode ser negativo")
    int storesProcessed
) {
    /**
     * Construtor compacto com validações
     */
    public YearlySalesSyncResponse {
        Objects.requireNonNull(processedAt, "Timestamp de processamento não pode ser nulo");
        Objects.requireNonNull(year, "Ano processado não pode ser nulo");
        
        if (created < 0) {
            throw new IllegalArgumentException("Número de registros criados não pode ser negativo");
        }
        if (updated < 0) {
            throw new IllegalArgumentException("Número de registros atualizados não pode ser negativo");
        }
        if (storesProcessed < 0) {
            throw new IllegalArgumentException("Número de lojas processadas não pode ser negativo");
        }
    }
}
