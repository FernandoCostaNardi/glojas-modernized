package com.sysconard.business.dto.sync;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Record para resposta da sincronização de vendas diárias.
 * Contém as estatísticas do processo de sincronização executado.
 * 
 * @param created Número de registros criados durante a sincronização
 * @param updated Número de registros atualizados durante a sincronização
 * @param processedAt Data e hora em que o processamento foi concluído
 * @param startDate Data de início do período processado
 * @param endDate Data de fim do período processado
 * @param storesProcessed Número de lojas processadas na sincronização
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record DailySalesSyncResponse(
    int created,
    int updated,
    LocalDateTime processedAt,
    String startDate,
    String endDate,
    int storesProcessed
) {
    /**
     * Construtor compacto com validações e inicializações.
     * Garante que os valores numéricos não sejam negativos.
     */
    public DailySalesSyncResponse {
        if (created < 0) {
            throw new IllegalArgumentException("Número de registros criados não pode ser negativo");
        }
        
        if (updated < 0) {
            throw new IllegalArgumentException("Número de registros atualizados não pode ser negativo");
        }
        
        if (storesProcessed < 0) {
            throw new IllegalArgumentException("Número de lojas processadas não pode ser negativo");
        }
        
        // Definir processedAt como agora se não foi fornecido
        processedAt = processedAt != null ? processedAt : LocalDateTime.now();
    }
    
    /**
     * Calcula o total de registros processados (criados + atualizados).
     * 
     * @return Total de registros processados na sincronização
     */
    public int getTotalProcessed() {
        return created + updated;
    }
}
