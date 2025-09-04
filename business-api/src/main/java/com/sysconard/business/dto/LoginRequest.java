package com.sysconard.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login.
 * Utiliza Record para simplicidade e imutabilidade.
 */
public record LoginRequest(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,
    
    @NotBlank(message = "Senha é obrigatória")
    String password
) {
    // Construtor sem validações para evitar problemas de deserialização JSON
}
