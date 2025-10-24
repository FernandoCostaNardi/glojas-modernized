package com.sysconard.legacy.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sysconard.legacy.dto.HealthResponse;
import com.sysconard.legacy.service.HealthService;

/**
 * Testes de integração para o HealthController
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@WebMvcTest(HealthController.class)
class HealthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private HealthService healthService;

    
    @Test
    void shouldReturnBasicInfoWhenCallingRootEndpoint() throws Exception {
        // Given
        HealthResponse expectedResponse = HealthResponse.builder()
                .application("Legacy API - Sistema Glojas")
                .version("1.0.0")
                .status("RUNNING")
                .timestamp(LocalDateTime.now())
                .port(8087)
                .contextPath("/api/legacy")
                .description("API para integração com SQL Server existente")
                .build();
        
        when(healthService.getBasicInfo()).thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(get("/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.application").value("Legacy API - Sistema Glojas"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.port").value(8087))
                .andExpect(jsonPath("$.contextPath").value("/api/legacy"))
                .andExpect(jsonPath("$.description").value("API para integração com SQL Server existente"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void shouldReturnDetailedStatusWhenCallingStatusEndpoint() throws Exception {
        // Given
        Map<String, Object> databaseInfo = new HashMap<>();
        databaseInfo.put("status", "CONNECTED");
        databaseInfo.put("url", "jdbc:sqlserver://localhost:1433;databaseName=glojas");
        databaseInfo.put("product", "Microsoft SQL Server");
        databaseInfo.put("mode", "READ_ONLY");
        databaseInfo.put("writeOperations", "BLOCKED");
        
        HealthResponse expectedResponse = HealthResponse.builder()
                .application("Legacy API (Read-Only)")
                .version("1.0.0")
                .status("RUNNING")
                .timestamp(LocalDateTime.now())
                .port(8087)
                .contextPath("/api/legacy")
                .description("API para acesso read-only ao SQL Server existente")
                .database(databaseInfo)
                .build();
        
        when(healthService.getDetailedStatus()).thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(get("/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.application").value("Legacy API (Read-Only)"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.database.status").value("CONNECTED"))
                .andExpect(jsonPath("$.database.url").value("jdbc:sqlserver://localhost:1433;databaseName=glojas"))
                .andExpect(jsonPath("$.database.product").value("Microsoft SQL Server"))
                .andExpect(jsonPath("$.database.mode").value("READ_ONLY"))
                .andExpect(jsonPath("$.database.writeOperations").value("BLOCKED"));
    }
    
    @Test
    void shouldReturnEndpointsInfoWhenCallingInfoEndpoint() throws Exception {
        // Given
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
        
        HealthResponse expectedResponse = HealthResponse.builder()
                .application("Legacy API - Sistema Glojas")
                .version("1.0.0")
                .status("RUNNING")
                .timestamp(LocalDateTime.now())
                .description("API para acesso read-only ao SQL Server existente")
                .availableEndpoints(availableEndpoints)
                .futureEndpoints(futureEndpoints)
                .build();
        
        when(healthService.getEndpointsInfo()).thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(get("/info")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.application").value("Legacy API - Sistema Glojas"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.availableEndpoints['GET /']").value("Informações básicas da API"))
                .andExpect(jsonPath("$.availableEndpoints['GET /status']").value("Status detalhado + conexão banco"))
                .andExpect(jsonPath("$.availableEndpoints['GET /info']").value("Informações dos endpoints"))
                .andExpect(jsonPath("$.availableEndpoints['GET /actuator/health']").value("Spring Actuator health check"))
                .andExpect(jsonPath("$.futureEndpoints[0]").value("GET /produtos - Lista produtos"))
                .andExpect(jsonPath("$.futureEndpoints[1]").value("GET /produtos/{id} - Busca produto específico"))
                .andExpect(jsonPath("$.futureEndpoints[2]").value("GET /produtos/search - Filtrar produtos"));
    }
}
