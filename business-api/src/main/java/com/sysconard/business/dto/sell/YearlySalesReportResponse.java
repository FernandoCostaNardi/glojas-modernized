package com.sysconard.business.dto.sell;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO de resposta para relatório de vendas anuais.
 * Contém dados agregados de vendas por loja com percentuais de participação.
 * 
 * @param storeName Nome da loja
 * @param total Valor total das vendas anuais
 * @param percentageOfTotal Percentual de participação no total geral
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record YearlySalesReportResponse(
    @NotNull(message = "Nome da loja é obrigatório")
    String storeName,
    
    @NotNull(message = "Total é obrigatório")
    @DecimalMin(value = "0.0", message = "Total não pode ser negativo")
    BigDecimal total,
    
    @NotNull(message = "Percentual é obrigatório")
    @DecimalMin(value = "0.0", message = "Percentual não pode ser negativo")
    BigDecimal percentageOfTotal
) {
    /**
     * Construtor compacto com validações
     */
    public YearlySalesReportResponse {
        Objects.requireNonNull(storeName, "Nome da loja não pode ser nulo");
        Objects.requireNonNull(total, "Total não pode ser nulo");
        Objects.requireNonNull(percentageOfTotal, "Percentual não pode ser nulo");
        
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total não pode ser negativo");
        }
        if (percentageOfTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Percentual não pode ser negativo");
        }
    }
}
