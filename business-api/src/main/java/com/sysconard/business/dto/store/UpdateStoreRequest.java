package com.sysconard.business.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requisição para atualização de uma loja existente.
 * Contém os dados que podem ser atualizados em uma loja.
 * O código não pode ser alterado após a criação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreRequest {
    
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
     * Permite ativar/desativar lojas sem removê-las do sistema.
     */
    private Boolean status;
}
