package com.sysconard.business.dto.email;

import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.sync.DailySalesSyncResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para encapsular dados do email de vendas diárias.
 * Contém todas as informações necessárias para gerar o relatório por email.
 * 
 * @param date Data do relatório de vendas
 * @param salesData Lista de vendas agregadas por loja
 * @param syncStats Estatísticas da sincronização executada
 * 
 * @author Business API
 * @version 1.0
 */
public record DailySalesEmailData(
    LocalDate date,
    List<DailySalesReportResponse> salesData,
    DailySalesSyncResponse syncStats
) {
    
    /**
     * Construtor compacto com validações básicas
     */
    public DailySalesEmailData {
        if (date == null) {
            throw new IllegalArgumentException("Data do relatório não pode ser nula");
        }
        if (salesData == null) {
            throw new IllegalArgumentException("Dados de vendas não podem ser nulos");
        }
        if (syncStats == null) {
            throw new IllegalArgumentException("Estatísticas de sincronização não podem ser nulas");
        }
    }
    
    /**
     * Verifica se há dados de vendas para enviar
     * @return true se há dados de vendas, false caso contrário
     */
    public boolean hasSalesData() {
        return !salesData.isEmpty();
    }
    
    /**
     * Obtém o total de lojas com vendas
     * @return Número de lojas com dados de vendas
     */
    public int getStoreCount() {
        return salesData.size();
    }
}
