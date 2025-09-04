package com.sysconard.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.Set;

/**
 * DTO para requisição de atualização de usuário.
 * Não inclui password - deve ser alterado através de endpoint específico.
 * Utiliza Lombok para reduzir boilerplate mantendo validações Bean Validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;
    
    @Size(max = 255, message = "URL da imagem de perfil deve ter no máximo 255 caracteres")
    private String profileImageUrl;
    
    @NotNull(message = "Status ativo é obrigatório")
    private Boolean isActive;
    
    @NotNull(message = "Status de bloqueio é obrigatório")
    private Boolean isNotLocked;
    
    @NotEmpty(message = "Pelo menos uma role deve ser especificada")
    private Set<String> roles;
    
    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", isActive=" + isActive +
                ", isNotLocked=" + isNotLocked +
                ", roles=" + roles +
                '}';
    }
}
