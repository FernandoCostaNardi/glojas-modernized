package com.sysconard.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sysconard.business.dto.CriticalStockPageResponseDTO;
import com.sysconard.business.dto.CriticalStockSearchRequest;
import com.sysconard.business.service.CriticalStockService;

/**
 * Controller REST para estoque crítico.
 * Requer permissão buy:read para acesso.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/critical-stock")
@RequiredArgsConstructor
@Validated
public class CriticalStockController {
    
    private final CriticalStockService criticalStockService;
    
    /**
     * Busca produtos com estoque crítico.
     * Requer permissão buy:read.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param descricao Filtro opcional por descrição (busca também em grupo e marca)
     * @param grupo Filtro opcional por grupo
     * @param marca Filtro opcional por marca
     * @param page Número da página (base 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação (padrão: diferenca)
     * @param sortDir Direção da ordenação (padrão: desc)
     * @return Resposta paginada com itens de estoque crítico
     */
    @GetMapping
    @PreAuthorize("hasAuthority('buy:read')")
    public ResponseEntity<CriticalStockPageResponseDTO> getCriticalStock(
            @RequestParam(required = false) String refplu,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) String marca,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "diferenca") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("GET /api/v1/critical-stock - refplu={}, descricao={}, grupo={}, marca={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, descricao, grupo, marca, page, size, sortBy, sortDir);
        
        try {
            // Criar request validado
            CriticalStockSearchRequest request = new CriticalStockSearchRequest(
                refplu, descricao, grupo, marca, page, size, sortBy, sortDir
            );
            
            // Buscar dados
            CriticalStockPageResponseDTO response = criticalStockService.getCriticalStock(request);
            
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

