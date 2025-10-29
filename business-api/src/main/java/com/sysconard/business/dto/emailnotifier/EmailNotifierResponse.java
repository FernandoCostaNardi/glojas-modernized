package com.sysconard.business.dto.emailnotifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para EmailNotifier
 * Lombok para DTOs complexos (>5 campos) com auditoria
 * Seguindo princípios de Clean Code com responsabilidade única
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotifierResponse {
    
    /**
     * Identificador único do notificador
     */
    private UUID id;
    
    /**
     * Endereço de email para notificações
     */
    private String email;
    
    /**
     * Flag para notificações de vendas diárias
     */
    private boolean dailySellNotifier;
    
    /**
     * Flag para notificações de vendas mensais
     */
    private boolean dailyMonthNotifier;
    
    /**
     * Flag para notificações de vendas anuais
     */
    private boolean monthYearNotifier;
    
    /**
     * Data e hora de criação do registro
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do registro
     */
    private LocalDateTime updatedAt;
}
