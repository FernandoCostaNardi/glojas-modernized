package com.sysconard.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sysconard.business.dto.PurchaseAnalysisPageResponseDTO;
import com.sysconard.business.dto.PurchaseAnalysisSearchRequest;
import com.sysconard.business.service.PurchaseAnalysisService;

/**
 * Controller REST para análise de compras.
 * Requer permissão buy:read para acesso.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/purchase-analysis")
@RequiredArgsConstructor
@Validated
public class PurchaseAnalysisController {
    
    private final PurchaseAnalysisService purchaseAnalysisService;
    
    /**
     * Busca análise de compras com filtros, paginação e ordenação.
     * Requer permissão buy:read.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param hideNoSales Ocultar produtos sem vendas nos últimos 90 dias (padrão true)
     * @param page Número da página (base 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     * @return Resposta paginada com itens de análise de compras
     */
    @GetMapping
    @PreAuthorize("hasAuthority('buy:read')")
    public ResponseEntity<PurchaseAnalysisPageResponseDTO> getPurchaseAnalysis(
            @RequestParam(required = false) String refplu,
            @RequestParam(required = false) Boolean hideNoSales,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "refplu") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("GET /api/v1/purchase-analysis - refplu={}, hideNoSales={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, hideNoSales, page, size, sortBy, sortDir);
        
        try {
            // Criar request validado
            PurchaseAnalysisSearchRequest request = new PurchaseAnalysisSearchRequest(
                refplu, hideNoSales, page, size, sortBy, sortDir
            );
            
            // Buscar dados
            PurchaseAnalysisPageResponseDTO response = purchaseAnalysisService.getPurchaseAnalysis(request);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Requisição inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao processar requisição de análise de compras", e);
            return ResponseEntity.status(500).build();
        }
    }
}

