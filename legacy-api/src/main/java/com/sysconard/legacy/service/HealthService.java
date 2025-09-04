package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço responsável pela lógica de health check da Legacy API
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Service
public class HealthService {
    
    private final DataSource dataSource;
    
    @Autowired
    public HealthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * Obtém informações básicas da aplicação
     * 
     * @return HealthResponse com informações básicas
     */
    public HealthResponse getBasicInfo() {
        return HealthResponse.builder()
                .application("Legacy API - Sistema Glojas")
                .version("1.0.0")
                .status("RUNNING")
                .timestamp(LocalDateTime.now())
                .port(8082)
                .contextPath("/api/legacy")
                .description("API para integração com SQL Server existente")
                .build();
    }
    
    /**
     * Obtém status detalhado incluindo conectividade com banco
     * 
     * @return HealthResponse com status detalhado
     */
    public HealthResponse getDetailedStatus() {
        Map<String, Object> databaseInfo = checkDatabaseConnection();
        
        return HealthResponse.builder()
                .application("Legacy API (Read-Only)")
                .version("1.0.0")
                .status("RUNNING")
                .timestamp(LocalDateTime.now())
                .port(8082)
                .contextPath("/api/legacy")
                .description("API para acesso read-only ao SQL Server existente")
                .database(databaseInfo)
                .build();
    }
    
    /**
     * Obtém informações sobre endpoints disponíveis
     * 
     * @return HealthResponse com informações dos endpoints
     */
    public HealthResponse getEndpointsInfo() {
        Map<String, String> availableEndpoints = new HashMap<>();
        availableEndpoints.put("GET /", "Informações básicas da API");
        availableEndpoints.put("GET /status", "Status detalhado + conexão banco");
        availableEndpoints.put("GET /info", "Informações dos endpoints");
        availableEndpoints.put("GET /actuator/health", "Spring Actuator health check");
        
        String[] futureEndpoints = {
            "GET /produtos - Lista produtos",
            "GET /produtos/{id} - Busca produto específico",
            "GET /produtos/search - Filtrar produtos"
        };
        
        return HealthResponse.builder()
                .application("Legacy API - Sistema Glojas")
                .version("1.0.0")
                .status("RUNNING")
                .timestamp(LocalDateTime.now())
                .description("API para acesso read-only ao SQL Server existente")
                .availableEndpoints(availableEndpoints)
                .futureEndpoints(futureEndpoints)
                .build();
    }
    
    /**
     * Verifica a conexão com o banco de dados
     * 
     * @return Map com informações da conexão
     */
    private Map<String, Object> checkDatabaseConnection() {
        Map<String, Object> databaseInfo = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            databaseInfo.put("status", "CONNECTED");
            databaseInfo.put("url", connection.getMetaData().getURL());
            databaseInfo.put("product", connection.getMetaData().getDatabaseProductName());
            databaseInfo.put("version", connection.getMetaData().getDatabaseProductVersion());
            databaseInfo.put("driver", connection.getMetaData().getDriverName());
            databaseInfo.put("readOnly", connection.isReadOnly());
            databaseInfo.put("mode", "READ_ONLY");
            databaseInfo.put("writeOperations", "BLOCKED");
        } catch (Exception e) {
            databaseInfo.put("status", "ERROR");
            databaseInfo.put("error", e.getMessage());
            databaseInfo.put("mode", "READ_ONLY");
            databaseInfo.put("writeOperations", "BLOCKED");
        }
        
        return databaseInfo;
    }
}
