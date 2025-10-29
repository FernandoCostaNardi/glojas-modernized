package com.sysconard.business.dto.emailnotifier;

/**
 * DTO para atualização de EmailNotifier
 * Record simples para DTOs com poucos campos (≤5)
 * Seguindo princípios de Clean Code com responsabilidade única
 */
public record UpdateEmailNotifierRequest(
    
    /**
     * Flag para notificações de vendas diárias
     */
    boolean dailySellNotifier,
    
    /**
     * Flag para notificações de vendas mensais
     */
    boolean dailyMonthNotifier,
    
    /**
     * Flag para notificações de vendas anuais
     */
    boolean monthYearNotifier
) {
}
