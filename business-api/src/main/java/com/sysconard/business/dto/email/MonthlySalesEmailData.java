package com.sysconard.business.dto.email;

import com.sysconard.business.dto.sell.MonthlySalesReportResponse;
import com.sysconard.business.dto.sync.MonthlySalesSyncResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DTO para dados de email de vendas mensais.
 * Contém informações necessárias para gerar e enviar emails de relatório mensal.
 * 
 * @author Business API
 * @version 1.0
 */
public record MonthlySalesEmailData(
    /**
     * Data de início do período
     */
    LocalDate startDate,
    
    /**
     * Data de fim do período
     */
    LocalDate endDate,
    
    /**
     * Nome do mês formatado (ex: "Outubro 2024")
     */
    String monthName,
    
    /**
     * Indica se é fechamento do mês (dia 1) ou atualização progressiva
     */
    boolean isMonthClosure,
    
    /**
     * Dados de vendas mensais por loja
     */
    List<MonthlySalesReportResponse> salesData,
    
    /**
     * Resposta da sincronização (opcional)
     */
    Optional<MonthlySalesSyncResponse> syncResponse
) {
    
    /**
     * Construtor com validações básicas
     */
    public MonthlySalesEmailData {
        if (startDate == null) {
            throw new IllegalArgumentException("Data de início não pode ser nula");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Data de fim não pode ser nula");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
        }
        if (monthName == null || monthName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do mês não pode ser nulo ou vazio");
        }
        if (salesData == null) {
            throw new IllegalArgumentException("Dados de vendas não podem ser nulos");
        }
        if (syncResponse == null) {
            syncResponse = Optional.empty();
        }
    }
    
    /**
     * Calcula o total geral de vendas
     * @return Soma de todos os totais das lojas
     */
    public double getGrandTotal() {
        return salesData.stream()
                .mapToDouble(sale -> sale.total().doubleValue())
                .sum();
    }
    
    /**
     * Retorna o número de lojas com vendas
     * @return Quantidade de lojas
     */
    public int getStoreCount() {
        return salesData.size();
    }
    
    /**
     * Verifica se há dados para exibir
     * @return true se há dados de vendas
     */
    public boolean hasData() {
        return !salesData.isEmpty();
    }
}
