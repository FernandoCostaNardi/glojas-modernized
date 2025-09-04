package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respostas de teste de conexão de produtos
 * 
 * Segue os princípios de Clean Code:
 * - Uso do Lombok para reduzir boilerplate
 * - Builder pattern automático
 * - Getters/setters automáticos
 * - Construtores automáticos
 * - Documentação clara
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductConnectionResponse {
    
    /**
     * Status da conexão (SUCCESS, ERROR)
     */
    private String status;
    
    /**
     * Mensagem descritiva
     */
    private String message;
    
    /**
     * Nome do banco de dados
     */
    private String database;
    
    /**
     * Total de produtos no banco
     */
    private long totalProducts;
    
    /**
     * Driver JDBC utilizado
     */
    private String driver;
    
    /**
     * Versão do Java
     */
    private String java;
    
    /**
     * Timestamp da resposta
     */
    private LocalDateTime timestamp;
    
    /**
     * Detalhes do erro (quando aplicável)
     */
    private String error;
}
