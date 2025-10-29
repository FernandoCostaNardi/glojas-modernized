package com.sysconard.business.controller;

import com.sysconard.business.dto.dashboard.DashboardSummaryResponse;
import com.sysconard.business.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para operações do dashboard.
 * Fornece endpoints para métricas e resumos da página inicial.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * Endpoint para obter resumo completo do dashboard.
     * Retorna métricas consolidadas de vendas e contagem de lojas ativas.
     * Dados sempre frescos sem cache para garantir informações atualizadas.
     * 
     * @return Resumo consolidado do dashboard
     */
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        
        log.info("Recebida solicitação de resumo do dashboard");
        
        try {
            DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
            
            log.info("Resumo do dashboard processado com sucesso: vendas hoje={}, mês={}, ano={}, lojas={}", 
                    summary.totalSalesToday(), summary.totalSalesMonth(), 
                    summary.totalSalesYear(), summary.activeStoresCount());
            
            // Configurar headers anti-cache para garantir dados sempre frescos
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(summary);
            
        } catch (Exception e) {
            log.error("Erro ao processar resumo do dashboard: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
}
