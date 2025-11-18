package com.sysconard.business.dto.jobposition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para JobPosition.
 * Utilizado para retornar dados de cargos nas respostas da API.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPositionResponse {
    
    /**
     * Identificador único do cargo (UUID)
     */
    private UUID id;
    
    /**
     * Código único do cargo da Legacy API
     */
    private String jobPositionCode;
    
    /**
     * Descrição do cargo
     */
    private String jobPositionDescription;
    
    /**
     * Data e hora de criação do registro
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do registro
     */
    private LocalDateTime updatedAt;
}

