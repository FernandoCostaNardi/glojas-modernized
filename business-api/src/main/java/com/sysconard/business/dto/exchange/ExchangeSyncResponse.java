package com.sysconard.business.dto.exchange;

import java.time.LocalDateTime;

/**
 * Record para resposta de sincronização de trocas.
 * Contém estatísticas do processo de sincronização.
 * 
 * @param created Quantidade de trocas criadas
 * @param updated Quantidade de trocas atualizadas
 * @param skipped Quantidade de trocas ignoradas (erros ou duplicatas)
 * @param processedAt Data e hora do processamento
 * @param startDate Data de início do período processado
 * @param endDate Data de fim do período processado
 */
public record ExchangeSyncResponse(
    int created,
    int updated,
    int skipped,
    LocalDateTime processedAt,
    String startDate,
    String endDate
) {
}

