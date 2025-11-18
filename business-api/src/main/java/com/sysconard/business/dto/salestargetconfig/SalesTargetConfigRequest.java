package com.sysconard.business.dto.salestargetconfig;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Record para requisição de criação de configuração de metas e comissões.
 * Usado no Business API (Java 17) - DTOs simples usam Records.
 * 
 * @param storeCode Código da loja (6 caracteres)
 * @param competenceDate Data de competência no formato MM/YYYY
 * @param storeSalesTarget Meta de venda da loja
 * @param collectiveCommissionPercentage Percentual de comissão coletiva (gerente)
 * @param individualSalesTarget Meta de venda individual (vendedores)
 * @param individualCommissionPercentage Percentual de comissão individual (vendedores)
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
public record SalesTargetConfigRequest(
    @NotBlank(message = "Código da loja é obrigatório")
    @Size(min = 6, max = 6, message = "Código da loja deve ter exatamente 6 caracteres")
    String storeCode,
    
    @NotBlank(message = "Data de competência é obrigatória")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$", message = "Data de competência deve estar no formato MM/YYYY (ex: 01/2024)")
    String competenceDate,
    
    @NotNull(message = "Meta de venda da loja é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Meta de venda da loja deve ser maior ou igual a zero")
    BigDecimal storeSalesTarget,
    
    @NotNull(message = "Percentual de comissão coletiva é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentual de comissão coletiva deve ser maior ou igual a zero")
    BigDecimal collectiveCommissionPercentage,
    
    @NotNull(message = "Meta de venda individual é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Meta de venda individual deve ser maior ou igual a zero")
    BigDecimal individualSalesTarget,
    
    @NotNull(message = "Percentual de comissão individual é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentual de comissão individual deve ser maior ou igual a zero")
    BigDecimal individualCommissionPercentage
) {
    /**
     * Construtor compacto com validações adicionais
     */
    public SalesTargetConfigRequest {
        if (storeCode != null) {
            storeCode = storeCode.trim();
        }
        if (competenceDate != null) {
            competenceDate = competenceDate.trim();
        }
    }
}

