package com.sysconard.business.dto.operation;

import com.sysconard.business.enums.OperationSource;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de atualização de operação.
 * Contém os dados que podem ser atualizados em uma operação existente.
 * Todos os campos são opcionais para permitir atualizações parciais.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOperationRequest {
    
    /**
     * Novo código da operação.
     * Se fornecido, deve ser único no sistema e seguir padrão alfanumérico.
     */
    @Size(min = 2, max = 50, message = "Código deve ter entre 2 e 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Código deve conter apenas letras maiúsculas, números, underscore e hífen")
    private String code;
    
    /**
     * Nova descrição da operação.
     * Se fornecida, deve ter tamanho adequado para descrever a operação.
     */
    @Size(min = 3, max = 255, message = "Descrição deve ter entre 3 e 255 caracteres")
    private String description;
    
    /**
     * Nova fonte da operação.
     * Se fornecida, define a origem da operação (SELL, EXCHANGE).
     */
    private OperationSource operationSource;
}
