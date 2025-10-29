package com.sysconard.business.dto.sync;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO de requisição para sincronização de vendas anuais.
 * Contém o ano para processamento das vendas mensais agregadas.
 * 
 * @param year Ano para sincronização (obrigatório, entre 2000 e ano atual + 1)
 * 
 * @author Business API
 * @version 1.0
 */
@Builder
public record YearlySalesSyncRequest(
    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano deve ser maior ou igual a 2000")
    @Max(value = 2030, message = "Ano deve ser menor ou igual a 2030")
    Integer year
) {
    /**
     * Construtor compacto com validações adicionais
     */
    public YearlySalesSyncRequest {
        Objects.requireNonNull(year, "Ano não pode ser nulo");
        
        int currentYear = LocalDate.now().getYear();
        if (year < 2000) {
            throw new IllegalArgumentException("Ano deve ser maior ou igual a 2000");
        }
        if (year > currentYear + 1) {
            throw new IllegalArgumentException("Ano deve ser menor ou igual a " + (currentYear + 1));
        }
    }
}
