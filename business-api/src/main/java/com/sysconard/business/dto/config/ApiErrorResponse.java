package com.sysconard.business.dto.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para resposta de erro da API.
 * Utiliza Lombok para reduzir boilerplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private List<String> details;
    private Map<String, String> fieldErrors;
    
    /**
     * Construtor para erro básico
     */
    public ApiErrorResponse(String status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Construtor para erro com detalhes
     */
    public ApiErrorResponse(String status, String message, String path, List<String> details) {
        this(status, message, path);
        this.details = details;
    }
    
    /**
     * Construtor para erro com erros de campo
     */
    public ApiErrorResponse(String status, String message, String path, Map<String, String> fieldErrors) {
        this(status, message, path);
        this.fieldErrors = fieldErrors;
    }
}
