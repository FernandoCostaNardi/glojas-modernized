package com.sysconard.business.dto.salestargetconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para SalesTargetConfig.
 * Utilizado para retornar dados de configurações de metas e comissões nas respostas da API.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTargetConfigResponse {
    
    /**
     * Identificador único da configuração (UUID)
     */
    private UUID id;
    
    /**
     * Código da loja
     */
    private String storeCode;
    
    /**
     * Data de competência no formato MM/YYYY
     */
    private String competenceDate;
    
    /**
     * Meta de venda da loja
     */
    private BigDecimal storeSalesTarget;
    
    /**
     * Percentual de comissão coletiva (gerente)
     */
    private BigDecimal collectiveCommissionPercentage;
    
    /**
     * Meta de venda individual (vendedores)
     */
    private BigDecimal individualSalesTarget;
    
    /**
     * Percentual de comissão individual (vendedores)
     */
    private BigDecimal individualCommissionPercentage;
    
    /**
     * Data e hora de criação do registro
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do registro
     */
    private LocalDateTime updatedAt;
}

