package com.sysconard.legacy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sysconard.legacy.dto.PurchaseAnalysisPageResponse;
import com.sysconard.legacy.service.PurchaseAnalysisControllerService;

/**
 * Controller REST para análise de compras.
 * Endpoints para consulta de dados de vendas, estoque, custos e preços.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/purchase-analysis")
@RequiredArgsConstructor
public class PurchaseAnalysisController {
    
    private final PurchaseAnalysisControllerService purchaseAnalysisControllerService;
    
    /**
     * Busca análise de compras com filtros, paginação e ordenação.
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
    public ResponseEntity<PurchaseAnalysisPageResponse> getPurchaseAnalysis(
            @RequestParam(required = false) String refplu,
            @RequestParam(required = false, defaultValue = "true") Boolean hideNoSales,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "refplu") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("GET /purchase-analysis - refplu={}, hideNoSales={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, hideNoSales, page, size, sortBy, sortDir);
        
        try {
            PurchaseAnalysisPageResponse response = purchaseAnalysisControllerService.getPurchaseAnalysis(
                refplu, hideNoSales, page, size, sortBy, sortDir
            );
            
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

