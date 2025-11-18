package com.sysconard.business.dto.collaborator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para Collaborator.
 * Utilizado para retornar dados de colaboradores nas respostas da API.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorResponse {
    
    /**
     * Identificador único do colaborador (UUID)
     */
    private UUID id;
    
    /**
     * Código único do colaborador da Legacy API
     */
    private String employeeCode;
    
    /**
     * Código do cargo do colaborador
     */
    private String jobPositionCode;
    
    /**
     * Código da loja onde o colaborador trabalha
     */
    private String storeCode;
    
    /**
     * Nome completo do colaborador
     */
    private String name;
    
    /**
     * Data de nascimento do colaborador
     */
    private LocalDate birthDate;
    
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
    
    /**
     * Data e hora de criação do registro
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do registro
     */
    private LocalDateTime updatedAt;
}

