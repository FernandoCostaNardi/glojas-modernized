package com.sysconard.business.dto.emailnotifier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para criação de EmailNotifier
 * Record simples para DTOs com poucos campos (≤5)
 * Seguindo princípios de Clean Code com responsabilidade única
 */
public record CreateEmailNotifierRequest(
    
    /**
     * Endereço de email para notificações
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    String email,
    
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
    
    /**
     * Construtor compacto para validações adicionais
     */
    public CreateEmailNotifierRequest {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}
