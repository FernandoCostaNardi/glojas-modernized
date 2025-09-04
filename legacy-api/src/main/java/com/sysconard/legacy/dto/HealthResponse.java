package com.sysconard.legacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para respostas de health check da Legacy API
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
public class HealthResponse {
    
    /**
     * Nome da aplicação
     */
    private String application;
    
    /**
     * Versão da aplicação
     */
    private String version;
    
    /**
     * Status da aplicação (RUNNING, ERROR, etc.)
     */
    private String status;
    
    /**
     * Timestamp da resposta
     */
    private LocalDateTime timestamp;
    
    /**
     * Porta da aplicação
     */
    private Integer port;
    
    /**
     * Context path da aplicação
     */
    private String contextPath;
    
    /**
     * Descrição da aplicação
     */
    private String description;
    
    /**
     * Informações do banco de dados
     */
    private Map<String, Object> database;
    
    /**
     * Endpoints disponíveis
     */
    private Map<String, String> availableEndpoints;
    
    /**
     * Endpoints futuros planejados
     */
    private String[] futureEndpoints;
}
