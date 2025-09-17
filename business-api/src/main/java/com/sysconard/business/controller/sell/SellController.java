package com.sysconard.business.controller.sell;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sysconard.business.dto.sell.StoreReportRequest;
import com.sysconard.business.dto.sell.StoreReportResponse;
import com.sysconard.business.service.sell.SellService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller REST para operações relacionadas a vendas.
 * Fornece endpoints para relatórios de vendas consumindo dados da Legacy API.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Validated
public class SellController {
    
    private final SellService sellService;
    
    /**
     * Endpoint para obter relatório de vendas por loja.
     * Consome a Legacy API para retornar dados agregados de DANFE, PDV e TROCA3.
     * 
     * @param request DTO com todos os parâmetros necessários para o relatório
     * @return Lista de dados agregados por loja
     */
    @PostMapping("/store-report")
    @PreAuthorize("hasAuthority('sell:read')")
    public ResponseEntity<List<StoreReportResponse>> getStoreReport(
            @Valid @RequestBody StoreReportRequest request) {
        
        log.info("Recebida solicitação de relatório de vendas por loja: startDate={}, endDate={}, storeCodes={}", 
                request.startDate(), request.endDate(), request.storeCodes());
        
        try {
            List<StoreReportResponse> report = sellService.getStoreReport(request);
            
            log.info("Relatório de vendas processado com sucesso: {} lojas retornadas", report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Erro ao processar relatório de vendas: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
}
