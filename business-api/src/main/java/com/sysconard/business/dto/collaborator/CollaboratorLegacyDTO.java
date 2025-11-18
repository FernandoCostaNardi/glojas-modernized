package com.sysconard.business.dto.collaborator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO para receber dados de Collaborator da Legacy API.
 * Utilizado para deserializar respostas da Legacy API.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorLegacyDTO {
    
    /**
     * Código único do colaborador formatado com 6 dígitos (ex: 000001)
     */
    private String id;
    
    /**
     * Código do cargo do colaborador
     */
    private String jobPositionCode;
    
    /**
     * Código da loja onde o colaborador trabalha
     */
    private String storeCode;
    
    /**
     * Nome do colaborador
     */
    private String name;
    
    /**
     * Data de nascimento do colaborador
     */
    private Date birthDate;
    
    /**
     * Percentual de comissão do colaborador
     */
    private BigDecimal commissionPercentage;
    
    /**
     * Email do colaborador
     */
    private String email;
    
    /**
     * Status ativo do colaborador (S/N)
     */
    private String active;
    
    /**
     * Sexo do colaborador
     */
    private String gender;
}

