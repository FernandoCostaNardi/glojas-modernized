package com.sysconard.legacy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sysconard.legacy.dto.CriticalStockPageResponse;
import com.sysconard.legacy.service.CriticalStockControllerService;

/**
 * Controller REST para estoque crítico.
 * Identifica produtos com estoque menor que a média de vendas mensal.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/critical-stock")
@RequiredArgsConstructor
public class CriticalStockController {
    
    private final CriticalStockControllerService criticalStockControllerService;
    
    /**
     * Busca produtos com estoque crítico.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param page Número da página (base 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação (padrão: diferenca)
     * @param sortDir Direção da ordenação (padrão: desc)
     * @return Resposta paginada com itens de estoque crítico
     */
    @GetMapping
    public ResponseEntity<CriticalStockPageResponse> getCriticalStock(
            @RequestParam(required = false) String refplu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "diferenca") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("GET /critical-stock - refplu={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, page, size, sortBy, sortDir);
        
        try {
            CriticalStockPageResponse response = criticalStockControllerService.getCriticalStock(
                refplu, page, size, sortBy, sortDir
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Requisição inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao processar requisição de estoque crítico", e);
            return ResponseEntity.status(500).build();
        }
    }
}

