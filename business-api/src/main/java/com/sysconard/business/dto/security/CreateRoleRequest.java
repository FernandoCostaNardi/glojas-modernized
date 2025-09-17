package com.sysconard.business.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.Set;
import java.util.UUID;

/**
 * DTO para requisição de criação de role.
 * Utiliza Lombok para reduzir boilerplate mantendo validações Bean Validation.
 * Contém apenas os campos necessários para criação de uma nova role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {
    
    /**
     * Nome da role (ex: ADMIN, USER, MANAGER)
     */
    @NotBlank(message = "Nome da role é obrigatório")
    @Size(min = 2, max = 50, message = "Nome da role deve ter entre 2 e 50 caracteres")
    @Pattern(regexp = "^[A-Z_]+$", message = "Nome da role deve conter apenas letras maiúsculas e underscore")
    private String name;
    
    /**
     * Descrição da role
     */
    @NotBlank(message = "Descrição da role é obrigatória")
    @Size(min = 5, max = 255, message = "Descrição da role deve ter entre 5 e 255 caracteres")
    private String description;
    
    /**
     * IDs das permissões associadas à role
     */
    @NotEmpty(message = "Pelo menos uma permissão deve ser especificada")
    private Set<UUID> permissionIds;
    
    @Override
    public String toString() {
        return "CreateRoleRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", permissionIds=" + permissionIds +
                '}';
    }
}
