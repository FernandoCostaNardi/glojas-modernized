package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO para funcionários do sistema
 * 
 * Segue os princípios de Clean Code:
 * - Uso do Lombok para reduzir boilerplate
 * - Builder pattern automático
 * - Getters/setters automáticos
 * - Construtores automáticos
 * - Documentação clara
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    
    /**
     * Código único do funcionário formatado com 6 dígitos (ex: 000001)
     */
    private String id;
    
    /**
     * Código do cargo do funcionário
     */
    private String jobPositionCode;
    
    /**
     * Código da loja onde o funcionário trabalha
     */
    private String storeCode;
    
    /**
     * Nome do funcionário
     */
    private String name;
    
    /**
     * Data de nascimento do funcionário
     */
    private Date birthDate;
    
    /**
     * Percentual de comissão do funcionário
     */
    private BigDecimal commissionPercentage;
    
    /**
     * Email do funcionário
     */
    private String email;
    
    /**
     * Status ativo do funcionário (S/N)
     */
    private String active;
    
    /**
     * Sexo do funcionário
     */
    private String gender;
}

