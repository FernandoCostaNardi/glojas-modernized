package com.sysconard.business.dto.sync;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO de resposta para sincronização de vendas mensais.
 * Contém estatísticas do processamento realizado.
 * 
 * @param created Número de registros criados
 * @param updated Número de registros atualizados
 * @param processedAt Timestamp do processamento
 * @param startDate Data de início formatada (String)
 * @param endDate Data de fim formatada (String)
 * @param storesProcessed Número de lojas processadas
 * @param monthsProcessed Número de meses processados
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record MonthlySalesSyncResponse(
    int created,
    int updated,
    LocalDateTime processedAt,
    String startDate,
    String endDate,
    int storesProcessed,
    int monthsProcessed
) {
    /**
     * Construtor compacto com validações
     */
    public MonthlySalesSyncResponse {
        // Garantir que valores não sejam negativos
        if (created < 0) {
            throw new IllegalArgumentException("Número de registros criados não pode ser negativo");
        }
        if (updated < 0) {
            throw new IllegalArgumentException("Número de registros atualizados não pode ser negativo");
        }
        if (storesProcessed < 0) {
            throw new IllegalArgumentException("Número de lojas processadas não pode ser negativo");
        }
        if (monthsProcessed < 0) {
            throw new IllegalArgumentException("Número de meses processados não pode ser negativo");
        }
    }
}
