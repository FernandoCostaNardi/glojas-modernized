package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.HealthResponse;
import com.sysconard.legacy.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para health check e informações da Legacy API
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: apenas roteamento HTTP
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o serviço
 * - Documentação clara
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@RestController
@RequestMapping("/")
public class HealthController {

    private final HealthService healthService;
    
    @Autowired
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * Endpoint básico para verificar se a API está funcionando
     * 
     * @return Informações básicas da aplicação
     */
    @GetMapping("/")
    public ResponseEntity<HealthResponse> root() {
        HealthResponse response = healthService.getBasicInfo();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para verificar status da aplicação e conectividade com banco
     * 
     * @return Status detalhado incluindo conexão com SQL Server
     */
    @GetMapping("/status")
    public ResponseEntity<HealthResponse> status() {
        HealthResponse response = healthService.getDetailedStatus();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para informações sobre os endpoints disponíveis
     * 
     * @return Lista de endpoints disponíveis
     */
    @GetMapping("/info")
    public ResponseEntity<HealthResponse> info() {
        HealthResponse response = healthService.getEndpointsInfo();
        return ResponseEntity.ok(response);
    }
}
