package com.sysconard.business.dto.operation;

import com.sysconard.business.enums.OperationSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de criação de operação.
 * Contém os dados necessários para criar uma nova operação no sistema.
 * Utiliza validações Bean Validation para garantir integridade dos dados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOperationRequest {
    
    /**
     * Código único da operação.
     * Deve ser único no sistema e seguir padrão alfanumérico.
     */
    @NotBlank(message = "Código da operação é obrigatório")
    @Size(min = 2, max = 50, message = "Código deve ter entre 2 e 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Código deve conter apenas letras maiúsculas, números, underscore e hífen")
    private String code;
    
    /**
     * Descrição da operação.
     * Deve fornecer informações claras sobre o propósito da operação.
     */
    @NotBlank(message = "Descrição da operação é obrigatória")
    @Size(min = 3, max = 255, message = "Descrição deve ter entre 3 e 255 caracteres")
    private String description;
    
    /**
     * Fonte da operação.
     * Define a origem da operação (SELL, EXCHANGE).
     */
    @NotNull(message = "Fonte da operação é obrigatória")
    private OperationSource operationSource;
}
