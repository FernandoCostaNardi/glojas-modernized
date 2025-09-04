package com.sysconard.legacy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sysconard.legacy.dto.HealthResponse;

/**
 * Testes unitários para o HealthService
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class HealthServiceTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
    @Mock
    private DatabaseMetaData databaseMetaData;
    
    private HealthService healthService;
    
    @BeforeEach
    void setUp() {
        healthService = new HealthService(dataSource);
    }
    
    @Test
    void shouldReturnBasicInfoSuccessfully() {
        // When
        HealthResponse response = healthService.getBasicInfo();
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getApplication()).isEqualTo("Legacy API - Sistema Glojas");
        assertThat(response.getVersion()).isEqualTo("1.0.0");
        assertThat(response.getStatus()).isEqualTo("RUNNING");
        assertThat(response.getPort()).isEqualTo(8082);
        assertThat(response.getContextPath()).isEqualTo("/api/legacy");
        assertThat(response.getDescription()).isEqualTo("API para integração com SQL Server existente");
        assertThat(response.getTimestamp()).isNotNull();
    }
    
    @Test
    void shouldReturnDetailedStatusWithDatabaseConnected() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getURL()).thenReturn("jdbc:sqlserver://localhost:1433;databaseName=glojas");
        when(databaseMetaData.getDatabaseProductName()).thenReturn("Microsoft SQL Server");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("15.0.2000.5");
        when(databaseMetaData.getDriverName()).thenReturn("Microsoft JDBC Driver 8.2 for SQL Server");
        when(connection.isReadOnly()).thenReturn(true);
        
        // When
        HealthResponse response = healthService.getDetailedStatus();
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getApplication()).isEqualTo("Legacy API (Read-Only)");
        assertThat(response.getDatabase()).isNotNull();
        assertThat(response.getDatabase().get("status")).isEqualTo("CONNECTED");
        assertThat(response.getDatabase().get("url")).isEqualTo("jdbc:sqlserver://localhost:1433;databaseName=glojas");
        assertThat(response.getDatabase().get("product")).isEqualTo("Microsoft SQL Server");
        assertThat(response.getDatabase().get("mode")).isEqualTo("READ_ONLY");
        assertThat(response.getDatabase().get("writeOperations")).isEqualTo("BLOCKED");
    }
    
    @Test
    void shouldReturnDetailedStatusWithDatabaseError() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));
        
        // When
        HealthResponse response = healthService.getDetailedStatus();
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDatabase()).isNotNull();
        assertThat(response.getDatabase().get("status")).isEqualTo("ERROR");
        assertThat(response.getDatabase().get("error")).isEqualTo("Connection failed");
        assertThat(response.getDatabase().get("mode")).isEqualTo("READ_ONLY");
        assertThat(response.getDatabase().get("writeOperations")).isEqualTo("BLOCKED");
    }
    
    @Test
    void shouldReturnEndpointsInfoSuccessfully() {
        // When
        HealthResponse response = healthService.getEndpointsInfo();
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getApplication()).isEqualTo("Legacy API - Sistema Glojas");
        assertThat(response.getAvailableEndpoints()).isNotNull();
        assertThat(response.getAvailableEndpoints()).hasSize(4);
        assertThat(response.getAvailableEndpoints().get("GET /")).isEqualTo("Informações básicas da API");
        assertThat(response.getAvailableEndpoints().get("GET /status")).isEqualTo("Status detalhado + conexão banco");
        assertThat(response.getAvailableEndpoints().get("GET /info")).isEqualTo("Informações dos endpoints");
        assertThat(response.getAvailableEndpoints().get("GET /actuator/health")).isEqualTo("Spring Actuator health check");
        
        assertThat(response.getFutureEndpoints()).isNotNull();
        assertThat(response.getFutureEndpoints()).hasSize(3);
        assertThat(response.getFutureEndpoints()[0]).isEqualTo("GET /produtos - Lista produtos");
        assertThat(response.getFutureEndpoints()[1]).isEqualTo("GET /produtos/{id} - Busca produto específico");
        assertThat(response.getFutureEndpoints()[2]).isEqualTo("GET /produtos/search - Filtrar produtos");
    }
}
