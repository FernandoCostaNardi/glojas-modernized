package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * DTO para requisição de alteração de senha.
 * Utiliza Lombok para reduzir boilerplate mantendo validações Bean Validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    
    @NotBlank(message = "Senha atual é obrigatória")
    private String currentPassword;
    
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, max = 100, message = "Nova senha deve ter entre 6 e 100 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$", 
             message = "Nova senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial")
    private String newPassword;
    
    @NotBlank(message = "Confirmação da nova senha é obrigatória")
    private String confirmNewPassword;
    
    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "currentPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                ", confirmNewPassword='[PROTECTED]'" +
                '}';
    }
}
