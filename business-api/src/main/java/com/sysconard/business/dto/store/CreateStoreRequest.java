package com.sysconard.business.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requisição para criação de uma nova loja.
 * Contém os dados necessários para criar uma loja no sistema.
 * Inclui validações para garantir a integridade dos dados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreRequest {
    
    /**
     * Código único da loja.
     * Deve ter exatamente 6 caracteres alfanuméricos.
     */
    @NotBlank(message = "Código da loja é obrigatório")
    @Size(min = 6, max = 6, message = "Código deve ter exatamente 6 caracteres")
    @Pattern(regexp = "^[A-Z0-9]{6}$", message = "Código deve conter apenas letras maiúsculas e números")
    private String code;
    
    /**
     * Nome da loja.
     * Representa o nome comercial ou fantasia da loja.
     */
    @NotBlank(message = "Nome da loja é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    private String name;
    
    /**
     * Cidade onde a loja está localizada.
     * Utilizado para organização geográfica e relatórios.
     */
    @NotBlank(message = "Cidade da loja é obrigatória")
    @Size(min = 2, max = 100, message = "Cidade deve ter entre 2 e 100 caracteres")
    private String city;
    
    /**
     * Status da loja.
     * Indica se a loja está ativa (true) ou inativa (false).
     * Por padrão, novas lojas são criadas como ativas.
     */
    @Builder.Default
    private boolean status = true;
}
