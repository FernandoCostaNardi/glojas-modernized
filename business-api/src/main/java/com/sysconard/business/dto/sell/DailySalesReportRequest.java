package com.sysconard.business.dto.sell;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Record para requisição de relatório de vendas diárias.
 * Contém os parâmetros de data para filtrar o relatório.
 * 
 * @param startDate Data de início do período (obrigatório)
 * @param endDate Data de fim do período (obrigatório)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record DailySalesReportRequest(
    @NotNull(message = "Data de início é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,
    
    @NotNull(message = "Data de fim é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {
    /**
     * Construtor compacto com validações
     */
    public DailySalesReportRequest {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data de início deve ser anterior ou igual à data de fim");
        }
    }
}
