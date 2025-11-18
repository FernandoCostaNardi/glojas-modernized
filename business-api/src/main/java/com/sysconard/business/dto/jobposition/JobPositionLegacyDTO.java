package com.sysconard.business.dto.jobposition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para receber dados de JobPosition da Legacy API.
 * Utilizado para deserializar respostas da Legacy API.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPositionLegacyDTO {
    
    /**
     * Código único do cargo formatado com 6 dígitos (ex: 000001)
     */
    private String id;
    
    /**
     * Descrição do cargo
     */
    private String description;
}

