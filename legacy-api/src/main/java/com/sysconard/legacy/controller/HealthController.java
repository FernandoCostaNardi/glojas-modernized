package com.sysconard.legacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller básico para health check e informações da API
 * 
 * Endpoints de verificação de status e conectividade
 */
@RestController
@RequestMapping("/")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Endpoint básico para verificar se a API está funcionando
     * 
     * @return Informações básicas da aplicação
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Legacy API - Sistema Glojas");
        response.put("version", "1.0.0");
        response.put("status", "RUNNING");
        response.put("timestamp", LocalDateTime.now());
        response.put("port", 8082);
        response.put("contextPath", "/api/legacy");
        response.put("description", "API para integração com SQL Server existente");
        return ResponseEntity.ok(response);
    }


    /**
     * Endpoint para verificar status da aplicação e conectividade com banco
     * 
     * @return Status detalhado incluindo conexão com SQL Server
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Testa conexão com banco
            try (Connection connection = dataSource.getConnection()) {
                response.put("database", "CONNECTED");
                response.put("databaseUrl", connection.getMetaData().getURL());
                response.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
                response.put("databaseVersion", connection.getMetaData().getDatabaseProductVersion());
                response.put("driverName", connection.getMetaData().getDriverName());
                response.put("readOnly", connection.isReadOnly());
            }
        } catch (Exception e) {
            response.put("database", "ERROR");
            response.put("databaseError", e.getMessage());
        }
        
        response.put("application", "Legacy API (Read-Only)");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("uptime", "Running");
        response.put("port", 8082);
        response.put("mode", "READ_ONLY");
        response.put("readOnlyMode", true);
        response.put("writeOperations", "BLOCKED");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para informações sobre os endpoints disponíveis
     * 
     * @return Lista de endpoints disponíveis
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Legacy API - Sistema Glojas");
        response.put("description", "API para acesso read-only ao SQL Server existente");
        response.put("version", "1.0.0");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /", "Informações básicas da API");
        endpoints.put("GET /status", "Status detalhado + conexão banco");
        endpoints.put("GET /info", "Informações dos endpoints");
        endpoints.put("GET /actuator/health", "Spring Actuator health check");
        
        response.put("availableEndpoints", endpoints);
        response.put("futureEndpoints", new String[]{
            "GET /produtos - Lista produtos",
            "GET /produtos/{id} - Busca produto específico",
            "GET /produtos/search - Filtrar produtos"
        });
        
        return ResponseEntity.ok(response);
    }
}
